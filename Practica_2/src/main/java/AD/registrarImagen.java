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
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.time.LocalDate;

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
    
    //Path on es guardaran les imatges
    private final String path = "/var/webapp/Practica_2/images";
    private Imatge imatge = null;
    
    //Cal afegir els metodes de guardar la data , potser un metode que retorni l'extensio, tot lo de la db i taliqual
    
    
    
    //Crea un objecte Imatge i inicialitza els seus atributs
    boolean guardaAuxImatge (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //guardem els atributs del formulari
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String keywords = request.getParameter("keywords");
        String author = request.getParameter("author");
        String captureDate = request.getParameter("captureDate");
        Part imagePart = request.getPart("image");

        //guardem el id de la foto
        Database db = new Database();
        int nextId = db.getNextId();
        
        //guardem el nom d'usuari
        HttpSession sessio = request.getSession(false);
        String username = (String) sessio.getAttribute("username");
        
        //guardem la data
        LocalDate storageDate = LocalDate.now();
        
        //Verifiquem que la imatge es png, jpeg o gif
        String contentType = imagePart.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            request.setAttribute("tipus_error", "registrar");
            request.setAttribute("msg_error", "El tipus d'arxiu no es vàlid. Només es poden pujar arxius .jpeg, .png i .gif");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);

            return false;
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
        
        
        
        imatge = new Imatge(String.valueOf(nextId), title, description, keywords, author, username, captureDate, storageDate, filename, imagePart);
        
        return true;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet registrarImagen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet registrarImagen at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");

            try { //Intentem guardar la imatge
                System.out.println("Entrem al primer try:\n");
                if (guardaAuxImatge(request, response)) {
                    System.out.println("objecte imatge creat\n");
                    File directori = new File(path);
                    if (!directori.exists()) {
                        directori.mkdirs();
                        directori.setReadable(true);
                        directori.setWritable(true);
                        System.out.println("Creo directoris\n");
                    }
                    
                    String uploadPath = path + File.separator + imatge.getFilename();
                    try (OutputStream outputStream = new FileOutputStream(new File(uploadPath))) {
                        // Creem un OutputStream per guardar la imatge
                        int bytesRead;
                        byte[] buffer = new byte[8192];

                        Part imagePart = request.getPart("image");
                        
                        try (InputStream fileContent = imagePart.getInputStream()) {
                            // Escribim les dades de la imatge al OutputStream
                            while ((bytesRead = fileContent.read(buffer, 0, 8192)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            
                            fileContent.close();
                            System.out.println("imatge guardada a disc\n");
                            Database db = new Database();
                            db.registrarImatge(imatge);
                            System.out.println("imatge a la db\n");
                        }
                        
                    } catch (Exception e) {
                        request.setAttribute("tipus_error", "registrar");
                        request.setAttribute("msg_error", "El directori /var/webapp/Practica_2/images/ no existeix. Crea'l i posa tots els permisos");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }
                }
            } catch (Exception e) {
                request.setAttribute("tipus_error", "registrar");
                System.out.println(e.getMessage());
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
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
