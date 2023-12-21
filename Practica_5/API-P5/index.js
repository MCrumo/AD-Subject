express = require('express');
config = require('./config/config');
cors = require('cors');

const fileUpload = require('express-fileupload');
const bodyParser = require('body-parser');
const mysql = require('mysql2');
const path = require('path');

const app = express();
const port = config.port;

// BODY PARSER
app.use(bodyParser.json({limit:'10mb'}));
app.use(bodyParser.urlencoded({ extended: true }));

// EXPRESS
app.use(express.json());

// FILE UPLOAD
app.use(fileUpload());

// CORS
app.use(cors({ origin: true, credentials: true }));

// DATABASE
dbConnection = mysql.createConnection(config.mysqlConfig);

dbConnection.connect((err) => {
  if (err) {
    console.error('Error connexió base de dades:', err);
    return;
  }
  console.log(`Connectat a la base de dades ${config.mysqlConfig.database}`);
});

// RUTES
const publicRouter = require('./routes/public-routes');
const apiRouter = require('./routes/api-routes');
const { verificarToken } = require('./middleware/jwt-check');

// APLICAR RUTES I JWT
app.use('/', publicRouter);
// Middleware para servir arxius estàtics des de la carpeta de images
const imagesFolder = path.join(__dirname, 'images');
app.use('/images', express.static(imagesFolder));

// Apliquem middleware JWT a les rutes per sota d'aquest punt
app.use(verificarToken);
app.use('/api', apiRouter);



app.listen(port, () => {
  console.log(`Servidor corrent a http://localhost:${port}`);
});
