<%-- 
    Document   : menu
    Created on : 5 oct 2023, 12:41:30
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <!-- Verificació de la sessio HTTP-->
    <%@ page import="DB.Database" %>
    <%@ page import="jakarta.servlet.http.HttpSession" %>

    
    <%
        // Obté la HttpSession
        HttpSession sessio = request.getSession(false);

        // Verifica si la HttpSession no es nula i si existeix un atribut "username"
        if (sessio != null && sessio.getAttribute("username") != null) {
            // Obté el nom d'usuari de la sessió
            String username = (String) sessio.getAttribute("username");

            // Crea una instància de ls classe Database
            Database db = new Database();
            
            // Crida al mètode checkUsername para verificar si nom d'usuari existeix en la base de dades
            boolean userExists = db.checkUsername(username);

            // Si no exiteix l'usuari, envia a la pàgina d'error pertinent
            if (!userExists) {
                request.setAttribute("tipus_error", "autenticacio");
                request.setAttribute("msg_error", "La sessió no és vàlida.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
        } else { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
            request.setAttribute("tipus_error", "autenticacio");
            request.setAttribute("msg_error", "La sessió no està iniciada.");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }
    %>
    
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Menú:</title>
        <link rel="stylesheet" type="text/css" href="./css/styleGeneral.css">
    </head>
    <body>
        <div align="center">
        <h1>Menú principal</h1>
        
        <%   out.println("<h3>Benvingut " + sessio.getAttribute("username") + "!</h3>");%>
        
        <p><a href="registrarImagen.jsp">Registrar Imatge</a></p>
        <p><a href="listImg.jsp">Llistar Imatges</a></p>
        <p><a href="buscarImagen.jsp">Buscar Imatges</a></p>
        
        <br>
        <form action="logout.jsp" method = "POST">
            <button type="submit" class="boto"> Tancar Sessió </button>
        </form>
        </div>
    </body>
</html>
