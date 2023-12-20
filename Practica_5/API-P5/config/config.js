const path = require('path');

module.exports = {
    port: 8082,
    mysqlConfig: {
        host: 'database',
        user: 'root',
        password: 'ji71FoLC04!',
        database: 'pr2',
        port: 3306,
        //host: 'localhost',
        //user: 'root',
        //password: '',
        //database: 'pr2',
        //port: 3306,
    },
    jwtKey: 'erG45LqcuaVlOVMY2EWxoqvASKSkoPi2sG96dh4SbY8Q6Ll6qelGnwiik9vE1GeZ',

    Bycript_Hashing_Cost: 5, //Cost de hashing de contrasenyes amb Bcrypt
    
    filePath: path.join(__dirname, '..', 'images')
};