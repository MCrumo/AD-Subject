/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

import jakarta.servlet.http.Part;
import java.util.Date;

/**
 *
 * @author nacho
 */
public class Imatge {
    private String id = "";
    private String title = "";
    private String description = "";
    private String keywords = "";
    private String author = "";
    private String creator = "";
    private Date captureDate = null;
    private Date storageDate = null;
    private String filename = "";
    private Part part;

    public Imatge() {
    }

    public Imatge(String id, String title, String description, String keywords, String author,
                  String creator, Date captureDate, Date storageDate, String filename, Part part) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.author = author;
        this.creator = creator;
        this.captureDate = captureDate;
        this.storageDate = storageDate;
        this.filename = filename;
        this.part = part;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
        
    /** Puc passar la data com a Date com a String i me la converteix, també li puc fer el get sobre els dos formats**/
    
    public Date getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(Date captureDate) {
        this.captureDate = captureDate;
    }

    public Date getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(Date storageDate) {
        this.storageDate = storageDate;
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date fechaNacimiento = dateFormat.parse(fechaNacimientoStr);
            // Ahora tienes la fecha como un objeto Date
        } catch (ParseException e) {
            // Manejo de la excepción en caso de que la cadena no sea una fecha válida
            e.printStackTrace();
        }*/
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}

