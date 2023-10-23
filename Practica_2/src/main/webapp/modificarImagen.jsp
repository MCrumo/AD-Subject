<%-- 
    Document   : modificarImagen
    Created on : 14 oct 2023, 19:20:06
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!-- Página que permite modificar los datos de una imagen
registrada por un usuario en el sistema. A esta página se puede llegar desde las
páginas de listado o búsqueda, que se explican más adelante. -->

<html>
    
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="DB.Database" %>
    <%@ page import="Aux.Imatge" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>

    
    <%
        // Obté la HttpSession
        HttpSession sessio = request.getSession(false);
        String username = "";
        
        // Verifica si la HttpSession no es nula i si existeix un atribut "username"
        if (sessio != null && sessio.getAttribute("username") != null) {
            // Obté el nom d'usuari de la sessió
            username = (String) sessio.getAttribute("username");

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
        
        
        //Guardo la imatge amb id en un objecte imatge i verifico que l'autor és qui la intenta eliminar
        String id = request.getParameter("id");
        Imatge imatge = null;

        // Verifica que 'id' no sigui nul
        if (id != null) {
            try {
                Database db = new Database();
                imatge = db.getImatgeAmbId(id);
                if (imatge != null) {
                    if (!imatge.getAuthor().equals(username)) {
                        request.setAttribute("tipus_error", "eliminar");
                        request.setAttribute("msg_error", "Estas intentant modificar una imatge que no és teva...");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }
                } else {
                    request.setAttribute("tipus_error", "eliminar");
                    request.setAttribute("msg_error", "Hi ha hagut un error en llegir la imatge de la base de dades.");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("tipus_error", "eliminar");
                request.setAttribute("msg_error", "No existeix cap fitxer amb tal id: " + id);
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
        }
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Modificar Imatge</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body style="max-width: 800px;">
        <div align="center">
            <h1>Modificar Imatge</h1>
            <button onclick="goBack()">Enrere</button>
        
            <br>
            <form action="modificarImagen" method="POST">
                <p>
                    <label for="capture_date">Títol:</label>
                    <input type="text" id="title" name="title" placeholder="Introdueix el títol" value="<%= imatge.getTitle() %>" required>
                </p>

                <p>
                    <label for="capture_date">Descripció:</label>
                    <input type="text" id="description" name="description" placeholder="Introdueix la descripció" value="<%= imatge.getDescription() %>" required>
                </p>

                <p>
                    <label for="capture_date">Paraules clau:</label>
                    <input type="text" id="keywords" name="keywords" placeholder="Introdueix les paraules clau" value="<%= imatge.getKeywords() %>" required>
                </p>

                <p>
                    <label for="capture_date">Autor:</label>
                    <input type="text" id="author" name="author" placeholder="Introdueix l'usuari autor" value="<%= imatge.getAuthor() %>" required>
                </p>

                <p>
                    <label for="capture_date">Data de captura:</label>
                    <input type="date" id="captureDate" name="captureDate" value="<%= imatge.getCaptureDateISO() %>" required>
                </p>
                
                <p>
                    <a href='images/<%= imatge.getFilename() %>'>
                        <img src='images/<%= imatge.getFilename() %>' width='400' height='400'>
                    </a>
                </p>
                
                <input type="hidden" name="id" value="<%= imatge.getId() %>">
                <input type="submit" value="Modificar detalls">
            </form>
    </div>
        <script>
        function goBack() {
            // Utiliza window.history par retrocedir una pàgina en el navegador
            window.history.back();
        }
        </script>
    </body>
</html>
