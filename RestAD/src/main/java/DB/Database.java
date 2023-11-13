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
    
    /*
    public Imatge getImatgeAmbId(String identificador) {
        openConnection();
        
        Imatge imatge = null; 
        
        String sql = "SELECT TITLE,DESCRIPTION,KEYWORDS,AUTHOR,CREATOR,CAPTURE_DATE,STORAGE_DATE,FILENAME FROM PR2.IMAGE WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identificador); //Identificador
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String captureDate = rs.getString("CAPTURE_DATE");
                String storageDate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");

                // Crea l'objecte imatge
                imatge = new Imatge(identificador, title, description, keywords, author, creator, captureDate, storageDate, filename, null);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            closeConnection();
        }
        
        closeConnection();
        return imatge;
    }
    
    public boolean eliminaImatge (String identificador) {
        openConnection();
        
        String sql = "DELETE FROM PR2.IMAGE WHERE ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, identificador); //Identificador
            
            int rowsAffected = preparedStatement.executeUpdate(); // Utilitzem executeUpdate() per DELETE
            if (rowsAffected == 0) {
                //No s'ha eliminat cap fila perque no existia el id
                return false;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            closeConnection();
            return false;
        }
        
        closeConnection();
        return true;
    }
    
    public ArrayList<Imatge> getAllImatges(){
        openConnection();
        ArrayList<Imatge> setImatges = new ArrayList<Imatge>();
        String stringSQL = "SELECT * FROM PR2.image ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                String id = rs.getString("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String cdate = rs.getString("CAPTURE_DATE");
                String sdate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");
                Imatge img = new Imatge(id, title, description, keywords, author, creator, cdate, sdate, filename, null);           
                setImatges.add(img);
            }                
            closeConnection();  
            return setImatges;
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public ArrayList<Imatge> getImatgesByTitle(String titol){
        openConnection();
        ArrayList<Imatge> setImatges = new ArrayList<Imatge>();
        String stringSQL = "SELECT * FROM PR2.image WHERE TITLE LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +titol+ "%");
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                String id = rs.getString("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String cdate = rs.getString("CAPTURE_DATE");
                String sdate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");
                Imatge img = new Imatge(id, title, description, keywords, author, creator, cdate, sdate, filename, null);           
                setImatges.add(img);
            }                
            closeConnection();  
            return setImatges;
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public ArrayList<Imatge> getImatgesByAuthor(String autor){
        openConnection();
        ArrayList<Imatge> setImatges = new ArrayList<Imatge>();
        String stringSQL = "SELECT * FROM PR2.image WHERE AUTHOR LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +autor+ "%");
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                String id = rs.getString("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String cdate = rs.getString("CAPTURE_DATE");
                String sdate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");
                Imatge img = new Imatge(id, title, description, keywords, author, creator, cdate, sdate, filename, null);           
                setImatges.add(img);
            }                
            closeConnection();  
            return setImatges;
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    public ArrayList<Imatge> getImatgesByKeyword(String keyword){
        openConnection();
        ArrayList<Imatge> setImatges = new ArrayList<Imatge>();
        String stringSQL = "SELECT * FROM PR2.image WHERE KEYWORDS LIKE ? ORDER BY STORAGE_DATE DESC";
        try {
            PreparedStatement statement = connection.prepareStatement(stringSQL);
            statement.setString(1, "%" +keyword+ "%");
            ResultSet rs = statement.executeQuery();      
                           
            // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
            while(rs.next()) {
                String id = rs.getString("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String cdate = rs.getString("CAPTURE_DATE");
                String sdate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");
                Imatge img = new Imatge(id, title, description, keywords, author, creator, cdate, sdate, filename, null);           
                setImatges.add(img);
            }                
            closeConnection();  
            return setImatges;
                
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            closeConnection();
        }
        return null;
    }
    
    //Retorna la informació de les fotos que quadren amb alfun dels tags de "references"
    public ArrayList<Imatge> getSetImatges(String reference){
        openConnection();
        // indexs: ID, TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME
        String sql = "SELECT ID,TITLE,DESCRIPTION,KEYWORDS,AUTHOR,CREATOR,CAPTURE_DATE,STORAGE_DATE,FILENAME FROM PR2.image WHERE TITLE LIKE ? OR DESCRIPTION LIKE ? OR AUTHOR LIKE ? OR CAPTURE_DATE LIKE ? OR KEYWORDS LIKE ? ORDER BY STORAGE_DATE DESC";
        
        ArrayList<Imatge> setImatges = new ArrayList<Imatge>();
       
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
                String id = rs.getString("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String cdate = rs.getString("CAPTURE_DATE");
                String sdate = rs.getString("STORAGE_DATE");
                String filename = rs.getString("FILENAME");
                Imatge img = new Imatge(id, title, description, keywords, author, creator, cdate, sdate, filename, null);           
                setImatges.add(img);
            }  
        }
        
        catch (Exception e){
            System.err.println(e.getMessage());
            //response.sendRedirect("menu.jsp");
        }
        
        closeConnection();
        return setImatges;
    }
    */
    
    
    public void registrarImatge(String title, String description, String keywords, String author, String creator, String capt_date, String filename) {
        openConnection();
                
        try {
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

        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e.getMessage());
        }
        
        closeConnection();
    }
    /*
    public boolean modificaImatge(Imatge imatge) {
        openConnection();
        
        try {
            String sql = "UPDATE PR2.IMAGE SET TITLE = ?, DESCRIPTION = ?, KEYWORDS = ?, CREATOR = ?, CAPTURE_DATE = ?, FILENAME = ? WHERE ID = ?";
            
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, imatge.getTitle());
            preparedStatement.setString(2, imatge.getDescription());
            preparedStatement.setString(3, imatge.getKeywords());
            preparedStatement.setString(4, imatge.getCreator());
            preparedStatement.setString(5, imatge.getCaptureDate());
            preparedStatement.setString(6, imatge.getFilename());
            preparedStatement.setString(7, imatge.getId());
            
            preparedStatement.executeUpdate();
            System.out.println("es fa el update");
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e.getMessage());
            return false;
        }
        
        closeConnection();
        return true;
    }*/
}
