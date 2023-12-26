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

import Aux.SessioUtil;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessioUtil.validaSessio(request.getSession(false)) == 0) {
            String addr = ConnectionUtil.getServerAddr();
            String id = request.getParameter("id");
            
            if (id == null) {
                response.sendRedirect("menu.jsp");
            } else {
                HttpSession sessio = request.getSession(false);
                HttpURLConnection connection = null;

                try {
                    //CONNEXIO GET IMATGE AMB ID
                    URL url = new URL("http://"+ addr +"/api/delete");
                    connection = (HttpURLConnection) url.openConnection();
                    String token = (String) sessio.getAttribute("tokenJWT");
                    connection.setRequestProperty("Authorization", "Bearer " + token);
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
                    
                    switch (responseCode) {
                        case 201: // OK
                            request.setAttribute("ok", 1);
                            String encodedURL = response.encodeRedirectURL("menu.jsp?ok=1");
                            response.sendRedirect(encodedURL);
                            break;
                        
                        case 404:
                            request.setAttribute("tipus_error", "eliminar");
                            request.setAttribute("msg_error", "No existeix la imatge amb tal id");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                            break;

                        case 400:
                            request.setAttribute("tipus_error", "error");
                            request.setAttribute("msg_error", "Error intern del servidor");
                            RequestDispatcher rd1 = request.getRequestDispatcher("error.jsp");
                            rd1.forward(request, response);
                            break;

                        case 500:
                            request.setAttribute("tipus_error", "error");
                            request.setAttribute("msg_error", "Error en la base de dades");
                            RequestDispatcher rd2 = request.getRequestDispatcher("error.jsp");
                            rd2.forward(request, response);
                            break;

                        default:
                            request.setAttribute("tipus_error", "error");
                            RequestDispatcher rd3 = request.getRequestDispatcher("error.jsp");
                            rd3.forward(request, response);
                            break;
                    }
                } catch (Exception e) {
                    if (connection != null) connection.disconnect();
                    System.out.println("ERROR: " + e.getMessage());
                    request.setAttribute("tipus_error", "connexio");
                    request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei rest, torna-ho a intentar més tard");
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
