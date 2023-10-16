<%-- 
    Document   : registrarImagen
    Created on : 14 oct 2023, 19:15:01
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!-- Página que pide los datos de una imagen para darla de alta en
la aplicación. Los datos de una imagen deben ser (como mínimo), identificador de
la imagen (no se le pide al usuario, se asigna automáticamente), título, descripción,
palabras clave, autor (el usuario que capturó la imagen inicialmente), creador (el
usuario que la inserta en el sistema), fecha de creación, fecha de alta en el sistema
(esta no se pide al usuario) y nombre del fichero que la contiene. Los datos de la
imagen que no se generen automáticamente se tienen que pedir al usuario con un
formulario. El fichero de imagen también se tiene que subir al sistema (campo de
formulario HTML tipo file) y se almacenará en un directorio fijo de la aplicación
web (ver Anexo 1 para detalles de gestión de los directorios). Los campos autor y
creador pueden ser el mismo, aunque sólo el creador tiene que ser un usuario
dado de alta en la base de datos (foreign key en las tablas de la práctica 1). -->

<html>
    
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="DB.Database" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>

    
    <%
        // Obté la HttpSession
        HttpSession sessio = request.getSession(false);

        // Verifica si la HttpSession no es nula i si existeix un atribut "username"
        if (sessio != null && sessio.getAttribute("username") != null) {
            // Obté el nom d'usuari de la sessió
            String username = (String) sessio.getAttribute("username");

            // Crea una instància de ls classe Database
            Database db = new Database();
            
            // Crida al mètode checkUsername para verificar si nom d'usuari existeix en la base de dades
            boolean userExists = db.checkUsername(username);

            // Si no exiteix l'usuari, envia a la pàgina d'error pertinent
            if (!userExists) {
                request.setAttribute("tipus_error", "autenticacio");
                request.setAttribute("msg_error", "La sessió no és vàlida.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
        } else { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
            request.setAttribute("tipus_error", "autenticacio");
            request.setAttribute("msg_error", "La sessió no està iniciada.");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Registrar Imatge</title>
    </head>
    <body>
        <div align="center">
        <h1>Registrar Imatge:</h1>
        <button onclick="goBack()">Enrere</button>
        </div>
        
        <!--Aquí val el codi HTML-->
        
        <script>
        function goBack() {
            // Utiliza window.history par retrocedir una pàgina en el navegador
            window.history.back();
        }
        </script>
    </body>
</html>
