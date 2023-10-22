<%-- 
    Document   : buscarImagen
    Created on : 14 oct 2023, 19:25:53
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!--Página que permite buscar imágenes a partir de uno o varios
    campos asociados a la imagen. Para las imágenes que sean del usuario que hace la
    búsqueda tiene que aparecer la posibilidad de modificar la imagen (enlace a la
    página modificarImagen) o eliminarla (enlace a la página eliminarImagen). -->

<html>
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="DB.Database" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="Aux.Imatge" %>
    <%@ page import="java.util.List" %>
    <%@page import="java.util.ListIterator"%>


    
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
        <title>Buscar Imatge</title>
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body>
        <div align="center">
        <h1>Buscar Imatge:</h1>
        <button onclick="goBack()">Enrere</button>
        </div>
            <form action='buscarImagen' method = 'POST'>
                <input type='text' name='value' placeholder="Introdueix l'usuari autor" required>
                <select name='modeBusqueda'>
                    <option value='keyword'>Paraula clau</option>
                    <option value='title'>Títol</option>
                    <option value='author'>Autor</option> <br/>
                </select>    
                <input type='submit' class="boto" value='Search' />
            </form>
            
            <%
                List<Imatge> imatges = (List<Imatge>) request.getAttribute("setImatges");
                //Integer emptysearch = (Integer) request.getAttribute("estatBusqueda");
                if (setImatges.isEmpty()) out.println("Ho sentim, no hem trobat cap imatge");
                else {               
                    out.println("<table class='table'>");
                    out.println("<tr>");
                    out.println("<th>Id</th>");
                    out.println("<th>Títol</th>");
                    out.println("<th>Descripció</th>");
                    out.println("<th>Paraules clau</th>");
                    out.println("<th>Autor</th>");
                    out.println("<th>Creador</th>");
                    out.println("<th>Date de pujada</th>");
                    out.println("<th>Date de captura</th>");
                    out.println("<th>Filename</th>");
                    out.println("<th>Imagte</th>");
                    out.println("<th>Accions</th>");
                    out.println("</tr>");

                    ListIterator<Imatge> listIterator = imatges.listIterator();
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
                        out.println("<td><a href='display.jsp?id="+i.id+"'><img src='Images/"+i.filename+"'width='75' height='50'></a></td>");
                        if (user.equals(i.creator)) {
                            out.println("<td><a href='modificarImagen.jsp?id="+i.id+"'>Modify</a>/<a href='eliminarImagen.jsp?id="+i.id+"'>Delete</a></td>");
                        }
                        out.println("</tr>");
                     }
                    out.println("</table>");
                }
                
            %>
        
        <script>
        function goBack() {
            // Utiliza window.history par retrocedir una pàgina en el navegador
            window.history.back();
        }
        </script>
    </body>
</html>
