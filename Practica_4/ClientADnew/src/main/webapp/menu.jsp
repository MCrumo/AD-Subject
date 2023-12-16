<%-- 
    Document   : menu
    Created on : 5 oct 2023, 12:41:30
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="Aux.SessioUtil" %>

    
    <%
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
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Menú:</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleMenu.css">
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body>
        <div align="center">
        <h1>Menú principal</h1>
        
        <%   out.println("<h3>Benvingut/da " + sessio.getAttribute("username") + "!</h3>");%>
        
        <%-- Mostrar missatge ok si s'ha eliminat una imatge correctament --%>
        <%
            int eliminatOk = 0;
            String okParam = request.getParameter("ok");

            if (okParam != null) {
                try {
                    eliminatOk = Integer.parseInt(okParam);
                } catch (NumberFormatException e) {
                    // Handle the case where the parameter is not a valid integer
                    eliminatOk = 0;
                }
            }
        %>
        
        <% if (eliminatOk == 1) { %>
                <br>
                <p style="color: green;">Imatge eliminada correctament!</p>
                <br>
        <% } %>
        
        <p><a href="registrarImagen.jsp" class="enllaçMenu">Registrar Imatge</a></p>
        <p><a href="list" class="enllaçMenu">Llistar Imatges</a></p>  
        <p><a href="buscarImagen.jsp" class="enllaçMenu">Buscar Imatges</a></p>
        <br>
        <p><a href="listOtherGroup" class="enllaçMenu">Llistar Imatges (Rest altre grup)</a></p>  
        <br>    
        <form action="logout.jsp" method = "POST">
            <button type="submit" class="boto"> Tancar Sessió </button>
        </form>
        </div>
    </body>
</html>
