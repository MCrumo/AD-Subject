<%-- 
    Document   : registreOk
    Created on : Oct 22, 2023, 6:37:12 PM
    Author     : nacho
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="jakarta.servlet.http.HttpSession" %>
    <%@ page import="Aux.SessioUtil" %>

    
    <%
        // Obté la HttpSession
        //HttpSession sessio = request.getSession(false);
        
        int res = SessioUtil.validaSessio(request.getSession(false));
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
        <title>Registre Correcte</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleMenu.css">
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body>
        <div align="center">
            <h1>Imatge registrada correctament</h1>
            <p>Què vols fer a continuació?</p>
            <br>
            <p align='center'><a href='registrarImagen.jsp' class="enllaçMenu">Registrar una altra imatge</a></p>
            <p align='center'><a href='menu.jsp' class="enllaçMenu">Tornar al menu principal</a></p>
        </div>
    </body>
</html>
