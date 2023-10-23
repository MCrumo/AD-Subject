/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

import jakarta.mail.internet.ParseException;
import jakarta.servlet.http.Part;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author nacho
 */
public class Imatge {
    //Path on es guardaran les imatges
    private static String path = "/var/webapp/Practica_2/images";
    
    private String id = "";
    private String title = "";
    private String description = "";
    private String keywords = "";
    private String author = "";
    private String creator = "";
    private String captureDate = null;
    private String storageDate = null;
    private String filename = "";
    private Part part;

    
    //Retorna la data en el format adequat per guardar yyyy/MM/dd
    private static String validaFormatStringData (String data) {
        String auxData = data;
        if (data.contains("-")) {
            DateTimeFormatter formatOriginal = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dataLocal = LocalDate.parse(data, formatOriginal);

            DateTimeFormatter nouFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            auxData = dataLocal.format(nouFormat);
        }
        
        return auxData;
    }
    
    //Converteix de LocalDate a String formatat com yyyy/MM/dd
    private static String formataDataAString (LocalDate data) {
        DateTimeFormatter formatData = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dataFormatada = data.format(formatData);        
        return dataFormatada;
    }
    
    private static String reemplasaEspais(String input) {
        // Reemplaça múltiples espais per una coma, s'assegura de no duplicar comes
        String res = input;
        if (input.isBlank()) res = input.replaceAll("\\s+,\\s*", ", ");
        return res;
    }
    
    public Imatge() {
    }

    public Imatge(String id, String title, String description, String keywords, String author,
                  String creator, String captureDate, String storageDate, String filename, Part part) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = reemplasaEspais(keywords);
        this.author = author;
        this.creator = creator;
        this.captureDate = validaFormatStringData(captureDate);
        this.storageDate = validaFormatStringData(storageDate);
        this.filename = filename;
        this.part = part;
    }
    
    public Imatge(String id, String title, String description, String keywords, String author,
                  String creator, String captureDate, LocalDate storageDate, String filename, Part part) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = reemplasaEspais(keywords);
        this.author = author;
        this.creator = creator;
        this.captureDate = validaFormatStringData(captureDate);
        this.storageDate = formataDataAString(storageDate);
        this.filename = filename;
        this.part = part;
    }

    public static String getPath() {
        return path;
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
        this.keywords = reemplasaEspais(keywords);
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
            
    public String getCaptureDate() {
        return captureDate;
    }
    
    public String getStorageDate() {
        return storageDate;
    }
    
    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }

    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;        
    }
    
    public void setCaptureDate(LocalDate captureDate) {
        this.captureDate = formataDataAString(captureDate);
    }
    
    public void setStorageDate(LocalDate storageDate) {
        this.captureDate = formataDataAString(storageDate);;
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

