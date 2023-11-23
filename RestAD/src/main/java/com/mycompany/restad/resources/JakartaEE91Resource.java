//package ad.restad.resources;
package com.mycompany.restad.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


//Añadidos
import DB.Database;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author Nacho i Miquel
 */
@Path("jakartaee9")
public class JakartaEE91Resource {
    
    @GET
    public Response ping(){
        return Response
                .ok("ping Jakarta EE")
                .build();
    }

    /**
      * OPERACIONES DEL SERVICIO REST
      */

    /**
    * POST method to login in the application
    * @param username
    * @param password
    * @return
    */
    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        Database db = new Database();
        if (db.validUser(username, password)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    /**
    * POST method to check username existence
    * @param username
    * @return
    */
    @Path("userExists")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userExists(@FormParam("username") String username) {
        Database db = new Database();
        if (db.checkUsername(username)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
    * POST method to register a new image – File is not uploaded
    * @param title
    * @param description
    * @param keywords
    * @param author
    * @param creator
    * @param capt_date
    * @param filename
    * @return
    */
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerImage (@FormParam("title") String title, @FormParam("description") String description,
                                   @FormParam("keywords") String keywords, @FormParam("author") String author,
                                   @FormParam("creator") String creator, @FormParam("capture") String capt_date,
                                   @FormParam("filename") String filename) {
        try {
            Database db = new Database();
            db.registrarImatge(title, description, keywords, author, creator, capt_date, filename);
            
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * POST method to modify an existing image
    * @param id
    * @param title
    * @param description
    * @param keywords
    * @param author
    * @param creator, used for checking image ownership
    * @param capt_date
    * @param filename
    * @return
    */
    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyImage (@FormParam("id") String id, @FormParam("title") String title, @FormParam("description") String description,
                                 @FormParam("keywords") String keywords, @FormParam("author") String author, @FormParam("creator") String creator,
                                 @FormParam("capture") String capt_date, @FormParam("filename") String filename) {
        try {
            Database db = new Database();
            db.modificaImatge(id, title, description, keywords, creator, capt_date, filename);

            return Response.ok().build();
        } catch (Exception e) {
            // Cualquier otra excepción, devuelve 500 Internal Server Error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * POST method to delete an existing image
    * @param id
    * @return
    */
    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteImage (@FormParam("id") String id) {
        try {
            Database db = new Database();
            db.eliminaImatge(id);

            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * GET method to list images
    * @return
    */
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listImages () {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getAllImatges();
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
    * GET method to search images by id
    * @param id
    * @return
    */
    @Path("searchID/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByID(@PathParam("id") int id) {
        try {
            Database db = new Database();
            JsonObject imageJson = db.getImatgeAmbId(String.valueOf(id));

            if (imageJson != null && !imageJson.isEmpty()) {
                return Response.ok(imageJson).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * GET method to search images by title
    * @param title
    * @return
    */
    @Path("searchTitle/{title}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByTitle (@PathParam("title") String title) {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getImatgesByTitle(title);
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * GET method to search images by creation date. Date format should be
    * yyyy-mm-dd
    * @param date
    * @return
    */
    @Path("searchCreationDate/{date}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByCreationDate (@PathParam("date") String date) {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getImatgesByCreationDate(date);
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * GET method to search images by author
    * @param author
    * @return
    */
    @Path("searchAuthor/{author}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByAuthor (@PathParam("author") String author) {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getImatgesByAuthor(author);
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
    * GET method to search images by keyword
    * @param keywords
    * @return
    */
    @Path("searchKeywords/{keywords}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByKeywords (@PathParam("keywords") String keywords) {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getImatgesByKeyword(keywords);
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    /**
    * GET method to get the last used id
    * @return
    */
    @Path("getNextId")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNextId () {
        Database db = new Database();
        int nextId = db.getNextId();
        
        // Creem un JSON amb el valor de nextId
        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("nextId", String.valueOf(nextId))
            .build();

        // Retornem el JSON en la resposta
        return Response.ok(jsonResponse.toString(), MediaType.APPLICATION_JSON).build();
    }
    
    
     /**
    * GET method to search images by coincidence on: title, description, author, keywords and captureDate 
    * @param coincidence
    * @return
    */
    @Path("searchCoincidence/{coincidence}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByKeyCoincidence (@PathParam("coincidence") String coincidence) {
        try {
            Database db = new Database();
            JsonArray jsonArray =  db.getImatgesByCoincidence(coincidence);
            // Retornem el JSON en la resposta
            return Response.ok(jsonArray.toString(), MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }
    
    /**
    * POST method to upload an image
    * @param fileInputStream
    * @param fileDetail
    * @param id
    * @return
    */
    @Path("uploadImage/")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImage(@FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition fileDetail,
                                @FormDataParam("id") String id) {
        // Lógica para guardar la imagen en disco y registrar la información en la base de datos
        // Retorna la respuesta apropiada
        return Response.ok().build();
    }

    /**
    * GET method to download an image
    * @param id
    * @return Part image que corresponde con el id
    */
    @Path("downloadImage/{id}")
    @GET
    @Produces("image/*")
    public Response downloadImage(@PathParam("id") String id) {
        //DUMMY CODE, FALTA IMPLEMENTACIÓ COMPLETA!!!

        // Lógica para recuperar la imagen del disco (reemplaza "ruta/a/tu/imagen.jpg" con la ruta real)
        final String imagePath = "var/webapp/images/filename.jpg";

        StreamingOutput stream;
        stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try (FileInputStream fis = new FileInputStream(imagePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
            }
        };

        return Response.ok(stream).build();
    }
    
}