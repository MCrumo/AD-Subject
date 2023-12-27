const fs = require('fs');
const path = require('path');
const moment = require('moment');
const { filePath } = require('../config/config');
const { validationResult, param, body } = require('express-validator');
const { validateFileUpload } = require('../middleware/upload-utils');

const apiRouter = express.Router();

/********* POST **********/

/** 
 * POST method to register a new image 
 * @param {string} title 
 * @param {string} description 
 * @param {string} keywords      
 * @param {string} author 
 * @param {string} capt_date format yyyy-mm-dd, no pot ser posterior a la data d'avui
 * @param {File} image
 * @return
 */ 
apiRouter.post('/register-image',
    [
        body('title').notEmpty().isLength({ max: 256 }).withMessage('El camp title és obligatori i pot ocupar màxim 256 caràcters')
            .matches(/^\S*$/, '').withMessage('El camp title no pot contenir espais en blanc.'),
        body('description').notEmpty().isLength({ max: 1024 }).withMessage('El camp description és obligatori i pot ocupar màxim 1024 caràcters'),
        body('keywords').notEmpty().isLength({ max: 256 }).withMessage('El camp keywords és obligatori i pot ocupar màxim 256 caràcters'),
        body('author').notEmpty().isLength({ max: 256 }).withMessage('El camp author és obligatori i pot ocupar màxim 256 caràcters'),
        body('capt_date').notEmpty().isLength({ max: 10 }).withMessage('El camp capt_date pot ocupar màxim 10 caràcters')
            .custom((value, { req }) => {
                // Verifiquem el format de la data amb moment 'yyyy-mm-dd'
                if (!moment(value, 'YYYY-MM-DD', true).isValid()) throw new Error('El camp capt_date ha de tenir format "yyyy-mm-dd"');

                // Verifiquem que la data sigui anterior o igual a la data d'avui
                const today = moment().startOf('day');
                const captureDate = moment(value, 'YYYY-MM-DD').startOf('day');
                if (captureDate.isAfter(today)) throw new Error('El camp capt_date ha de ser ser igual o anterior a la data d\'avui');

                return true;
            }),
        validateFileUpload
    ], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message:  messages } });
            }

            const {title, description, keywords, author, capt_date} = req.body;

            dbConnection.execute('CALL insertImage(?,?,?,?,?,?,?,?)', [title, description, keywords, author, req.userData.id, capt_date, moment().format('YYYY-MM-DD'), ''], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }
                
                const last_id = result[0][0]['LAST_INSERT_ID()'];
                const extension = path.extname(req.files.image.name);
                const filename = last_id + '_' + title + extension;

                // Actualitzem el filename a l'entrada afegida
                dbConnection.execute('CALL updateFilename(?,?)', [last_id, filename], (err, result, fields) => {
                    if (err) {
                        console.error('Error de la Base de Dades:', err);
                        return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                    }
            
                    try {
                        let imatge = req.files.image;
            
                        fs.mkdirSync(filePath, { recursive: true });
                        let rutaImatge = path.join(filePath, filename);
                        imatge.mv(rutaImatge);
            
                    } catch (error) {
                        console.error('Error intern del servidor, image file:', error);
                        return res.status(500).json({ result: -11, data: { message: 'Error en guardar l\'arxiu' } });
                    }
            
                    return res.status(201).json({ result: 0, data: { message: 'Imatge afegida correctament' } });
                });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(500).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
    return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
  }
});

/**
 * POST method to delete an existing image
 * @param id
 * @return
 */
apiRouter.post('/delete',
    [
        body('id').notEmpty().isInt().toInt().withMessage('El camp id és obligatori i numèric'),
    ], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const id = req.body.id;
            dbConnection.execute('CALL deleteImage(?)', [id], (err, result, fields) => {
                if (err) { // Control d'errors: fallada de connexió amb la BD
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0][0]?.filename === undefined) return res.status(404).json({ result: -11, data: { message: 'No existeix la imatge' } });

                const fileRoute = path.join(filePath, result[0][0]?.filename);

                // Utilizem fs.unlink per eliminar l'arxiu
                if (fs.existsSync(fileRoute)) {
                    fs.unlink(fileRoute, (err) => {
                        if (err) {
                            console.error('Error en eliminar l\'arxiu:', err);
                            return res.status(500).json({ result: -12, data: { message: 'Error eliminant la imatge de disc' } });
                        }
                
                        // Retorn OK
                        return res.status(201).json({ result: 0, data: { message: 'Imatge eliminada correctament' } });
                    });
                } else {
                    // No existeix l'arxiu
                    console.log("Eliminat Ok pero arxiu imatge no trobat.")
                    return res.status(201).json({ result: 0, data: { message: 'Imatge eliminada correctament de la Base de Dades, no s\'ha trobat l\'arxiu amb nom: ' + result[0][0]?.filename } });
                }
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});

/**
* POST method to modify an existing image
* @param {int} id 
* @param {string} title 
* @param {string} description 
* @param {string} keywords      
* @param {string} author 
* @param {string} capt_date format yyyy-mm-dd, no pot ser posterior a la data d'avui
* @return
*/
apiRouter.post('/modify',
[
    body('id').notEmpty().isInt().toInt().withMessage('El camp id és obligatori i ha de ser un enter'),
    body('title').notEmpty().trim().isLength({ max: 256 }).withMessage('El camp title és obligatori i pot ocupar màxim 256 caràcters')
        .matches(/^\S*$/, '').withMessage('El camp title no pot contenir espais en blanc.'),
    body('description').notEmpty().isLength({ max: 1024 }).withMessage('El camp description és obligatori i pot ocupar màxim 1024 caràcters'),
    body('keywords').notEmpty().isLength({ max: 256 }).withMessage('El camp keywords és obligatori i pot ocupar màxim 256 caràcters'),
    body('author').notEmpty().isLength({ max: 256 }).withMessage('El camp author és obligatori i pot ocupar màxim 256 caràcters'),
    body('capt_date').notEmpty().isLength({ max: 10 }).withMessage('El camp capt_date pot ocupar màxim 10 caràcters')
        .custom((value, { req }) => {
            // Verifiquem el format de la data amb moment 'yyyy-mm-dd'
            if (!moment(value, 'YYYY-MM-DD', true).isValid()) throw new Error('El camp capt_date ha de tenir format "yyyy-mm-dd"');

            // Verifiquem que la data sigui anterior o igual a la data d'avui
            const today = moment().startOf('day');
            const captureDate = moment(value, 'YYYY-MM-DD').startOf('day');
            if (captureDate.isAfter(today)) throw new Error('El camp capt_date ha de ser ser igual o anterior a la data d\'avui');

            return true;
        })
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const { id, title, description, keywords, author, capt_date } = req.body;

            // Obtén el nom de l'arxiu en cas que existeixi a la base de dades
            dbConnection.execute('SELECT filename, creator FROM pr2.image WHERE ID = ?', [id], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                console.log(result);

                if (result.length === 0) {
                    return res.status(404).json({ result: -11, data: { message: 'No existeix la imatge amb tal id' } });
                }

                if (result[0]?.creator != req.userData.id) {
                    return res.status(403).json({ result: -12, data: { message: 'No ets el propietari de la imatge' } });
                }
                
                const existingFilename = result[0]?.filename;
                const extension = path.extname(existingFilename);
                const newFilename = id + '_' + title + extension;
                const oldFilePath = path.join(filePath, existingFilename);
                const newFilePath = path.join(filePath, newFilename);

                // Actualiza las columnas de la base de datos
                dbConnection.execute('CALL updateImage(?,?,?,?,?,?,?)', [id, title, description, keywords, author, capt_date, newFilename], (err, result, fields) => {
                    if (err) {
                        console.error('Error de la Base de Dades:', err);
                        return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                    }

                    fs.rename(oldFilePath, newFilePath, (err) => {
                        if (err) {
                            console.error('Error al cambiar el nombre del archivo:', err);
                            return res.status(500).json({ result: -12, data: { message: 'Error en canviar el nom de l\'arxiu' } });
                        }

                        return res.status(200).json({ result: 0, data: { message: 'Imatge modificada correctament' } });
                    });
                });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/********* GET **********/

/**
* GET method to list images
* @return list of images
*/
apiRouter.get('/list', (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            dbConnection.execute('CALL getAllImages()', (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }
                const data = { result: 0, data: result[0] };
                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'No hi ha imatges a la base de dades' } });
                else return res.status(200).json(data);
                //else return res.status(200).json({ result: 0, data: result[0] });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});

/**
* GET method to search images by id
* @param id
* @return
*/
apiRouter.get('/searchID/:id',
[
    param('id').isInt().withMessage('El paràmetre id és obligatori i ha de ser un enter')
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const id = req.params.id;

            dbConnection.execute('CALL getImageById(?)', [id], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(404).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else return res.status(200).json({ result: 0, data: result[0][0] });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/**
* GET method to search images by title
* @param title
* @return
*/
apiRouter.get('/searchTitle/:title',
[
    param('title').notEmpty().isLength({ max: 256 }).withMessage('El camp title és obligatori i pot ocupar màxim 256 caràcters')
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const title = req.params.title;
            console.log('searching by title: ' + title);

            dbConnection.execute('CALL getImagesByTitle(?)', [title], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else return res.status(200).json({ result: 0, data: result[0] });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/**
* GET method to search images by creation date. Date format should be
* yyyy-mm-dd
* @param capture_date
* @return
*/
apiRouter.get('/searchCaptureDate/:capture_date',
[
    param('capture_date').notEmpty().isLength({ max: 10 }).withMessage('El camp capture_date pot ocupar màxim 10 caràcters')
    .custom((value, { req }) => { // Verifiquem el format de la data amb moment 'yyyy-mm-dd'
        if (!moment(value, 'YYYY-MM-DD', true).isValid()) throw new Error('El camp capture_date ha de tenir format \'yyyy-mm-dd\'');
        return true;
    })
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const capture_date = req.params.capture_date;
            console.log('searching by capture date: ' + capture_date);

            dbConnection.execute('CALL getImagesByCreationDate(?)', [capture_date], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else return res.status(200).json({ result: 0, data: result[0] });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/**
* GET method to search images by author
* @param author
* @return
*/
apiRouter.get('/searchAuthor/:author',
[
    param('author').notEmpty().isLength({ max: 256 }).withMessage('El camp author és obligatori i pot ocupar màxim 256 caràcters')
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const author = req.params.author;
            console.log('searching by author: ' + author);

            dbConnection.execute('CALL getImagesByAuthor(?)', [author], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else return res.status(200).json({ result: 0, data: result[0] });
            });
        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/**
* GET method to search images by keyword
* @param keywords
* @return
*/
apiRouter.get('/searchKeyword/:keyword',
[
    param('keyword').notEmpty().isLength({ max: 256 }).withMessage('El camp keyword és obligatori i pot ocupar màxim 256 caràcters')
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const keyword = req.params.keyword;
            console.log('searching by keyword: ' + keyword);

            dbConnection.execute('CALL getImagesByKeyword(?)', [keyword], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else { 
                    const jsonData = { result: 0, data: result[0] };
                    console.log('JSON enviado al cliente: ', JSON.stringify(jsonData));
                    return res.status(200).json(jsonData);
                }
            });        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});


/**
* GET method to search images by coincidence on: title, description, author, keywords and captureDate 
* @param coincidence
* @return
*/
apiRouter.get('/searchCoincidence/:coincidence',
[
    param('coincidence').notEmpty().isLength({ max: 1024 }).withMessage('El camp coincidence és obligatori i pot ocupar màxim 1024 caràcters')
], (req, res) => {
    if (req.userData && req.userData.id) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                console.log(errors);
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const coincidence = req.params.coincidence;
            console.log('searching by coincidence: ' + coincidence);
            dbConnection.execute('CALL getImagesByCoincidence(?)', [coincidence], (err, result, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).json({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (result[0].length === 0 ) return res.status(200).json({ result: 1, data: { message: 'Cap imatge de la base de dades compleix el criteri de cerca' } });
                else return res.status(200).json({ result: 0, data: result[0] });
            });        } catch (error) {
            console.error('Error intern del servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error intern del servidor' } });
        }
    } else {
        return res.status(401).json({ result: -1, data: { message: 'No s\'ha iniciat sessió' } });
    }
});

module.exports = apiRouter;