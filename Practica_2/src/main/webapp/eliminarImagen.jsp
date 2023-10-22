<%-- 
    Document   : eliminarImagen
    Created on : 14 oct 2023, 19:22:19
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!-- Página para eliminar una imagen del sistema. A esta página se
puede llegar desde las páginas de listado o búsqueda, que se explican más adelante.
El fichero asociado a los datos que hay en la base de datos también se tiene que
eliminar. -->

<html>
    
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="DB.Database" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="Aux.Imatge"%>

    
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
    
    <%
        
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Eliminar Imatge</title>
    </head>
    <body>
        <div align="center">
            <h1>Eliminar Imatge:</h1>
            <button onclick="goBack()">Enrere</button>
            <br>
            <table class='table'>
                <tr>
                    <th>Id</th>
                    <th>Títol</th>
                    <th>Descripció</th>
                    <th>Paraules clau</th>
                    <th>Autor</th>
                    <th>Creador</th>
                    <th>Data de pujada</th>
                    <th>Data de captura</th>
                    <th>Nom de l'arxiu</th>
                </tr>
                <tr>
                    <%  
                        out.println("<td>"+i.getTitle()+"</td>");
                        out.println("<td>"+i.getDescription()+"</td>");
                        out.println("<td>"+i.getKeywords()+"</td>");
                        out.println("<td>"+i.getAuthor()+"</td>");
                        out.println("<td>"+i.getCreator()+"</td>");
                        out.println("<td>"+i.getStorageDate()+"</td>");
                        out.println("<td>"+i.getCaptureDate()+"</td>");
                    %>
                </tr>
                <tr>
                    <%out.println("<td><a href='Images/"+img.filename+"'><img src='images/"+img.filename+"'width='100' height='75'></a></td>");%>
                </tr>
                <tr>
                    <form action="eliminarImagen" method = "POST">

                    </form>
                </tr>
            </table>
        </div>
        <script>
        function goBack() {
            // Utiliza window.history par retrocedir una pàgina en el navegador
            window.history.back();
        }
        </script>
    </body>
</html>
