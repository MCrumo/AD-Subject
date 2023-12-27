<%-- 
    Document   : buscarImagen
    Created on : 13 nov 2023, 13:56:03
    Author     : miquel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<!--Página que permite buscar imágenes a partir de uno o varios
    campos asociados a la imagen. Para las imágenes que sean del usuario que hace la
    búsqueda tiene que aparecer la posibilidad de modificar la imagen (enlace a la
    página modificarImagen) o eliminarla (enlace a la página eliminarImagen). -->

<html>
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="java.util.List" %>
    <%@ page import="java.util.ListIterator" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%//@ page import="jakarta.json.Json"%>
    <%//@ page import="jakarta.json.JsonObject"%>
    <%//@ page import="jakarta.json.JsonReader"%>
    <%@ page import="Aux.SessioUtil" %>
    <%@ page import="Aux.Imatge"%>
    <%@ page import="java.net.HttpURLConnection"%>
    <%@ page import="java.net.URL"%>
    <%@ page import="Aux.ConnectionUtil"%>


    
    <%
        // Obté la HttpSession
        HttpSession sessio = request.getSession(false);
        String username = "";
        
        int res = SessioUtil.validaSessio(sessio);
        // Verifica si la HttpSession no es nula i si existeix un atribut "username"
        if (res != 0) {
            if (res == -2) { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
                request.setAttribute("tipus_error", "autenticacio");
                request.setAttribute("msg_error", "La sessió no està iniciada.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            } else { // res = -1 o qualsevol altre
                request.setAttribute("tipus_error", "autenticacio");
                request.setAttribute("msg_error", "La sessió no és vàlida.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
        } else {
            username = (String) sessio.getAttribute("username");
        }
    %>
    
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Buscar Imatge</title>
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleOpcions.css">
    </head>
    <body>
        <div align="center">
        <h1>Buscar Imatge</h1>
        <button onclick="goBack()" class="boto" >Enrere</button>
        <br>

        <!--    <form action='buscarImagen' method = 'POST'>
                <input type='text' name='descripcio' placeholder='Buscador' required />
                <select name='modeBusqueda'>
                    <option value='keyword'>Paraula clau</option>
                    <option value='title'>Títol</option>
                    <option value='author'>Autor</option> <br/>
                </select>
                <br>
                <input type='submit' class='boto' value='Buscar' />
            </form>     -->
        
            <%
                String addrFront = ConnectionUtil.getServerAddrFrontend();
                String addr = ConnectionUtil.getServerAddr();
                
                List<Imatge> imatges = (List<Imatge>) request.getAttribute("setImatges");
                Integer estatBusqueda = (Integer) request.getAttribute("busquedaBuida");
                if (estatBusqueda != null && estatBusqueda == 1) out.println("<h2>Ho sentim, no hem trobat cap imatge</h2>");
                if (imatges == null || imatges.isEmpty()) {
                    out.println("<form action='buscarImagen' method = 'POST'>");
                    out.println("<input type='text' name='descripcio' placeholder='Buscador' required />");
                    out.println("<br>");
                    out.println("<select class='select' name='modeBusqueda'>");
                    out.println("<option value='all'>Totes</option>");
                    out.println("<option value='keyword'>Paraula clau</option>");
                    out.println("<option value='title'>Títol</option>");
                    out.println("<option value='author'>Autor</option>");
                    out.println("<option value='creationDate'>Data de creació (yyyy-mm-dd)</option>");
                    out.println("</select>");
                    out.println("<br>");
                    out.println("<input type='submit' class='boto' value='Buscar' />");
                    out.println("</form>");
                }
                else { //if (!(imatges == null || imatges.isEmpty())) {               
                    out.println("<table border='1' class='table'>");
                    out.println("<thread>");
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
                    out.println("</thread>");
                
                    ListIterator<Imatge> listIterator = imatges.listIterator();
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
                        out.println("<td><a href='showImg.jsp?id="+i.getId()+"'><img src='http://"+ addrFront +"/images/" + i.getFilename()+"' width='75' height='50'></a></td>");                        
                        if (username.equals(i.getCreator())) {
                            out.println("<td><a href='modificarImagen.jsp?id="+i.getId()+"'>Modificar</a>/<a href='eliminarImagen.jsp?id="+i.getId()+"'>Eliminar</a></td>");
                        }
                        out.println("</tr>");
                     }
                     out.println("</tbody>");
                    out.println("</table>");
                }
                
            %>
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
