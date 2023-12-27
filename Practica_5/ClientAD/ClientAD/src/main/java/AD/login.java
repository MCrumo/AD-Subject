/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import Aux.ConnectionUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;
import java.net.HttpURLConnection;

/**
 *
 * @author nacho i miquel
 */
@WebServlet(name = "login", urlPatterns = {"/login"})
public class login extends HttpServlet {
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String user = request.getParameter("username");
        String password = request.getParameter("password");
        String addr = ConnectionUtil.getServerAddr();
        
        try {
            //URL url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/login");
            URL url = new URL("http://" + addr + "/login/" + user + "/" + password);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer el JSON de la respuesta del backend
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder responseStringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseStringBuilder.append(line);
                    }

                    // Parsear el JSON
                    JSONObject jsonResponse = new JSONObject(responseStringBuilder.toString());
                    
                    // Obtener el id y el token del JSON
                    String id = jsonResponse.getJSONObject("data").getString("id");
                    String token = jsonResponse.getJSONObject("data").getString("token");

                    // Almacenar el id y el token en la sesión del usuario
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", id); // o puedes usar "username" según lo que necesites
                    session.setAttribute("tokenJWT", token);

                    response.sendRedirect("menu.jsp");
                }
            } else {
                request.setAttribute("tipus_error", "login");
                request.setAttribute("msg_error", "El nom d'usuari o la contrasenya son incorrectes.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }

            // Estem a GET, permetem la sortida de dades
            //connection.setDoOutput(true);

            /*String postData = "user=" + username + "&password=" + password;
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }*

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                HttpSession sessio = request.getSession(true);
                sessio.setAttribute("username", username);
                sessio.setAttribute("tokenJWT", username);

                response.sendRedirect("menu.jsp");
            } else {
                request.setAttribute("tipus_error", "login");
                request.setAttribute("msg_error", "El nom d'usuari o la contrasenya son incorrectes.");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
            
            connection.disconnect();*/
        } catch (Exception e) {
            request.setAttribute("tipus_error", "connexio-login");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
