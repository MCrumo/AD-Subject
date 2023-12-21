/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import Aux.ConnectionUtil;
import Aux.Imatge;
import Aux.SessioUtil;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author alumne
 */
@WebServlet(name = "list", urlPatterns = {"/list"})
public class list extends HttpServlet {

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
        HttpSession sessio = request.getSession(false);
        if (SessioUtil.validaSessio(sessio) == 0) {
            String addr = ConnectionUtil.getServerAddr();
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                HttpURLConnection connection = null;
                List<Imatge> setImatges = new ArrayList();
                try {
                    URL url = new URL("http://"+ addr +"/api/list");
                    connection = (HttpURLConnection) url.openConnection();

                    //NOVA IMP
                    String token = (String) sessio.getAttribute("tokenJWT");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    //--------

                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);


                    int responseCode = connection.getResponseCode();
                    System.out.println(responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        
                        //NOVA IMP
                        try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                            JsonObject jsonResponse = jsonReader.readObject();
                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");

                            for (JsonValue jsonValue : jsonImatges) {
                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                setImatges.add(imatge);
                            }


                            System.out.println("Dades de les imatges rebudes correctament del backend.");
                            // Para que la JSP lo utilice
                            request.setAttribute("setImatges", setImatges);
                            // Redirige al JSP
                            request.getRequestDispatcher("list.jsp").forward(request, response);
                        }
                    } else {
                        request.setAttribute("tipus_error", "connexio");
                        request.setAttribute("msg_error", "lololololololol");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }
                    connection.disconnect();


                } catch (Exception e) {
                    request.setAttribute("tipus_error", "connexio-login");
                    request.setAttribute("msg_error", e.getMessage());
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                    connection.disconnect();
                }
            }
        }
        else {
            request.setAttribute("tipus_error", "autenticacio");
            request.setAttribute("msg_error", "La sessió no és vàlida.");
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
