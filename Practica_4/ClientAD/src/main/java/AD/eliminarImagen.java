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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Servlet que recoge los datos de la imagen a eliminar y
    elimina tanto el fichero como la información de la base de datos. En caso de que se
    pueda eliminar la imagen correctamente, mostrará un mensaje y dará la opción de
    volver al menú. En caso de error, redireccionará al usuario a la página de error.
 */
@WebServlet(name = "eliminarImagen", urlPatterns = {"/eliminarImagen"})
public class eliminarImagen extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*private boolean elimina(String filename) {

        // Concatenem el path general i el nom de l'arxiu
        String fullPath = Imatge.getPath() + File.separator + filename;

        // Crea un File sobre la ruta completa
        File imatge = new File(fullPath);

        // Verifiquem que existeix
        if (imatge.exists()) {
            try {
                Path archivoPath = Paths.get(fullPath);
                Files.delete(archivoPath);
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
        
        return true;
    }*/
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (SessioUtil.validaSessio(request.getSession(false)) == 0) {
            String addr = ConnectionUtil.getServerAddr();
            String id = request.getParameter("id");
            
            if (id == null) {
                response.sendRedirect("menu.jsp");
            } else {
                Imatge imatge = null;
                HttpURLConnection connection = null;
                try {
                    //CONNEXIO GET IMATGE AMB ID
                    URL url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/searchID/"+id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                            JsonObject jsonImatge = jsonReader.readObject();
                            imatge = Imatge.jsonToImatge(jsonImatge);
                        }
                        
                        connection.disconnect();
                    } else {
                        connection.disconnect();
                        request.setAttribute("tipus_error", "connexio");
                        request.setAttribute("msg_error", "No s'ha trobat la imatge amb id: " + id + ". Error intern del servidor");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                        return;
                    }
                } catch (Exception e) {
                    if (connection != null) connection.disconnect();
                    request.setAttribute("tipus_error", "connexio");
                    request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei REST, torna-ho a intentar més tard");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                    return;
                }
                
                
                if (imatge != null) {
                    HttpSession sessio = request.getSession(false);
                    String username = (String) sessio.getAttribute("username");
                    if (imatge.getCreator().equals(username)) {
                        connection = null;
                        try {

                            //CONNEXIO GET IMATGE AMB ID
                            URL url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/delete/");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");

                            // Permetem la sortida de dades
                            connection.setDoOutput(true);
                            
                            String postData = "id=" + id;
                            try (OutputStream os = connection.getOutputStream()) {
                                byte[] input = postData.getBytes("utf-8");
                                os.write(input, 0, input.length);
                            }
                            
                            int responseCode = connection.getResponseCode();
                            connection.disconnect();
                            
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                /*request.setAttribute("ok", 1);
                                RequestDispatcher dispatcher = request.getRequestDispatcher("menu.jsp");
                                dispatcher.forward(request, response);*/
                                
                                /*request.setAttribute("ok", 1);
                                response.setStatus(HttpServletResponse.SC_FOUND);
                                response.setHeader("Location", "menu.jsp");
                                response.sendRedirect("menu.jsp");*/
                                
                                request.setAttribute("ok", 1);
                                String encodedURL = response.encodeRedirectURL("menu.jsp?ok=1");
                                response.sendRedirect(encodedURL);
                            } else {
                                System.out.println("resp: "+responseCode);
                                request.setAttribute("tipus_error", "eliminar");
                                request.setAttribute("msg_error", "No s'ha pogut eliminar la imatge de la base de dades, torna-ho a intentar més tard");
                                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                rd.forward(request, response);
                            }
                            
                        } catch (Exception e) {
                            if (connection != null) connection.disconnect();
                            System.out.println("ERROR: " + e.getMessage());
                            request.setAttribute("tipus_error", "connexio");
                            request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei rest, torna-ho a intentar més tard");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                        }
                    } else {
                        request.setAttribute("tipus_error", "eliminar");
                        request.setAttribute("msg_error", "Has intentat eliminar una imatge que no era teva...");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }
                } else {
                    request.setAttribute("tipus_error", "eliminar");
                    request.setAttribute("msg_error", "No s'ha pogut obtenir la imatge de la base de dades.");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                }
            }
            
        } else {
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
