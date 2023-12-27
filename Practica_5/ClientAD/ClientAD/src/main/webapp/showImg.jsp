<%-- 
    Document   : showImg
    Created on : 23 oct 2023, 15:56:01
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="jakarta.json.Json"%>
<%@ page import="jakarta.json.JsonObject"%>
<%@ page import="jakarta.json.JsonReader"%>
<%@ page import="Aux.SessioUtil" %>
<%@ page import="Aux.Imatge"%>
<%@ page import="Aux.ConnectionUtil" %>
<%@ page import="java.net.HttpURLConnection"%>
<%@ page import="java.net.URL"%>
<%@ page import="Aux.ConnectionUtil"%>

<!DOCTYPE html>

<%  
    String addr = ConnectionUtil.getServerAddr();
    String addrFront = ConnectionUtil.getServerAddrFrontend();
    
    // Obté la HttpSession
    HttpSession sessio = request.getSession(false);

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
    }

    //Guardo la imatge amb id en un objecte imatge i verifico que l'autor és qui la intenta eliminar
    String id = request.getParameter("id");
    Imatge imatge = null;

    // Verifica que 'id' no sigui nul
    if (id != null) {
        HttpURLConnection connection  = null;
        try {
            //CONNEXIO GET IMATGE AMB ID
            URL url = new URL("http://" + addr + "/api/searchID/" + id);
            connection = (HttpURLConnection) url.openConnection();
            String token = (String) sessio.getAttribute("tokenJWT");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                    JsonObject jsonResponse = jsonReader.readObject();
                    JsonObject jsonImatge = jsonResponse.getJsonObject("data");
                    imatge = Imatge.jsonToImatge(jsonImatge);
                }
                connection.disconnect();
            } else if (responseCode == 404) {
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
            if (connection != null) connection.disconnect();
            System.out.println("error:");System.out.println(e.getMessage());
            request.setAttribute("tipus_error", "connexio");
            request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei REST, torna-ho a intentar més tard");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
            return;
        }
    }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Visualitzador</title>
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleOpcions.css">
    </head>
    <body>
        <div align="center">
            
            <h1>Visualitzador d'Imatges</h1>
            
            <%
                out.println("<h2>Imatge: "+imatge.getFilename()+"</h2>"); 
                out.println("<img src='http://"+ addrFront + "/images/" + imatge.getFilename() + "' style='max-width: 650px; max-height: 425px;'>");      
            %>
            <br>
            <% out.println("<a href='http://" + addrFront + "/download-image/" + imatge.getFilename() + "' target='_blank'>"
                            + "<button class='boto'>Download</button>"
                         + "</a>"); %>

            <br><br>
            <button onclick="goBack()" class="boto">Enrere</button>
            <br>
            <a href="menu.jsp" class='boto'>Menú</a>
        </div>
            
        <script>
            function goBack() {
                // Utiliza window.history par retrocedir una pàgina en el navegador
                window.history.back();
            }
        </script>
    </body>
</html>
