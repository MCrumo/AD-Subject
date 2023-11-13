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
import jakarta.json.JsonObject;

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
        
        //NO ES GUARDA BÉ A LA DB I GUARDA MALAMENT EL NOM DE LARXIU, NO ACCEDEIXO AL JSON XDDD
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
    * @return
    */
    @Path("modify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyImage (@FormParam("id") String id, @FormParam("title") String title, @FormParam("description") String description,
                                 @FormParam("keywords") String keywords, @FormParam("author") String author, @FormParam("creator") String creator,
                                 @FormParam("capture") String capt_date) {
        return Response.ok().build();
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
            return Response.ok().build();
    }

    /**
    * GET method to list images
    * @return
    */
    @Path("list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listImages () {
return Response.ok().build();
    }

    /**
    * GET method to search images by id
    * @param id
    * @return
    */
    @Path("searchID/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByID (@PathParam("id") int id) {
return Response.ok().build();
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
return Response.ok().build();
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
return Response.ok().build();
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
return Response.ok().build();
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
return Response.ok().build();

    }
    
    /**
    * GET method to get the last used id
    * @return
    */
    @Path("getNextId")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchByKeywords () {
        Database db = new Database();
        int nextId = db.getNextId();
        
        // Creem un JSON amb el valor de nextId
        JsonObject jsonResponse = Json.createObjectBuilder()
            .add("nextId", nextId)
            .build();

        // Retornem el JSON en la resposta
        return Response.ok(jsonResponse.toString(), MediaType.APPLICATION_JSON).build();
    }
    
    //PROVA GIT MERGE TOCAT PER MQUEL I NACHO
}