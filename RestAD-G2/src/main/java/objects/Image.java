/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

/**
 *
 * @author alumne
 */
public class Image {
    public Integer ID;
    public String title;
    public String description;
    public String keywords;
    public String author;
    public String creator;
    public String creationDate;
    public String registrationDate;
    public String filename;
    
    public Image(Integer ID, String title, String description, String keywords, String author, String creator, String filename) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.author = author;
        this.creator = creator;
        this.filename = filename;
    }
    
    public Integer getID() {
        return ID;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public String getCreationDate() {
        return creationDate;
    }
    
    public String getRegistrationDate() {
        return registrationDate;
    }
    
    public String getFilename() {
        return filename;
    }
}
