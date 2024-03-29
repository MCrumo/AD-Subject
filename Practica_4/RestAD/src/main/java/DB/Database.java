/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DB;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.json.JsonObject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;

//import Aux.Imatge;

/**
 *
 * @author miquel i nacho
 */
public class Database {
    private Connection connection = null;
    
    private static final String Url = "jdbc:derby://localhost:1527/pr2";
    private static final String Usr = "pr2";
    private static final String Pswd = "pr2";
    
    //obre una connexió amb la DB
    private void openConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(Url,Usr,Pswd);
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    //tanca la connexió oberta amb la DB
    private void closeConnection() {
        Connection tmp = connection;
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException ex) {
            // connection close failed.
            connection = tmp;
            System.err.println(ex.getMessage());
        }
    }
    
    //verifca que l'usuari i contrasenya passats com a paràmetre
    //estàn a la DB
    public boolean validUser(String username, String password) {
        openConnection();
        
        boolean existeix = false;
        
        String sql = "SELECT * FROM PR2.USUARIOS WHERE ID_USUARIO = ? AND PASSWORD = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();
            
            existeix = rs.next();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        closeConnection();
        return existeix;
    }
    
    //verifica que el nom d'usuari existeix a la base de dades,
    //s'utilitza per validar la sessio Http
    public boolean checkUsername(String username) {
        openConnection();
        boolean existeix = false;
        
        String sql = "SELECT * FROM PR2.USUARIOS WHERE ID_USUARIO = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            
            existeix = rs.next();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        closeConnection();
        return existeix;
    }
    
    //Retorna el id de la última foto introduïda
    public int getNextId() {
        int id = 0;
        openConnection();
        
        String sql = "SELECT MAX(ID) AS max_id FROM PR2.IMAGE";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                id = rs.getInt("max_id");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        closeConnection();
        return id + 1;
    } 
    
    
    public JsonObject getImatgeAmbId(String identificador) throws SQLException {
        openConnection();
    
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        String sql = "SELECT TITLE,DESCRIPTION,KEYWORDS,AUTHOR,CREATOR,CAPTURE_DATE,STORAGE_DATE,FILENAME FROM PR2.IMAGE WHERE ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, identificador);

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            jsonBuilder
                .add("id", identificador)
                .add("title", rs.getString("TITLE"))
                .add("description", rs.getString("DESCRIPTION"))
                .add("keywords", rs.getString("KEYWORDS"))
                .add("author", rs.getString("AUTHOR"))
                .add("creator", rs.getString("CREATOR"))
                .add("captureDate", rs.getString("CAPTURE_DATE"))
                .add("storageDate", rs.getString("STORAGE_DATE"))
                .add("filename", rs.getString("FILENAME"));
        }
            
        closeConnection();
        return jsonBuilder.build();
    }
    
    public String eliminaImatge (String identificador) throws SQLException {
        openConnection();
        String sql = "SELECT FILENAME FROM PR2.IMAGE WHERE ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, identificador);
        ResultSet rs = preparedStatement.executeQuery();
        String filename;
        
        if (rs.next()) {
                filename = rs.getString("FILENAME");
        } else {
            // No se encontraron filas con el ID proporcionado
            filename = "";
        }
        
        sql = "DELETE FROM PR2.IMAGE WHERE ID = ?";
        
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, identificador); //Identificador
        preparedStatement.executeUpdate(); // Utilitzem executeUpdate() per DELETE
        
        closeConnection();
        return filename;
    }
    
    /*public String getFilenameById (String identificador) throws SQLException {
        openConnection();
        String sql = "SELECT FILENAME FROM PR2.IMAGE WHERE ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, identificador);
        ResultSet rs = preparedStatement.executeQuery();
        
        String filename;
        try {
            filename = rs.getString("FILENAME");
        } catch (SQLException e) {
            filename = "";
        }
        
        closeConnection();
        return filename;
    }*/
    public String getFilenameById(String identificador) throws SQLException {
        openConnection();
        String sql = "SELECT FILENAME FROM PR2.IMAGE WHERE ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, identificador);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("FILENAME");
                } else {
                    // No se encontraron filas con el ID proporcionado
                    return "";
                }
            }
        } finally {
            closeConnection();
        }
    }

    
    
    public JsonArray getImatgesByTitle(String titol){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();    
        String stringSQL = "SELECT * FROM PR2.image WHERE TITLE LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +titol+ "%");
            ResultSet rs = statement.executeQuery();      
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public JsonArray getImatgesByAuthor(String autor){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        String stringSQL = "SELECT * FROM PR2.image WHERE AUTHOR LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +autor+ "%");
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public JsonArray getImatgesByKeyword(String keyword){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        String stringSQL = "SELECT * FROM PR2.image WHERE KEYWORDS LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +keyword+ "%");
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public JsonArray getImatgesByCreationDate(String creationDate){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        String stringSQL = "SELECT * FROM PR2.image WHERE CAPTURE_DATE LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            //statement.setString(1, "%" +creationDate+ "%");
            statement.setString(1, creationDate);
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    //Retorna la informació de les fotos que quadren amb alfun dels tags de "references"
    public JsonArray getImatgesByCoincidence(String reference){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
        String sql = "SELECT ID,TITLE,DESCRIPTION,KEYWORDS,AUTHOR,CREATOR,CAPTURE_DATE,STORAGE_DATE,FILENAME FROM PR2.image WHERE TITLE LIKE ? OR DESCRIPTION LIKE ? OR AUTHOR LIKE ? OR CAPTURE_DATE LIKE ? OR KEYWORDS LIKE ? ORDER BY STORAGE_DATE DESC";
       
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String referencia = '%' + reference + '%';
            preparedStatement.setString(1, referencia); // TITLE
            preparedStatement.setString(2, referencia); // DESCRIPTION
            preparedStatement.setString(3, referencia); // AUTHOR
            preparedStatement.setString(4, referencia); // CAPTURE_DATE
            preparedStatement.setString(5, referencia); // KEYWORDS
            ResultSet rs = preparedStatement.executeQuery();
            
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
              
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    
    public JsonArray getAllImatges(){
        openConnection();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        String stringSQL = "SELECT * FROM PR2.image ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                    .add("title", rs.getString("TITLE"))
                    .add("description", rs.getString("DESCRIPTION"))
                    .add("keywords", rs.getString("KEYWORDS"))
                    .add("author", rs.getString("AUTHOR"))
                    .add("creator", rs.getString("CREATOR"))
                    .add("captureDate", rs.getString("CAPTURE_DATE"))
                    .add("storageDate", rs.getString("STORAGE_DATE"))
                    .add("filename", rs.getString("FILENAME"))
                    .add("id", rs.getString("ID"));
                jsonArrayBuilder.add(jsonBuilder.build());
            }                
            closeConnection();  
            return jsonArrayBuilder.build();
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    
    public void registrarImatge(String title, String description, String keywords, String author, String creator, String capt_date, String filename) throws SQLException {
        openConnection();
        String sql = "INSERT INTO PR2.IMAGE (TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDate data = LocalDate.now();
        DateTimeFormatter formatData = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String storage_date = data.format(formatData);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, keywords);
        preparedStatement.setString(4, author);
        preparedStatement.setString(5, creator);
        preparedStatement.setString(6, capt_date);
        preparedStatement.setString(7, storage_date);
        preparedStatement.setString(8, filename);

        preparedStatement.executeUpdate();
        
        closeConnection();
    }
    
    public void modificaImatge(String id, String title, String description, String keywords, String creator,
                              String capt_date, String filename) throws SQLException {
        openConnection();
        
        String sql = "UPDATE PR2.IMAGE SET TITLE = ?, DESCRIPTION = ?, KEYWORDS = ?, CREATOR = ?, CAPTURE_DATE = ?, FILENAME = ? WHERE ID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, keywords);
        preparedStatement.setString(4, creator);
        preparedStatement.setString(5, capt_date);
        preparedStatement.setString(6, filename);
        preparedStatement.setString(7, id);

        preparedStatement.executeUpdate();
        
        closeConnection();
    }
}
