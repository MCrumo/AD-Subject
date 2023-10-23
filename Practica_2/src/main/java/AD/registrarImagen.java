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

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
    Servlet que recoge los datos de la imagen y los guarda en la
    base de datos. También debe leer la imagen y guardarla en un directorio de la
    aplicación web. En caso de que se pueda registrar la imagen correctamente,
    mostrará un mensaje y dará la opción de volver al menú o registrar otra imagen. En
    caso de error, redireccionará al usuario a la página de error.
 */

@WebServlet(name = "registrarImagen", urlPatterns = {"/registrarImagen"})
@MultipartConfig
public class registrarImagen extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
    //private final String path = "/var/webapp/Practica_2/images";
    private Imatge imatge = null;    
        
    //Crea un objecte Imatge i inicialitza els seus atributs
    boolean guardaAuxImatge (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //guardem els atributs del formulari en variables
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String keywords = request.getParameter("keywords");
        String author = request.getParameter("author");
        String captureDate = request.getParameter("captureDate");
        Part imagePart = request.getPart("image");

        //Array per si algun argument és incorrecte poder mostrar l'error en pantalla
        List<String> errors = new ArrayList<>();
        
        if (title != null && title.contains(" ")) {
            errors.add("El títol no pot contenir espais");
        }
        
        //guardem el id de la foto
        Database db = new Database();
        int nextId = db.getNextId();
        
        //guardem el nom d'usuari
        HttpSession sessio = request.getSession(false);
        String username = (String) sessio.getAttribute("username");
        
        //guardem la data
        LocalDate storageDate = LocalDate.now();
        
        //Verifico que la data introduida no és posterior a la data d'avui
        LocalDate dataFormulari = LocalDate.parse(captureDate);
        if (dataFormulari.isAfter(storageDate)) {
            errors.add("La imatge que vols penjar no s'ha pogut prendre en el futur!");
        }
        
        //Verifiquem que la imatge es png, jpeg o gif
        String contentType = imagePart.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            /*request.setAttribute("tipus_error", "registrar");
            request.setAttribute("msg_error", "El tipus d'arxiu no es vàlid. Només es poden pujar arxius .jpeg, .png i .gif");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);

            return false;*/
            errors.add("El tipus d'arxiu no es vàlid. Només es poden pujar arxius .jpeg, .png i .gif");
        }

        String extensio;
        switch (contentType) {
            case "image/gif":
                extensio = "gif";
                break;
            case "image/png":
                extensio = "png";
                break;
            default:
                extensio = "jpeg";
                break;
        }

        String filename = nextId + "_" + title + "." + extensio;
        
        //Si hi ha errors, sortim i retornem false. Els printarem a la pàgina web
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            RequestDispatcher dispatcher = request.getRequestDispatcher("registrarImagen.jsp");
            dispatcher.forward(request, response);
            return false;
        }
        
        imatge = new Imatge(String.valueOf(nextId), title, description, keywords, author, username, captureDate, storageDate, filename, imagePart);
        
        return true;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessioUtil.validaSessio(request.getSession(false))) {
            try { //Intentem guardar la imatge
                if (guardaAuxImatge(request, response)) { //si hem pogut guardar l'objecte imatge
                    File directori = new File(Imatge.getPath());
                    if (!directori.exists()) {
                        try {
                            directori.mkdirs();
                            directori.setReadable(true);
                            directori.setWritable(true);
                        } catch (Exception e) {
                            request.setAttribute("tipus_error", "registrar");
                            request.setAttribute("msg_error", "No existeix la ruta /var/webapp/Practica_2/images/ . Crea-la i otorga-li els permisos necessaris");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                        }
                    }

                    Part imagePart = request.getPart("image");
                    File file = new File(Imatge.getPath(), imatge.getFilename());

                    try (InputStream input = imagePart.getInputStream()) {
                        Files.copy(input, file.toPath());
                    } catch (Exception e) {
                        request.setAttribute("tipus_error", "registrar");
                        request.setAttribute("msg_error", "Ha succeït un error, revisa que el directori /var/webapp/Practica_2/images/ existeixi i que els permisos son adeqüats");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }

                    Database db = new Database();
                    db.registrarImatge(imatge);

                    response.sendRedirect("registreOk.jsp");
                }
            } catch (Exception e) {
                request.setAttribute("tipus_error", "registrar");
                //System.out.println(e.getMessage());
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
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
