/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import Aux.ConnectionUtil;
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
        HttpSession sessio = request.getSession(false);
        if (SessioUtil.validaSessio(sessio) == 0) {
            
            String addr = ConnectionUtil.getServerAddr();
    
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
                                URL url = new URL("http://"+ addr +"/api/searchKeyword/"+keywords);
                                connection = (HttpURLConnection) url.openConnection();
                                //NOVA IMP
                                String token = (String) sessio.getAttribute("tokenJWT");
                                connection.setRequestProperty("Authorization", "Bearer " + token);
                                //--------
                                System.out.println("Tocken Enviat");
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();

                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    System.out.println("Connexio correcta");
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonObject jsonResponse = jsonReader.readObject();
                                        int resultCode = jsonResponse.getInt("result");
                                        if (resultCode == 0){
                                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");
                                            for (JsonValue jsonValue : jsonImatges) {
                                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                                boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                                if (!idExist) setImatges.add(imatge);
                                            } 
                                        }
                                    }
                                } else {
                                    System.out.println("Response code: "+responseCode);
                                    request.setAttribute("tipus_error", "connexio");
                                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                    rd.forward(request, response);
                                }
                                connection.disconnect();
                            } catch (Exception e) {
                                System.out.println("Error connexio");
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
                                URL url = new URL("http://"+ addr +"/api/searchTitle/"+title);
                                connection = (HttpURLConnection) url.openConnection();
                                //NOVA IMP
                                String token = (String) sessio.getAttribute("tokenJWT");
                                connection.setRequestProperty("Authorization", "Bearer " + token);
                                //--------
                                System.out.println("Tocken Enviat");
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();

                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    System.out.println("Connexio correcta");
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonObject jsonResponse = jsonReader.readObject();
                                        int resultCode = jsonResponse.getInt("result");
                                        if (resultCode == 0){
                                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");
                                            for (JsonValue jsonValue : jsonImatges) {
                                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                                boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                                if (!idExist) setImatges.add(imatge);
                                            } 
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
                                URL url = new URL("http://"+ addr +"/api/searchAuthor/"+author);
                                connection = (HttpURLConnection) url.openConnection();
                                //NOVA IMP
                                String token = (String) sessio.getAttribute("tokenJWT");
                                connection.setRequestProperty("Authorization", "Bearer " + token);
                                //--------
                                System.out.println("Tocken Enviat");
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();

                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    System.out.println("Connexio correcta");
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonObject jsonResponse = jsonReader.readObject();
                                        int resultCode = jsonResponse.getInt("result");
                                        if (resultCode == 0){
                                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");
                                            for (JsonValue jsonValue : jsonImatges) {
                                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                                boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                                if (!idExist) setImatges.add(imatge);
                                            } 
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
                                System.out.println("Connexio correcta");
                                if (!isValidDateFormat(date)){
                                    System.out.println(date+" is a VALID format");
                                    date = "1111-11-11";
                                } else System.out.println(date+" is a INVALID format");
                                URL url = new URL("http://"+ addr +"/api/searchCaptureDate/"+date);
                                connection = (HttpURLConnection) url.openConnection();
                                //NOVA IMP
                                String token = (String) sessio.getAttribute("tokenJWT");
                                connection.setRequestProperty("Authorization", "Bearer " + token);
                                //--------
                                System.out.println("Tocken Enviat");
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();

                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonObject jsonResponse = jsonReader.readObject();
                                        int resultCode = jsonResponse.getInt("result");
                                        if (resultCode == 0){
                                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");
                                            for (JsonValue jsonValue : jsonImatges) {
                                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                                boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                                if (!idExist) setImatges.add(imatge);
                                            } 
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
                                URL url = new URL("http://"+ addr +"/api/searchCoincidence/"+coincidence);
                                connection = (HttpURLConnection) url.openConnection();
                                //NOVA IMP
                                String token = (String) sessio.getAttribute("tokenJWT");
                                connection.setRequestProperty("Authorization", "Bearer " + token);
                                //--------
                                System.out.println("Tocken Enviat");
                                connection.setRequestMethod("GET");
                                connection.setDoOutput(true);
                                int responseCode = connection.getResponseCode();

                                System.out.println(responseCode);
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    System.out.println("Connexio correcta");
                                    try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                                        JsonObject jsonResponse = jsonReader.readObject();
                                        int resultCode = jsonResponse.getInt("result");
                                        if (resultCode == 0){
                                            JsonArray jsonImatges = jsonResponse.getJsonArray("data");
                                            for (JsonValue jsonValue : jsonImatges) {
                                                JsonObject jsonImatge = (JsonObject) jsonValue;
                                                Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                                                boolean idExist = setImatges.stream().anyMatch(img -> img.getId().equals(imatge.getId()));
                                                if (!idExist) setImatges.add(imatge);
                                            } 
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
    
    private static boolean isValidDateFormat(String input) { // yyyy-mm-dd
        if (input.length() != 10) return false;
        for (int i = 0; i < 4; ++i) 
            if (!isNumber(input.charAt(i))) return false;
        if (input.charAt(4) != '-') return false;
        for (int i = 5; i < 7; ++i) 
            if (!isNumber(input.charAt(i))) return false;
        if (input.charAt(7) != '-') return false;
        for (int i = 8; i < 10; ++i) 
            if (!isNumber(input.charAt(i))) return false;
        return true;
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9';
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
