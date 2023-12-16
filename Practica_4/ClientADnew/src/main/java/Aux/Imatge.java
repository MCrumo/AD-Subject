/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

import jakarta.json.JsonObject;
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
                  String creator, String captureDate , String filename) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.keywords = reemplasaEspais(keywords);
        this.author = author;
        this.creator = creator;
        String aux = null;
        if (captureDate != null) aux  = validaFormatStringData(captureDate);
        this.captureDate = aux;
        this.filename = filename;
    }
    
    public static Imatge jsonToImatge(JsonObject jsonObject) {
        Imatge imatge = new Imatge();

        imatge.setId(jsonObject.getString("id", ""));
        imatge.setTitle(jsonObject.getString("title", ""));
        imatge.setDescription(jsonObject.getString("description", ""));
        imatge.setKeywords(jsonObject.getString("keywords", ""));
        imatge.setAuthor(jsonObject.getString("author", ""));
        imatge.setCreator(jsonObject.getString("creator", ""));
        imatge.setCaptureDate(jsonObject.getString("captureDate", null));
        imatge.setStorageDate(jsonObject.getString("storageDate", null));
        imatge.setFilename(jsonObject.getString("filename", ""));

        return imatge;
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
    
    //Retorna la data en format ISO
    public String getCaptureDateISO() {
        DateTimeFormatter formatPropi = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter formatISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate dataPropia = LocalDate.parse(captureDate, formatPropi);

        return dataPropia.format(formatISO);
    }
    
    public void setCaptureDate(String captureDate) {
        this.captureDate = validaFormatStringData(captureDate);
    }
    
    public void setCaptureDate(LocalDate captureDate) {
        this.captureDate = formataDataAString(captureDate);
    }
    
    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;
    }
    
    public String getStorageDate() {
        return this.storageDate;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

