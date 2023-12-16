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
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="jakarta.json.Json"%>
    <%@ page import="jakarta.json.JsonObject"%>
    <%@ page import="jakarta.json.JsonReader"%>
    <%@ page import="Aux.SessioUtil" %>
    <%@ page import="Aux.Imatge"%>
    <%@ page import="java.net.HttpURLConnection"%>
    <%@ page import="java.net.URL"%>
    <%@ page import="java.util.List" %>
    <%@ page import="Aux.ConnectionUtil" %>

    
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
        <link rel="stylesheet" type="text/css" href="./css/styleOpcions.css">
    </head>
    <body style="max-width: 800px;">
        <div align="center">
            <h1>Modificar Imatge</h1>
            <button onclick="goBack()" class='boto'>Enrere</button>
        
            <br>
            <%-- Mostrar missatges d'error si existeixen --%>
            <% List<String> errors = (List<String>)request.getAttribute("errors"); %>
            <% if (errors != null && !errors.isEmpty()) { %>
                <div style="color: red;">
                    <ul>
                        <% for (String error : errors) { %>
                            <p><%= error %></p>
                        <% } %>
                    </ul>
                    <p><a href='menu.jsp' class="enllaçMenu">Tornar al menu principal</a></p>
                </div>
            <% } %>
            <%
                Boolean okObject = (Boolean) request.getAttribute("ok");
                if (okObject != null && okObject.booleanValue()) {
            %>
                <div style="color: green;">
                    <p>
                        Les modificacions s'han enregistrat correctament
                        <p><a href='menu.jsp' class="enllaçMenu">Tornar al menu principal</a></p>
                    </p>
                </div>
            <% } %>
            <form action="modificarImagen" method="POST">
                <table>
                    <tr>
                        <td style="text-align: right;">
                            <label for="title">Títol:</label>
                        </td>
                        <td>
                            <input type="text" id="title" name="title" placeholder="Introdueix el títol" value="<%= imatge.getTitle() %>" required style="width: 300px;">
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <label for="description">Descripció:</label>
                        </td>
                        <td>
                            <input type="text" id="description" name="description" placeholder="Introdueix la descripció" value="<%= imatge.getDescription() %>" required style="width: 300px;">
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <label for="keywords">Paraules clau:</label>
                        </td>
                        <td>
                            <input type="text" id="keywords" name="keywords" placeholder="Introdueix les paraules clau" value="<%= imatge.getKeywords() %>" required style="width: 300px;">
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <label for="author">Autor:</label>
                        </td>
                        <td>
                            <input type="text" id="author" name="author" placeholder="Introdueix l'usuari autor" value="<%= imatge.getAuthor() %>" required style="width: 300px;">
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: right;">
                            <label for="captureDate">Data de captura:</label>
                        </td>
                        <td>
                            <input type="date" id="captureDate" name="captureDate" value="<%= imatge.getCaptureDateISO() %>" required style="width: 200px;">
                        </td>
                    </tr>
                </table>
                <p>
                    <% 
                       out.println("<a href='showImg.jsp?id="+imatge.getId()+"'>");
                       out.println("<img img src='http://"+ addr + "/RestAD/images/" + imatge.getFilename()+"' style='max-width:300px; max-height: 300px'></a>"); %>
                </p>
                
                <input type="hidden" name="id" value="<%= imatge.getId() %>">
                <input type="submit" class='boto' value="Modificar detalls">
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
