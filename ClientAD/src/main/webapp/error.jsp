<%-- 
    Document   : error_authentication
    Created on : 14 oct 2023, 13:11:43
    Author     : alumne
--%>

<!--
Tipus d'error possibles:
    login
    crear_usuari
    autenticacio
    registrar
    eliminar    
    modificar
-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% String tipus_error = (String) request.getAttribute("tipus_error"); %>
<% String msg_error = (String) request.getAttribute("msg_error"); %>
<html>
    <head class="site-icon">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleLogin.css">
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">

    </head>
    <body>
        <div align="center">
        <h1>Error</h1>
        <% 
        if (tipus_error == null) {
            if(msg_error != null) {
                out.println(msg_error);
            }
            
            response.sendRedirect("login.jsp");
        }
        else if (tipus_error.equals("login")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else {
                out.println("S'ha produït un error en intentar intentar iniciar sessió.");
            }
            out.println("<br><div align='center'><a href='login.jsp'>Tornar a la pàgina de login</a></div>");
        }
        else if (tipus_error.equals("crear_usuari")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else {
                out.println("S'ha produït un error en intentar crear un usuari.");
            }
            out.println("<br><div align='center'><a href='login.jsp'>Tornar a la pagina de login</a></div>"); 
        }
        else if (tipus_error.equals("autenticacio")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else out.println("L'usuari no està autenticat.");
            
            out.println("<br><div align='center'><a href='login.jsp'>Tornar a la pagina de login</a></div>"); 
        }
        else if (tipus_error.equals("registrar")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else out.println("S'ha produït un error en intentar registrar la imatge.");
            
            out.println("<br><div align='center'><a href='menu.jsp'>Tornar al menu principal</a></div>"); 
        }
        else if (tipus_error.equals("eliminar")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else out.println("S'ha produït un error en intentar eliminar la imatge.");
            
            out.println("<br><div align='center'><a href='menu.jsp'>Tornar al menu principal</a></div>");                
        }
        else if (tipus_error.equals("modificar")) {
            if(msg_error != null) {
                out.println(msg_error);
            } else out.println("S'ha produït un error en intentar modificar la imatge.");
            
            out.println("<br><div align='center'><a href='menu.jsp'>Tornar al menu principal</a></div>");                
        }
        else {
            response.sendRedirect("login.jsp");
        }       
        %>
    </body>
</html>
