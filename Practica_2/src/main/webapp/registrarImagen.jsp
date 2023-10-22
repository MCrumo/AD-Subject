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
    <%@ page import="java.util.List" %>

    
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
        <!--link rel="stylesheet" type="text/css" href="./css/styleGeneral.css"-->
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body>
        <div align="center">
        <h1>Registrar Imatge:</h1>
        <form action="menu.jsp">
            <button type="submit" class="boto">Enrere</button>
        </form>
        </div>
        <p>
            <div align="center">
                <form action="registrarImagen" method="POST" enctype="multipart/form-data">
                    <p><input type="text" id="title" name="title" placeholder="Introdueix el títol" required></p>

                    <p><input type="text" id="description" name="description" placeholder="Introdueix la descripció" required></p>

                    <p><input type="text" id="keywords" name="keywords" placeholder="Introdueix les paraules clau" required></p>

                    <p><input type="text" id="author" name="author" placeholder="Introdueix l'usuari autor" required></p>

                    <p>
                        <label for="capture_date">Data de captura:</label>
                        <input type="date" id="captureDate" name="captureDate" required>
                    </p>
                    
                    <p>
                        <label for="image">Arxiu:</label>
                        <input type="file" id="image" name="image" required>
                    </p>
                    <input type="submit" value="Registra la imatge">
                </form>
                
                <%-- Mostrar missatges d'error si existeixen --%>
                <% List<String> errors = (List<String>)request.getAttribute("errors"); %>
                <% if (errors != null && !errors.isEmpty()) { %>
                    <div style="color: red;">
                        <ul>
                            <% for (String error : errors) { %>
                                <li><%= error %></li>
                            <% } %>
                        </ul>
                    </div>
                <% } %>
            </div>
        <p/>
    </body>
</html>
