const jwt = require('jsonwebtoken');
const bcrypt = require("bcrypt");
const config = require('../config/config');
const fs = require('fs');
const path = require('path');
const { validationResult, param, body } = require('express-validator');
const publicRouter = express.Router();
const secretKey = config.jwtKey;


/***** GET *****/

/**
 * Inicia la sessió per l'usuari i assigna un token de sessió
 * 
 * @route GET /login/:user/:password
 * @param {string} user Nom d'usuari
 * @param {string} password Contrasenya de l'usuari
 * @returns {SuccessResponse} 200 - Login correcte
 * @returns {ErrorResponse} 400 - [-30] Error en els paràmetres. Array de "message"
 * @returns {ErrorResponse} 404 - [-11] Credencials invàlides
 * @returns {ErrorResponse} 500 - [-10] Error intern del servidor
 * @returns {ErrorResponse} 500 - [-20] Error de la base de dades
 */

publicRouter.get('/login/:user/:password', 
    [
        param('user').notEmpty().trim().withMessage('El nom d\'usuari no pot estar buit'),
        param('password').notEmpty().withMessage('Has de proporcionar una contrasenya vàlida')
    ],
    (req, res) => {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const user = req.params.user;
            const password = req.params.password;

            dbConnection.query('CALL getUserById(?)', [user], (err, results, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).send({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                // No hi ha cap usuari amb tal nom d'usuari
                if (results[0].length === 0) { 
                    return res.status(404).send({ result: -11, data: { message: 'Invalid credentials' } });

                }
                // Existeix un usuari amb tal nom d'usuari
                else { 
                    bcrypt.compare(password, results[0][0].password, function(err, isValid) {
                        if (err) {
                            console.error('Error Intern del Servidor (user password check):', err);
                            res.status(400).send({ result: -10, data: { message: 'Error Intern del Servidor' } });
                            return;
                        }

                        if (isValid) { // Contrasenya correcte
                            const payload = {
                                id: results[0][0].id_usuario,
                            }
            
                            const token = jwt.sign(payload, config.jwtKey, { expiresIn: '2h' });
                            return res.status(200).send({
                                result: 0,
                                data: {
                                    id: results[0][0].id_usuario,
                                    token
                                }
                            });
                        } else { // Contrasenya incorrecte
                            return res.status(404).send({ result: -11, data: {  message: 'Invalid credentials' } });
                        }
                    });
                }
            });
        } catch (error) {
            console.error('Error Intern del Servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error Intern del Servidor' } });
        }
});

/***** POST *****/

/**
 * Verifica el token de sessió
 * 
 * @route POST /verify-token
 * @param {string} token Bearer token
 * @returns {SuccessResponse} 200 - Login correcte
 * @returns {ErrorResponse} 400 - [-10] Error intern del servidor
 * @returns {ErrorResponse} 404 - [-11] Token invàlid
 * @returns {ErrorResponse} 404 - [-12] Usuari no reconegut
 * @returns {ErrorResponse} 500 - [-20] Error de la base de dades
 */
publicRouter.post('/verify-token', (req, res) => {
    try {
        const token = req.body.token;

        jwt.verify(token, secretKey, (err, decoded) => {
            if (err) {
                return res.status(400).send({ result: -11, data: { message: 'Token inválido' } });
            }
            req.userData = decoded;
            // Recull les dades de l'usuari
            dbConnection.query('SELECT id_usuario FROM pr2.usuarios WHERE id_usuario=?', [decoded.id], (err, results, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).send({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                if (results[0].length === 0) {
                    return res.status(404).send({ result: -12, data: { message: 'User unknown' } });
                } else {
                    console.log("Token de l'usuari " + results[0]?.id_usuario + " verificat.")
                    return res.status(200).send({
                        result: 0,
                        data: {
                            id: results[0]?.id_usuario,
                            token  
                        }  
                    });
                }
            });
        });
    } catch (error) {
        console.error('Error Intern del Servidor:', error);
        return res.status(400).json({ result: -10, data: { message: 'Error Intern del Servidor' } });
    }
});

/**
 * Registre d'usuari
 * 
 * @param user
 * @param password
 * @return
 */

publicRouter.post('/register-user', 
    [
        body('user').notEmpty().trim().withMessage('El nom d\'usuari no pot estar buit'),
        body('password').notEmpty().withMessage('Has de proporcionar una contrasenya')
    ],
    (req, res) => {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                const messages = errors.array().map(error => error.msg);
                return res.status(400).json({ result: -30, data: { message: messages } });
            }

            const user = req.body.user;
            const password = req.body.password;

            bcrypt.hash(password, config.Bycript_Hashing_Cost, function(err, hashedPass) {
                if (err) {
                    console.error('Error de Bcrypt: ', err);
                    return res.status(400).send({ result: -11, data: { message: 'Error intern del servidor' } });
                }

                dbConnection.query('CALL insertUser(?,?)', [user, hashedPass], (err, results, fields) => {
                    if (err) {
                        //Si l'usuari ja existeix no és un error però no deixem afegir-lo
                        if (err.code === 'ER_DUP_ENTRY') return res.status(200).send({ result: 1, data: { message: 'Ja existeix un usuari amb aquest nom' } })
                        
                        console.error('Error de la Base de Dades:', err);
                        return res.status(500).send({ result: -20, data: { message: 'Error de la Base de Dades' } });
                    }

                    return res.status(200).send({ result: 0, data: { message: 'Usuari creat correctament' } });
                });
            });

            /*dbConnection.query('CALL getUserById(?)', [user], (err, results, fields) => {
                if (err) {
                    console.error('Error de la Base de Dades:', err);
                    return res.status(500).send({ result: -20, data: { message: 'Error de la Base de Dades' } });
                }

                // No hi ha cap usuari amb tal nom d'usuari
                if (results[0].length === 0) { 
                    return res.status(404).send({ result: -11, data: { message: 'Invalid credentials' } });

                }
                // Existeix un usuari amb tal nom d'usuari
                else { 
                    bcrypt.compare(password, results[0][0].password, function(err, isValid) {
                        if (err) {
                            console.error('Error Intern del Servidor (user password check):', err);
                            res.status(400).send({ result: -10, data: { message: 'Error Intern del Servidor' } });
                            return;
                        }

                        if (isValid) { // Contrasenya correcte
                            const payload = {
                                id: results[0][0].id_usuario,
                            }
            
                            const token = jwt.sign(payload, config.jwtKey, { expiresIn: '2h' });
                            return res.status(200).send({
                                result: 0,
                                data: {
                                    id: results[0][0].id_usuario,
                                    token
                                }
                            });
                        } else { // Contrasenya incorrecte
                            return res.status(404).send({ result: -11, data: {  message: 'Invalid credentials' } });
                        }
                    });
                }
            });*/
        } catch (error) {
            console.error('Error Intern del Servidor:', error);
            return res.status(400).json({ result: -10, data: { message: 'Error Intern del Servidor' } });
        }
});


module.exports = publicRouter;
