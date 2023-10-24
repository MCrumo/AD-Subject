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
                    if (!imatge.getCreator().equals(username)) {
                        request.setAttribute("tipus_error", "eliminar");
                        request.setAttribute("msg_error", "Estas intentant eliminar una imatge que no és teva...");
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
        <title>Eliminar Imatge</title>
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleOpcions.css">
    </head>
    <body>
        <div align="center">
            <h1>Eliminar Imatge:</h1>
            <button onclick="goBack()" class='boto'>Enrere</button>
            <p></p>
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
                    <% if (imatge != null) { %>
                        <td><%= imatge.getId() %></td>
                        <td><%= imatge.getTitle() %></td>
                        <td><%= imatge.getDescription() %></td>
                        <td><%= imatge.getKeywords() %></td>
                        <td><%= imatge.getAuthor() %></td>
                        <td><%= imatge.getCreator() %></td>
                        <td><%= imatge.getStorageDate() %></td>
                        <td><%= imatge.getCaptureDate() %></td>
                        <td><%= imatge.getFilename() %></td>
                    <% } %>
                </tr>
                <tr>
                    <% if (imatge != null) {
                              out.println("<td colspan='9' style='text-align:center;'>"); 
                              out.println("<a href='showImg.jsp?id="+imatge.getId()+"'>");
                              out.println("<img src='images/"+imatge.getFilename()+" 'style='max-width:300px; max-height: 300px'  ></a></td>");
                    } %>
                </tr>   
                <tr>
                    <td colspan="9" style="text-align:center;">
                        <form action="eliminarImagen" method="POST">
                            <input type="hidden" name="id" value="<%= imatge.getId() %>">
                            <button type="submit" class="boto"> Eliminar imatge </button>
                        </form>
                    </td>
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
