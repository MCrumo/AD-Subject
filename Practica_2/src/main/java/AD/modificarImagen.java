/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AD;

import Aux.Imatge;
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
import Aux.SessioUtil;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Servlet que recoge los datos de la imagen a modificar y
    actualiza la base de datos y/o el fichero de la imagen. En caso de que se pueda
    modificar la imagen correctamente, mostrará un mensaje y dará la opción de
    volver al menú. En caso de error, redireccionará al usuario a la página de error.
 */
@WebServlet(name = "modificarImagen", urlPatterns = {"/modificarImagen"})
public class modificarImagen extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    private boolean modificaTitol(Imatge imatge, String titleMod) {
        String filename = imatge.getFilename(); //es de la forma "1_nombre.png"
        
        String extensio = filename.substring(filename.lastIndexOf('.') + 1); //obte l'extensio de l'arxiu
        String filenameMod = imatge.getId() + "_" + titleMod + "." + extensio;
        
        String pathAntic = Imatge.getPath() + "/" + filename; System.out.println(pathAntic);
        String pathNou = Imatge.getPath() + "/" + filenameMod; System.out.println(pathNou);
        
        File arxiuAntic = new File(pathAntic);
        File arxiuNou = new File(pathNou);

        // Renombrar l'arxiu
        System.out.println("abans de renombrar");
        if (arxiuAntic.renameTo(arxiuNou)) {
            // Actualizar el nombre en la instancia de Imatge
            imatge.setFilename(filenameMod);
            // Actualizar el título en la instancia de Imatge
            imatge.setTitle(titleMod);
            System.out.println("hem renombrat");
            return true;
        } else {
            return false;
        }
    }
    
    //Verifica que la data no és més enllà de la data d'avui
    private boolean verificaData(String captureDate) {
        LocalDate storageDate = LocalDate.now();
        
        LocalDate dataFormulari = LocalDate.parse(captureDate);
        
        return !dataFormulari.isAfter(storageDate);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessioUtil.validaSessio(request.getSession(false))) {
            String titleMod = request.getParameter("title");
            String descriptionMod = request.getParameter("description");
            String keywordsMod = request.getParameter("keywords");
            String authorMod = request.getParameter("author");
            String captureDateMod = request.getParameter("captureDate");
            
            String id = request.getParameter("id");
            
            if (id == null) {
                response.sendRedirect("login.jsp");
            } else {
                Database db = new Database();
                Imatge imatge = db.getImatgeAmbId(id);
                
                HttpSession sessio = request.getSession(false);
                String username = (String) sessio.getAttribute("username");
                
                boolean modified = false;
                if (imatge.getCreator().equals(username)) {
                    List<String> errors = new ArrayList<>();
                    
                    //Si s'ha modificat el titol i és valid, actualitzem també el nom de l'arxiu
                    if (!titleMod.equals(imatge.getTitle())) {
                        if (titleMod.contains(" ")) {
                            errors.add("El títol no pot contenir espais");
                        } else {
                            if (modificaTitol(imatge, titleMod)) modified = true;
                            else {
                                request.setAttribute("tipus_error", "modificar");
                                request.setAttribute("msg_error", "Hi ha hagut un error en modificar el titol de la imatge, torna-ho a provar més tard");
                                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                rd.forward(request, response);
                            }
                        }
                    }
                    //Si ha canviat la desccripció, ho actualitzem a l'objecte imatge
                    if (!descriptionMod.equals(imatge.getDescription())) {
                        imatge.setDescription(descriptionMod);
                        modified = true;
                    }
                    //Si han canviat les keywords, ho actualitzem a l'objecte imatge
                    if (!keywordsMod.equals(imatge.getKeywords())) {
                        imatge.setKeywords(keywordsMod);
                        modified = true;
                    }
                    //Si ha canviat l'autor, ho actualitzem a l'objecte imatge
                    if (!authorMod.equals(imatge.getAuthor())) {
                        imatge.setAuthor(authorMod);
                        modified = true;
                    }
                    //Si ha canviat la data de captura, ho modifiquem a l'objecte imatge
                    if (!captureDateMod.equals(imatge.getCaptureDate())) {
                        if (verificaData(captureDateMod)){
                            imatge.setCaptureDate(captureDateMod);
                            modified = true;
                        } else {
                            errors.add("La data de captura no pot ser en el futur!");
                        }
                    }
                    
                    //Si hi ha errors, els mostrem per pantalla
                    if (!errors.isEmpty()) { 
                        request.setAttribute("errors", errors);
                        RequestDispatcher dispatcher = request.getRequestDispatcher("modificarImagen.jsp?id=" + id);
                        dispatcher.forward(request, response);
                    } else if (modified) { //sinó, modifiquem tots els valors a la base de dades.
                        boolean ok = db.modificaImatge(imatge);
                        if (!ok) {
                            request.setAttribute("tipus_error", "modificar");
                            request.setAttribute("msg_error", "Hi ha hagut un error en modificar les dades a la base de dades");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                        }
                        request.setAttribute("ok", ok);
                        RequestDispatcher dispatcher = request.getRequestDispatcher("modificarImagen.jsp?id=" + id);
                        dispatcher.forward(request, response);
                    }
                
                } else {
                    request.setAttribute("tipus_error", "modificar");
                    request.setAttribute("msg_error", "Has intentat modificar una imatgte que no era teva...");
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
