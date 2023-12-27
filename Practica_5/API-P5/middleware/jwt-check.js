const jwt = require('jsonwebtoken');


// Clau secreta per verificar i signar els tokens
const secretKey = config.jwtKey;

// Middleware para verificar el token
const verificarToken = (req, res, next) => {
  const authorizationHeader = req.headers['authorization'];

  if (!authorizationHeader) {
    res.status(401).json({ result: -11, data: { message: 'Token no proporcionat' } });
  } else {
    // Verifiquem que el token es de tipus Bearer
    if (authorizationHeader.startsWith('Bearer ')) {
      const token = authorizationHeader.slice(7); // Eliminem "Bearer " del token

      jwt.verify(token, secretKey, (err, decoded) => {
        if (err) {
          return res.status(401).json({ result: -12, data: { message: 'Token invàlid' } });
        }
        // El token es vàlid, emmagatzemem les dades codificades a la silicitud.
        req.userData = decoded;

        // Verifiquem que l'usuari existeix a la base de dades
        dbConnection.query('SELECT * FROM pr2.usuarios WHERE id_usuario=?', [decoded.id], (err, [results, fields]) => {
          if (err) {
            console.error('Database error:', err);
            return res.status(500).send({ result: -20, data: { message: 'Database error' } });
          }
          if (results.length === 0) {
            return res.status(401).send({ result: -13, data: { message: 'User unknown' } });
          }
          // Continuem amb el propera funció del middleware (en cas que hi hagin)
          next();
        });
      });
    } else {
      return res.status(401).json({ result: -14, data: { message: 'Format de token incorrecte' } });
    }
  }
};

module.exports = {
  verificarToken,
};
