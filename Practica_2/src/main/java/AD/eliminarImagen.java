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

//importamos la classe Database
import DB.Database;
import Aux.Imatge;
import Aux.SessioUtil;
import java.io.File;
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
    private boolean elimina(String filename) {

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
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (SessioUtil.validaSessio(request.getSession(false))) {
            String id = request.getParameter("id");
            
            if (id == null) {
                response.sendRedirect("login.jsp");
            } else {
                Database db = new Database();
                Imatge imatge = db.getImatgeAmbId(id);
                
                HttpSession sessio = request.getSession(false);
                String username = (String) sessio.getAttribute("username");
                if (imatge.getCreator().equals(username) && elimina(imatge.getFilename())) {
                    if (db.eliminaImatge(id)) {
                        //out.println("Deleted the file: " + "Images/" + img.filename);
                        response.sendRedirect("menu.jsp");
                    } else {
                        request.setAttribute("tipus_error", "eliminar");
                        request.setAttribute("msg_error", "No s'ha pogut eliminar la imatge de la base de dades, torna-ho a intentar més tard");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }
                } else {
                    request.setAttribute("tipus_error", "eliminar");
                    request.setAttribute("msg_error", "Has intentat eliminar una imatgte que no era teva o no s'ha pogut eliminar de disc, torna-ho a intentar més tard");
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
