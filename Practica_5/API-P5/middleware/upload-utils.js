// Middleware per validar que la request conté una imatge

const { body, validationResult } = require('express-validator');
const imageSize = require('image-size');

const validateFileUpload = [
    body('image').custom((value, { req }) => {
      //console.log("req.files: ");
      console.log(req.files);


      // Verifiquem que s'ha pujat un arxiu
      if (!req.files || Object.keys(req.files).length === 0) {
        throw new Error('L\'arxiu és obligatori.');
      }
  
      // Verifiquem que es una imatge mirant la mida
      const uploadedFile = req.files.image;
      const dimensions = imageSize(uploadedFile.data);

      if (!dimensions.width || !dimensions.height) {
        throw new Error("Format d'arxiu no vàlid. Només s'accepten imatges o gifs.");
      }
  
      return true;
    }),
  ];

  module.exports = {
    validateFileUpload
  };