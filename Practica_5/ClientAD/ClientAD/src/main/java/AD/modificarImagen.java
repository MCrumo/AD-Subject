package AD;

import Aux.ConnectionUtil;
import Aux.Imatge;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpSession;

import Aux.SessioUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    
    //Verifica que la data no és més enllà de la data d'avui
    private boolean verificaData(String captureDate) {
        LocalDate storageDate = LocalDate.now();
        
        LocalDate dataFormulari = LocalDate.parse(captureDate);
        
        return !dataFormulari.isAfter(storageDate);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (SessioUtil.validaSessio(request.getSession(false)) == 0) {
            HttpSession sessio = request.getSession(false);
            String token = (String) sessio.getAttribute("tokenJWT");
            
            String titleMod = request.getParameter("title");
            String descriptionMod = request.getParameter("description");
            String keywordsMod = request.getParameter("keywords");
            String authorMod = request.getParameter("author");
            String captureDateMod = request.getParameter("captureDate");
            
            String id = request.getParameter("id");
            String addr = ConnectionUtil.getServerAddr();
            
            if (id == null) {
                response.sendRedirect("menu.jsp");
                return;
            } else {
                Imatge imatge = null;
                try {
                    //CONNEXIO GET IMATGE AMB ID
                    URL url = new URL("http://" + addr + "/api/searchID/" + id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        try (JsonReader jsonReader = Json.createReader(connection.getInputStream())) {
                            JsonObject jsonResponse = jsonReader.readObject();
                            JsonObject jsonImatge = jsonResponse.getJsonObject("data");
                            imatge = Imatge.jsonToImatge(jsonImatge);
                        }
                        connection.disconnect();
                    } else if (responseCode == 404) {
                        connection.disconnect();    
                        request.setAttribute("tipus_error", "connexio");
                        request.setAttribute("msg_error", "No s'ha trobat la imatge amb id: " + id);
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                        return;
                    } else {
                        connection.disconnect();
                        request.setAttribute("tipus_error", "connexio");
                        RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                        rd.forward(request, response);
                        return;
                    }
                    
                } catch (Exception e) {
                    request.setAttribute("tipus_error", "connexio");
                    request.setAttribute("msg_error", "No s'ha pogut establir connexió amb el servei REST, torna-ho a intentar més tard");
                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                    rd.forward(request, response);
                    return;
                }
                
                String username = (String) sessio.getAttribute("username");
                
                boolean modified = false;
                if (imatge.getCreator().equals(username)) {
                    List<String> errors = new ArrayList<>();
                    
                    //Si s'ha modificat el titol i és valid, ho actualitzem a l'objecte imatge
                    if (!titleMod.equals(imatge.getTitle())) {
                        if (titleMod.contains(" ")) {
                            errors.add("El títol no pot contenir espais");
                        } else {
                            imatge.setTitle(titleMod);
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
                        try {
                            URL url = new URL("http://"+ addr +"/api/modify");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestProperty("Authorization", "Bearer " + token);
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);

                            String postData = String.format("id=%s&title=%s&description=%s&keywords=%s&author=%s&capt_date=%s",
                                    imatge.getId(), imatge.getTitle(), imatge.getDescription(), imatge.getKeywords(),
                                    imatge.getAuthor(), imatge.getCaptureDate());

                            try (OutputStream os = connection.getOutputStream()) {
                                byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                                os.write(input, 0, input.length);
                            }

                            int responseCode = connection.getResponseCode();
                            connection.disconnect();
                            
                            switch (responseCode) {
                                case 200:
                                    request.setAttribute("ok", true);
                                    RequestDispatcher dispatcher = request.getRequestDispatcher("modificarImagen.jsp?id=" + id);
                                    dispatcher.forward(request, response);
                                    break;
                                
                                case 404:
                                    request.setAttribute("tipus_error", "modificar");
                                    request.setAttribute("msg_error", "No existeix la imatge amb tal id");
                                    RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                                    rd.forward(request, response);
                                    break;
                            
                                case 403:
                                    request.setAttribute("tipus_error", "modificar");
                                    request.setAttribute("msg_error", "No ets el propietari de la imatge!");
                                    RequestDispatcher rd1 = request.getRequestDispatcher("error.jsp");
                                    rd1.forward(request, response);
                                    break;
                                
                                default:
                                    request.setAttribute("tipus_error", "modificar");
                                    request.setAttribute("msg_error", "Hi ha hagut un error en modificar les dades a la base de dades al servei REST");
                                    RequestDispatcher rd2 = request.getRequestDispatcher("error.jsp");
                                    rd2.forward(request, response);
                                    break;
                            }
                        } catch (Exception e) {
                            request.setAttribute("tipus_error", "connection");
                            request.setAttribute("msg_error", "No s'ha pogut connectar amb el servei REST. Torna-ho a intentar més tard");
                            RequestDispatcher rd = request.getRequestDispatcher("error.jsp");
                            rd.forward(request, response);
                        }
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
