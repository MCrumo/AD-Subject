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

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;


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
    private String addr = ConnectionUtil.getServerAddr();
        
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
        
        String nextId = "0";
        try {
            //guardem el id de la foto
            URL url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/getNextId");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                    JsonObject jsonResponse = jsonReader.readObject();
                    nextId = jsonResponse.getString("nextId");
                }
            } else {
                request.setAttribute("tipus_error", "connexio");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
            
            connection.disconnect();
        } catch (Exception e) {
            request.setAttribute("tipus_error", "connexio");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        }

        //guardem el nom d'usuari
        HttpSession sessio = request.getSession(false);
        String username = (String) sessio.getAttribute("username");
        
        
        //Verifico que la data introduida no és posterior a la data d'avui
        LocalDate storageDate = LocalDate.now();
        LocalDate dataFormulari = LocalDate.parse(captureDate);
        if (dataFormulari.isAfter(storageDate)) {
            errors.add("La imatge que vols penjar no s'ha pogut prendre en el futur!");
        }
        
        //Verifiquem que la imatge es png, jpeg o gif
        String contentType = imagePart.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
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
        
        imatge = new Imatge(nextId, title, description, keywords, author, username, captureDate, filename);
        
        return true;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int res = SessioUtil.validaSessio(request.getSession(false));
        if (res == 0) {
            try { //Intentem guardar la imatge
                if (guardaAuxImatge(request, response)) { //si hem pogut guardar l'objecte imatge
                    
                    final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                    StreamDataBodyPart filePart = new StreamDataBodyPart("file", fileP.getInputStream());
                    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                    final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart
                            .field("title", title, MediaType.TEXT_PLAIN_TYPE)
                            .field("description", description, MediaType.TEXT_PLAIN_TYPE)
                            .field("keywords", keywords, MediaType.TEXT_PLAIN_TYPE)
                            .field("author", author, MediaType.TEXT_PLAIN_TYPE)
                            .field("creator", author, MediaType.TEXT_PLAIN_TYPE)
                            .field("capture", cr_date, MediaType.TEXT_PLAIN_TYPE)
                            .field("filename", file_name, MediaType.TEXT_PLAIN_TYPE)
                            .bodyPart(filePart);
                    
                    String postData = "title=" + imatge.getTitle() +
                                          "&description=" + imatge.getDescription() +
                                          "&keywords=" + imatge.getKeywords() +
                                          "&author=" + imatge.getAuthor() +
                                          "&creator=" + imatge.getCreator() +
                                          "&capture=" + imatge.getCaptureDate() +
                                          "&filename=" + imatge.getFilename();

                    final WebTarget target = client.target("http://localhost:8080/test/resources/jakartaee9/register");
                    final Response resp = target.request().post(Entity.entity(multipart, multipart.getMediaType()));
                    int status = resp.getStatus();

                    formDataMultiPart.close();
                    multipart.close();

                    switch (status) {
                        case 201:
                            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/success?id=PhotoOk"));
                            break;
                        case 409:
                            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/error?err=imageExists"));
                            break;
                        case 406:
                            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/error?err=invalidFormat"));
                            break;
                        case 500:
                            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/error?err=general"));
                            break;
                        default:
                            break;
                    } 
                    
                    
                    /*File directori = new File(Imatge.getPath());
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
                    }*

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

                    try {
                        URL url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/register");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");

                        // Estem a post, permetem la sortida de dades
                        connection.setDoOutput(true);

                        //String postData = "username=" + username + "&password=" + password;
                        String postData = "title=" + imatge.getTitle() +
                                          "&description=" + imatge.getDescription() +
                                          "&keywords=" + imatge.getKeywords() +
                                          "&author=" + imatge.getAuthor() +
                                          "&creator=" + imatge.getCreator() +
                                          "&capture=" + imatge.getCaptureDate() +
                                          "&filename=" + imatge.getFilename();
                        try (OutputStream os = connection.getOutputStream()) {
                            byte[] input = postData.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            response.sendRedirect("registreOk.jsp");
                        } else {
                            request.setAttribute("tipus_error", "connexio");
                            request.setAttribute("msg_error", "Error en connectar amb el servei REST");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                        }
                    }  catch (Exception e) {
                        request.setAttribute("tipus_error", "connexio");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                    }*/
                }
            } catch (Exception e) {
                request.setAttribute("tipus_error", "registrar");
                RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                rd.forward(request, response);
            }
        } else if (res == -2) { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
            request.setAttribute("tipus_error", "autenticacio");
            request.setAttribute("msg_error", "La sessió no està iniciada.");
            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
            rd.forward(request, response);
        } else { // res = -1 o qualsevol altre
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
