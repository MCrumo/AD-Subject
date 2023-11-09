<%-- 
    Document   : showImg
    Created on : 23 oct 2023, 15:56:01
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Aux.Imatge"%>
<%@page import="DB.Database"%>
<!DOCTYPE html>

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

<%    
    String id = request.getParameter("id");
    Database db = new Database();
    Imatge imatge = db.getImatgeAmbId(id);
    if (imatge == null){
        response.sendRedirect("menu.jsp");
        return;
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
                out.println("<img src='images/"+imatge.getFilename()+"' style='max-width: 650px; max-height: 425px;'>");      
            %>
            <br>
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
