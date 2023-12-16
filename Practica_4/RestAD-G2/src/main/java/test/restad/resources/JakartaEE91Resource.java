package test.restad.resources;

import db.DataBase;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.util.List;
import objects.Image;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author 
 */
@Path("jakartaee9")
public class JakartaEE91Resource {
    
    final public static String UPLOAD_DIR = "/home/nacho/NetBeansProjects/AD-Subject/RestAD/src/main/webapp/images/";
    
    @Path("ping")
    @GET
    public Response ping(){
        return Response
                .ok("ping Jakarta EE")
                .build();
    }
    
    /**
    * POST method to login the application
    * @param username
    * @param password
    * @return
    */
   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   @Produces(MediaType.APPLICATION_JSON)
   public Response login(@FormParam("username") String username, @FormParam("password") String password) {
       
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        
        try {            
            if (db.login_usr(c, username, password)) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }                
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
        } finally {
            db.close_connection(c);
            System.out.println("[OK] RestAD login");
        }

   }
   
    /** 
    * POST method to register a new image 
    * @param title 
    * @param description 
    * @param keywords      
    * @param author 
    * @param creator 
    * @param capt_date     
    * @param filename     
    * @param fileInputStream     
    * @param fileMetaData     
    * @return 
    */ 
    @Path("register") 
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA) 
    @Produces(MediaType.APPLICATION_JSON) 
    public Response registerImage (@FormDataParam("title") String title, 
            @FormDataParam("description") String description, 
            @FormDataParam("keywords") String keywords, 
            @FormDataParam("author") String author, 
            @FormDataParam("creator") String creator, 
            @FormDataParam("capture") String capt_date,
            @FormDataParam("filename") String filename,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        
        DataBase db = new DataBase();
        Connection c = db.open_connection();
             
        try {              
   
            int lastID = db.get_max_ID(c) + 1;
            String newID = String.valueOf(lastID);
            String newFilename = null;
        
            if (filename != null) {
                newFilename = newID + "-" + filename;
                if (!writeImage(newFilename, fileInputStream)) {
                    return Response.serverError().build();
                }
            }
            
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String reg_date = currentDate.format(formatter);
            
            if (db.register_img(title, description, keywords, author, creator, 
                    capt_date, reg_date, newFilename, c)) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
        } finally {
            db.close_connection(c);
            System.out.println("[OK] RestAD registerImage");
        }
       
    }
    
    public static Boolean writeImage(String file_name, InputStream fileInputStream)  {
        try{
            makeDirIfNotExists();
            File targetfile = new File(UPLOAD_DIR + file_name);
        
            java.nio.file.Files.copy(
                    fileInputStream,
                    targetfile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );  
        } catch (IOException e){
            return false;
        }
        return true; 
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
    * @param fileInputStream
    * @param fileMetaData
    * @return
    */
    @Path("modify")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyImage (@FormDataParam("id") String id,
            @FormDataParam("title") String title, 
            @FormDataParam("description") String description, 
            @FormDataParam("keywords") String keywords, 
            @FormDataParam("author") String author, 
            @FormDataParam("creator") String creator, 
            @FormDataParam("capture") String capt_date,
            @FormDataParam("filename") String filename,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        
        try {
            String newFilename = filename;
            if (filename != null && !filename.isEmpty()) {
                String oldFilename = db.get_file_name(Integer.parseInt(id), c);
                if (!deleteFile(oldFilename)){
                    System.out.println("old image file doesn't exist");
                }
                newFilename = id + "-" + filename;
                if (!writeImage(newFilename, fileInputStream)) {
                    return Response.serverError().build();
                }
            }
            
            int image_id = Integer.parseInt(id);
            if (db.update_img(image_id, title, description, keywords, author, capt_date, newFilename, c)) {
                return Response.ok().build();
            } else {
                System.out.println("can't update db");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
        } finally {
            db.close_connection(c);
            System.err.println("[OK] RestAD modifyImage");
        }
        return null;
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
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        
        try {
            String fName = db.get_file_name(Integer.parseInt(id), c);
            if (!deleteFile(fName)){
                System.out.println("image file doesn't exist");
            }
            if (db.delete_img(id, c)) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.CREATED).build();
            }
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return Response.serverError().build();
        } finally {
            db.close_connection(c);
            System.out.println("[OK] RestAD deleteImage");
        }
    }
    
    public static Boolean deleteFile(String file_name) 
    {
        makeDirIfNotExists();
        
        File targetfile = new File(UPLOAD_DIR + file_name);
        if(! targetfile.delete()) {
            System.out.println("ERROR: Failed to delete " + targetfile.getAbsolutePath());
            return false;
        }
       
        System.out.println("SUCCESS: deleted " + targetfile.getAbsolutePath());
        return true;
    }  
    
    private static void makeDirIfNotExists() {
        File dir = new File(UPLOAD_DIR);
        // Creamos directorio si no existe.
        if (! dir.exists() ) {
           dir.mkdirs();
        }
    }

     /**
    * GET method to list images
    * @return
    */
    @Path("list")
    @GET
    @Produces("multipart/mixed")
    public Response listImages () {
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        
        try {
            //Construim missatge JSON
            List<Image> img_list = db.list_images(c);
            Gson gson = new Gson();
            String jsonData = gson.toJson(img_list);
            
            FormDataMultiPart multipart = new FormDataMultiPart();
            
            multipart.field("json", jsonData, MediaType.APPLICATION_JSON_TYPE);
            
            for (Image img : img_list) {
                Integer id = img.getID();
                String filename = img.getFilename();
                InputStream imageIS = new FileInputStream(UPLOAD_DIR + filename);
                
                multipart.bodyPart(new FormDataBodyPart(id.toString(), imageIS, MediaType.APPLICATION_OCTET_STREAM_TYPE));
            }
            
            db.close_connection(c);
            System.out.println("[OK] RestAD list");
            
            return Response.ok(multipart, "multipart/mixed").build();
        } catch (Exception e) {
            System.err.print(e.getMessage());
            return Response.serverError().build();
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
    public Response searchByID (@PathParam("id") int id) {
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        String json;
        try {
            Image img = db.search_ID(id, c);
            Gson gson = new Gson();
            json = gson.toJson(img);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
        } finally {
            db.close_connection(c);
            System.out.println("[OK] RestAD searchByID");
        }
        
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }   

    /**
    * GET method to search images
    * @param title
    * @param description
    * @param keywords
    * @param author
    * @param creator
    * @param capt_date
    * @return
    */
    @Path("searchImg")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchImg (@FormParam("title") String title, @FormParam("description") String description, 
                                 @FormParam("keywords") String keywords, @FormParam("author") String author, 
                                 @FormParam("creator") String creator, @FormParam("capture") String capt_date) {
        DataBase db = new DataBase();
        Connection c = db.open_connection();
        
        //Construim missatje JSON
        String json;
        try {
            List<Image> img_list = db.search_images(title, description, keywords, 
            author, creator, capt_date, c);
            Gson gson = new Gson();
            json = gson.toJson(img_list);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Response.serverError().build();
        } finally {
            db.close_connection(c);
            System.out.println("[OK] RestAD searchImg");
        }
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }
}