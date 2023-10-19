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
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
        <link rel="stylesheet" type="text/css" href="./css/styleLogin.css">
        <link rel="icon" type="image/x-icon" href="./css/imgs/camera-circle.png">
    </head>
    <body>
        <div align="center">
        <h1>Inici de sessió:</h1>
        <form action = "login" method = "POST">
            <p>
                <input type="text" name="username" placeholder="Nom d'usuari" required>
            </p>
            <p>
                <input type="password" name="password" placeholder="Contrasenya" required>
            </p>
            <input type="submit" value="Inicia Sesió">
        </form>  
        <br><br>
        <form action="logout.jsp" method = "POST">
            <button type="submit"> Tancar Sessió </button>
        </form>
        </div>
    </body>
</html>
