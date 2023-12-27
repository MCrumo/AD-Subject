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

import Aux.Imatge;
import Aux.SessioUtil;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        
        //Verifico que la data introduida no és posterior a la data d'avui
        LocalDate storageDate = LocalDate.now();
        LocalDate dataFormulari = LocalDate.parse(captureDate);
        if (dataFormulari.isAfter(storageDate)) {
            errors.add("La imatge que vols penjar no s'ha pogut prendre en el futur!");
        }
        
        //Verifiquem que la imatge es png, jpeg o gif
        String contentType = imagePart.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/gif")) {
            errors.add("El tipus d'arxiu no es vàlid. Només es poden pujar arxius .jpeg, .jpg, .png i .gif");
        }
        
        //Si hi ha errors, sortim i retornem false. Els printarem a la pàgina web
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            RequestDispatcher dispatcher = request.getRequestDispatcher("registrarImagen.jsp");
            dispatcher.forward(request, response);
            return false;
        }
        
        imatge = new Imatge("-1", title, description, keywords, author, "", captureDate, "");
        
        return true;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int res = SessioUtil.validaSessio(request.getSession(false));
        if (res == 0) {
            try { //Intentem guardar la imatge
                if (guardaAuxImatge(request, response)) { //si hem pogut guardar l'objecte imatge
                    
                    final Part fileP = request.getPart("image");
                    
                    final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
                    /*ClientConfig clientConfig = new ClientConfig();
                    clientConfig.register(MultiPartFeature.class);
                    
                    Client client = ClientBuilder.newClient(clientConfig);*/
                    StreamDataBodyPart filePart = new StreamDataBodyPart("image", fileP.getInputStream(), request.getPart("image").getContentType());
                    
                    FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
                    final FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart
                            .field("title", imatge.getTitle(), MediaType.TEXT_PLAIN_TYPE)
                            .field("description", imatge.getDescription(), MediaType.TEXT_PLAIN_TYPE)
                            .field("keywords", imatge.getKeywords(), MediaType.TEXT_PLAIN_TYPE)
                            .field("author", imatge.getAuthor(), MediaType.TEXT_PLAIN_TYPE)
                            .field("capt_date", imatge.getCaptureDate(), MediaType.TEXT_PLAIN_TYPE)
                            .bodyPart(filePart);
                    

                    final WebTarget target = client.target("http://" + addr + "/api/register-image");

                    HttpSession sessio = request.getSession(false);
                    String token = (String) sessio.getAttribute("tokenJWT");

                    final Response resp = target.request().header("Authorization", "Bearer " + token)
                                                          .post(Entity.entity(multipart, multipart.getMediaType()));

                    int status = resp.getStatus();
                    System.out.println("status:" + status);
                    switch (status) {
                        case 201:
                            response.sendRedirect("registreOk.jsp");
                            break;
                        case 400:
                            String message1 = "";
                            try (InputStream inputStream = resp.readEntity(InputStream.class);
                                JsonReader jsonReader = Json.createReader(inputStream)) {
                                JsonObject jsonResponse = jsonReader.readObject();
                                JsonObject jsonData = jsonResponse.getJsonObject("data");
                                JsonArray messagesArray = jsonData.getJsonArray("message");

                                // Convertir el array de mensajes en un solo String
                                for (Object jsonValue : messagesArray) {
                                    message1 += jsonValue.toString() + ", ";
                                }
                            
                                // Eliminar la última coma y espacio si existen
                                if (message1.endsWith(", ")) {
                                    message1 = message1.substring(0, message1.length() - 2);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (!message1.isEmpty()) {
                                request.setAttribute("msg_error", message1);
                                request.setAttribute("msg_error", "prueba en response");
                            }
                            //RequestDispatcher rd1 = request.getRequestDispatcher("error.jsp");
                            //rd1.forward(request, response);
                            response.sendRedirect("error.jsp");

                            break;
                        default:
                            request.setAttribute("tipus_error", "error");
                            String message2 = "";
                            try (InputStream inputStream = resp.readEntity(InputStream.class);
                                JsonReader jsonReader = Json.createReader(inputStream)) {
                                JsonObject jsonResponse = jsonReader.readObject();
                                JsonObject jsonData = jsonResponse.getJsonObject("data");
                                message2 = jsonData.getString("message");
                            } 
                            if (message2 != "") request.setAttribute("msg_error", message2);
                            RequestDispatcher rd2 = request.getRequestDispatcher("error.jsp");
                            rd2.forward(request, response);
                            break;
                    }

                    formDataMultiPart.close();
                    multipart.close();
                }
            } catch (Exception e) {
                request.setAttribute("tipus_error", "registrar");
                System.out.println(e.getMessage());
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
