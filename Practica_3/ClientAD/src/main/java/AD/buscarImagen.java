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

                String modeBusqueda = request.getParameter("modeBusqueda"); //keyword, title, author
                String description = request.getParameter("descripcio");

                if (modeBusqueda == null || description == null) {
                    response.sendRedirect("menu.jsp");
                    return;
                }
                List<Imatge> setImatges = new ArrayList();
                if (!description.isEmpty()){
                    //String[] keyWords = description.split(",");
                    String[] keyWordsMalicious = description.split("\\s+");
                    String[] keyWords = purgeArray(keyWordsMalicious);
                     
                    for(int i = 0; i < keyWords.length && (i < 16); ++i){ 
                        /* ------------------------------------------------------------ 
                         * -----SEARCH-BY-KEYWORD-------------------------------------- 
                         * ------------------------------------------------------------ 
                         */
                        if (modeBusqueda.equals("keyword")){
                            String keywords = keyWords[i];
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchKeywords/"+keywords);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                            if (!idExist) setImatges.add(imatge);
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
                                connection.disconnect();
                            }
                        }
                        /* ------------------------------------------------------------ 
                         * -----SEARCH-BY-TITLE---------------------------------------- 
                         * ------------------------------------------------------------ 
                         */
                        else if (modeBusqueda.equals("title")){
                            String title = keyWords[i];
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchTitle/"+title);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                            if (!idExist) setImatges.add(imatge);
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
                                connection.disconnect();
                            }
                        }
                        /* ------------------------------------------------------------ 
                         * -----SEARCH-BY-AUTHOR--------------------------------------- 
                         * ------------------------------------------------------------ 
                         */
                        else if (modeBusqueda.equals("author")){
                            String author = keyWords[i];
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchAuthor/"+author);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                            if (!idExist) setImatges.add(imatge);
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
                                connection.disconnect();
                            }
                        }
                        /* ------------------------------------------------------------ 
                         * -----SEARCH-BY-CREATION_DATE-------------------------------- 
                         * ------------------------------------------------------------ 
                         */
                        else if (modeBusqueda.equals("creationDate")){
                            String date = keyWords[i];
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchCreationDate/"+date);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                            if (!idExist) setImatges.add(imatge);
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
                                connection.disconnect();
                            }
                        }
                        /* ------------------------------------------------------------ 
                         * -----SEARCH-BY-COINCIDENCES--------------------------------- 
                         * ------------------------------------------------------------ 
                         */
                        else { // .equals("all")
                            String coincidence = keyWords[i];
                            HttpURLConnection connection = null;
                            try {
                                URL url = new URL("http://localhost:8080/RestAD/resources/jakartaee9/searchCoincidence/"+coincidence);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonArray jsonImatges = jsonReader.readArray();
                                        for(JsonValue jsonValue : jsonImatges){
                                            JsonObject jsonImatge = (JsonObject) jsonValue;
                                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                            boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                            if (!idExist) setImatges.add(imatge);
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
                                connection.disconnect();
                            }
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
    
    
    static String[] purgeArray(String[] potencialMaliciosa) {
        List<String> purgedList = new ArrayList<>();
        for (String palabra : potencialMaliciosa) {
            // Remove: punto y coma, comillas simples y comillas dobles
            String purgedString = palabra.replaceAll("[;'\"]", "");
            purgedList.add(purgedString);
        }
        return purgedList.toArray(new String[0]);
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
