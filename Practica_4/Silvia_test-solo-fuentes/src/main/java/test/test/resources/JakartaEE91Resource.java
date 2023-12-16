package test.test.resources;

import jakarta.servlet.http.Part;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author 
 */
@Path("jakartaee9")
public class JakartaEE91Resource {
    
    final public static String uploadDir = "/var/webapp/testGlassfish/upload/";
    
    @GET
    public Response ping(){
        return Response
                .ok("ping Jakarta EE")
                .build();
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
        
        Integer StatusCode = 201;                
   
        if (!writeImage(filename, fileInputStream)) { //no sha pogut guardar la imatge            
            StatusCode = 500; //fallada server
            String ErrorName = "general";
        }
        
        return Response
            .status(StatusCode)
            .build();
    }
       
    public static Boolean writeImage(String file_name, Part part) 
        throws IOException {
        makeDirIfNotExists();
        
        InputStream content = part.getInputStream();
        
        File targetfile = new File(uploadDir + file_name);
        
        java.nio.file.Files.copy(
                content,
                targetfile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
        
        return true;
    }
    public static Boolean writeImage(String file_name, InputStream fileInputStream)  {
        try{
            makeDirIfNotExists();
            File targetfile = new File(uploadDir + file_name);
        
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
    
    
    public static Boolean deleteImage(String file_name) 
    {
        makeDirIfNotExists();
        
        File targetfile = new File(uploadDir + file_name);
        if(! targetfile.delete()) {
            System.out.println("ERROR: Failed to delete " + targetfile.getAbsolutePath());
            return false;
        }
       
        System.out.println("SUCCESS: deleted " + targetfile.getAbsolutePath());
        return true;
    }    
    
        private static void makeDirIfNotExists() {
        File dir = new File(uploadDir);
        // Creamos directorio si no existe.
        if (! dir.exists() ) {
           dir.mkdirs();
        }
    }
}
