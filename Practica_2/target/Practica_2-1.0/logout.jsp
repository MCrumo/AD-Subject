<%-- 
    Document   : logout
    Created on : 14 oct 2023, 19:59:06
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% Boolean success = (Boolean) request.getAttribute("success"); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Logout</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
         <link rel="stylesheet" type="text/css" href="./css/styleLogin.css">
    </head>
    <body>
        <div align ="center">
        <h1>LOGOUT</h1>
        <%
        
        HttpSession sessio = request.getSession(false);

        if (sessio != null && sessio.getAttribute("username") != null) {
            sessio.invalidate();
            out.println("Has tancat la sessió correctament");
            
        } else {
            out.println("Hi ha hagut un problema intentant tancar la sessió,");
            out.println("torna-ho a intentar més tard.");
        }
        
        out.println("<br><div align='center'><a href='login.jsp'>Tornar a la pàgina de login</a></div>");
        %>
        </div>
    </body>
</html>
