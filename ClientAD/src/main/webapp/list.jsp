<%-- 
    Document   : list
    Created on : 14 oct 2023, 19:24:44
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!-- Página que lista todas las imágenes registradas en el sistema. Las imágenes
deben tener un enlace que permita visualizarlas. Para las imágenes que ha
registrado el usuario tiene que aparecer la posibilidad de modificar la imagen
(enlace a la página modificarImagen) o eliminarla (enlace a la página
eliminarImagen). -->

<html>
    
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="java.util.ListIterator"%>
    <%@ page import="java.util.List"%>
    
    <%@ page import="DB.Database" %>
    <%@ page import="Aux.Imatge" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>

    
    <%
        String username = "";
        // Obté la HttpSession
        HttpSession sessio = request.getSession(false);

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
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Llistat</title>
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleOpcions.css">
    </head>
    <body>
        <div align="center">
        <h1>Llistat d'Imatges</h1>
        <button onclick="goBack()" class='boto'>Enrere</button>
        
        <table border='1' class="table">
            <thread>
                <tr>
                    <th>Id</th>
                    <th>Títol</th>
                    <th>Descripció</th>
                    <th>Paraules Clau</th>
                    <th>Autor</th>
                    <th>Creador</th>
                    <th>Data de pujada</th>
                    <th>Data de captura</th>
                    <th>Filename</th>
                    <th>Imatge</th>
                    <th>Accions</th>
                </tr>
            </thread>
        <%  
            Database db = new Database();
            List<Imatge> setImatges = db.getAllImatges();
            ListIterator<Imatge> listIterator = setImatges.listIterator();
            out.println("<tbody>");
            while(listIterator.hasNext()) {
                Imatge i = listIterator.next();
                out.println("<tr>");
                out.println("<td>"+i.getId()+"</td>");
                out.println("<td>"+i.getTitle()+"</td>");
                out.println("<td>"+i.getDescription()+"</td>");
                out.println("<td>"+i.getKeywords()+"</td>");
                out.println("<td>"+i.getAuthor()+"</td>");
                out.println("<td>"+i.getCreator()+"</td>");
                out.println("<td>"+i.getStorageDate()+"</td>");
                out.println("<td>"+i.getCaptureDate()+"</td>");
                out.println("<td>"+i.getFilename()+"</td>");
                out.println("<td><a href='showImg.jsp?id="+i.getId()+"'><img src='images/"+i.getFilename()+"'width='75' height='50'></a></td>");
                if (username.equals(i.getCreator())) {
                    out.println("<td><a href='modificarImagen.jsp?id="+i.getId()+"'>Modificar</a>/<a href='eliminarImagen.jsp?id="+i.getId()+"'>Eliminar</a></td>");
                }
                out.println("</tr>");
            }
            out.println("</tbody>");
            out.println("</table>");
        %>
        
        </table>
        
        <br>
        <a href="menu.jsp"class="boto">Menú</a>
        </div>
        
        <script>
        function goBack() {
            // Utiliza window.history par retrocedir una pàgina en el navegador
            window.history.back();
        }
        </script>
    </body>
</html>
