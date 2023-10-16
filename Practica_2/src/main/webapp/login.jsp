<%-- 
    Document   : login
    Created on : 21 sept 2023, 13:13:45
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <link rel="stylesheet" type="text/css" href="./css/styleLogin.css">
    </head>
    <body>
        <div align="center">
        <h1>LOGIN:</h1>
        <form action = "login" method = "POST">
            <p>
                Username: <input type="text" name="username" required>
            </p>
            <p>
                Password: <input type="password" name="password" required>
            </p>
            <input type="submit" value="Inicia Sesió" class="boto">
        </form>  
        <br>
        <form action="logout.jsp" method = "POST">
            <button type="submit" class="boto"> Tancar Sessió </button>
        </form>
        </div>
    </body>
</html>
