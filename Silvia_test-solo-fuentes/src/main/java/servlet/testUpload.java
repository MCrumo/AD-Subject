/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

//nous imports pel lab4
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author alumne
 */
@WebServlet(name = "testUpload", urlPatterns = {"/testUpload"})


/**
 *
 * @author alumne
 */

@MultipartConfig()  
public class testUpload extends HttpServlet {
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
   
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String author = request.getParameter("author");
        String keywords = request.getParameter("keyword");
        String cr_date = request.getParameter("cr_date");
        final Part fileP = request.getPart("image");
        String file_name = fileP.getSubmittedFileName();

        System.out.println(title + " " + description + " " + author + " " + keywords + " " + cr_date + " " + file_name);
        
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