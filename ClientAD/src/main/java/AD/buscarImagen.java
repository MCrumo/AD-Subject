/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;

import Aux.Imatge;
import Aux.SessioUtil;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author alumne
 */
@WebServlet(name = "buscarImagen", urlPatterns = {"/buscarImagen"})
public class buscarImagen extends HttpServlet {

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
        if (SessioUtil.validaSessio(request.getSession(false)) == 0) {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet buscarImagen</title>");            
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet buscarImagen at " + request.getContextPath() + "</h1>");
                out.println("</body>");
                out.println("</html>");

                String modeBusqueda = request.getParameter("modeBusqueda"); //keyword, title, author
                String description = request.getParameter("descripcio");

                if (modeBusqueda == null || description == null) {
                    response.sendRedirect("menu.jsp");
                    return;
                }
                List<Imatge> setImatges = new ArrayList();
                if (!description.isEmpty()){
                    String[] keyWords = description.split(",");
                    
                    for(int i = 0; i < keyWords.length; ++i){ 
                        //Database db = new Database();
                        //-----SEARCH-BY-KEYWORD------------------------------
                        if (modeBusqueda.equals("keyword")){
                            //List<Imatge> imgsInfo = db.getImatgesByKeyword(keyWords[i]);
                            //setImatges.addAll(imgsInfo);
                        }
                        //-----SEARCH-BY-TITLE------------------------------
                        else if (modeBusqueda.equals("title")){
                            String title = keyWords[i];
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchTitle"+title);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                String postData = "title=" + title;
                                try (OutputStream os = connection.getOutputStream()) {
                                    byte[] input = postData.getBytes("utf-8");
                                    os.write(input, 0, input.length);
                                }
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            setImatges.add(imatge);
                                        }
                                    }
                                } else {
                                    request.setAttribute("tipus_error", "connexio");
                                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                    rd.forward(request, response);
                                }
                                connection.disconnect();
                            } catch (Exception e) {
                                request.setAttribute("tipus_error", "connexio-login");
                                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                rd.forward(request, response);
                            }
                        }
                        /*else if (modeBusqueda.equals("author")){
                            List<Imatge> imgsInfo = db.getImatgesByAuthor(keyWords[i]);
                            setImatges.addAll(imgsInfo);
                        }
                        else if (modeBusqueda.equals("creationDate")){
                            List<Imatge> imgsInfo = db.getImatgesByCreationDate(keyWords[i]);
                            setImatges.addAll(imgsInfo);
                        }*/
                        else { // .equals("all")
                            //List<Imatge> imgsInfo = db.getImatgesByCoincidence(keyWords[i]);
                            //setImatges.addAll(imgsInfo);
                        }
                    }
                    if(setImatges.isEmpty()) request.setAttribute("busquedaBuida", 1);
                    request.setAttribute("setImatges", setImatges);
                }
                else {
                    request.setAttribute("setImatges", null);
                    request.setAttribute("", null);
                }
                RequestDispatcher rd = request.getRequestDispatcher("buscarImagen.jsp");
                rd.forward(request, response);  
                
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
