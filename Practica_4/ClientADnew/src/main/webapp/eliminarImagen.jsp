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
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="jakarta.json.Json"%>
    <%@ page import="jakarta.json.JsonObject"%>
    <%@ page import="jakarta.json.JsonReader"%>
    <%@ page import="Aux.ConnectionUtil" %>
    <%@ page import="Aux.SessioUtil" %>
    <%@ page import="Aux.Imatge"%>
    <%@ page import="java.net.HttpURLConnection"%>
    <%@ page import="java.net.URL"%>
    

    
    <%
        String addr = ConnectionUtil.getServerAddr();
            
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
        
        
        //Guardo la imatge amb id en un objecte imatge i verifico que l'autor és qui la intenta eliminar
        String id = request.getParameter("id");
        Imatge imatge = null;
        
        // Verifica que 'id' no sigui nul
        if (id != null) {
            try {
                try {
                    //CONNEXIO GET IMATGE AMB ID
                    URL url = new URL("http://" + addr + "/RestAD/resources/jakartaee9/searchID/" + id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                            JsonObject jsonImatge = jsonReader.readObject();
                            imatge = Imatge.jsonToImatge(jsonImatge);
                        }
                        connection.disconnect();
                    } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                        connection.disconnect();    
                        request.setAttribute("tipus_error", "connexio");
                        request.setAttribute("msg_error", "No s'ha trobat la imatge amb id: " + id);
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                        return;
                    } else {
                        connection.disconnect();
                        request.setAttribute("tipus_error", "connexio");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                        return;
                    }
                    
                } catch (Exception e) {
                    request.setAttribute("tipus_error", "connexio");
                    request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei REST, torna-ho a intentar més tard");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                }
                
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
                              out.println("<img img src='http://"+ addr + "/RestAD/images/" + imatge.getFilename()+"'style='max-width:300px; max-height: 300px'  ></a></td>");
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
