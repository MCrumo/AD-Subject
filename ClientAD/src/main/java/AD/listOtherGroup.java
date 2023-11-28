/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import Aux.ConnectionUtil;
import Aux.Imatge;
import Aux.ImatgeAdapter;
import Aux.SessioUtil;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

/**
 *
 * @author nacho
 */
@WebServlet(name = "listOtherGroup", urlPatterns = {"/listOtherGroup"})
public class listOtherGroup extends HttpServlet {

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
            String addr = ConnectionUtil.getServerAddr();
            HttpURLConnection connection = null;
            //List<Imatge> setImatges = new ArrayList();
            
            try {
                URL url = new URL("http://"+ addr +"/RestAD-G2/resources/jakartaee9/list");
                System.out.println("Nos conectamos a: " + url.toString());
                
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(connection.getInputStream(), "multipart/mixed"));

                    // Obtenim el JSON de la part del formulari
                    jakarta.mail.BodyPart jsonPart = multipart.getBodyPart(0);
                    InputStream jsonInputStream = jsonPart.getInputStream();

                    // Convertim InputStream a String
                    String jsonContent = new BufferedReader(new InputStreamReader(jsonInputStream)).lines().collect(Collectors.joining("\n"));

                    System.out.println(jsonContent);

                    Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Imatge.class, new ImatgeAdapter())
                                    .create();
                    Type listType = new TypeToken<List<Imatge>>() {}.getType();
                    List<Imatge> llista = gson.fromJson(jsonContent, listType);
                    
                    System.out.println("llistat: " + llista);
                    
                    connection.disconnect();
                    request.setAttribute("setImatges", llista);
                    request.getRequestDispatcher("list-other-group.jsp").forward(request, response);
                    //RequestDispatcher dispatcher = request.getRequestDispatcher("list.jsp");
                    //dispatcher.forward(request, response);
                    //response.sendRedirect("list.jsp");
                    
                    // CODI NOSTRE
                    /*try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                        JsonArray jsonImatges = jsonReader.readArray();
                        for (JsonValue jsonValue : jsonImatges) {
                            JsonObject jsonImatge = (JsonObject) jsonValue;
                            Imatge imatge = Imatge.jsonToImatge(jsonImatge);
                            setImatges.add(imatge);
                        }
                        // Para que la JSP lo utilice
                        request.setAttribute("setImatges", setImatges);
                        // Redirige al JSP
                        request.getRequestDispatcher("list.jsp").forward(request, response);
                    }*/
                } else {
                    request.setAttribute("tipus_error", "connexio");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace(); 
               request.setAttribute("tipus_error", "connexio-login");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
                connection.disconnect();
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
