/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import static javax.management.Query.and;
import objects.Image;

/**
 *
 * @author alumne
 */
public class DataBase {
    public DataBase() {}
    
    public Connection open_connection () {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/pr2;user=pr2;password=pr2");
            return connection;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public void close_connection (Connection connection) {
        try {
            connection.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public boolean login_usr (Connection connection, String user, String pw) {
        try {
            PreparedStatement statement;
            String query = "SELECT * FROM pr2.USUARIOS WHERE ID_USUARIO = ? " +
                    "AND PASSWORD = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, user);
            statement.setString(2, pw);
            ResultSet rs = statement.executeQuery();
            return rs.next();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    /*
    public boolean register_usr (Connection connection, String user, String pw) throws SQLException {
        try {
            PreparedStatement statement;
            String query = "INSERT INTO pr2.USUARIOS " + "VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, user);
            statement.setString(2, pw);
            statement.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }*/
    
    public boolean register_img (String title, String description, 
            String keywords, String author, String creator, String crDate, 
            String actDate, String fileName, Connection connection) {
        try {
            PreparedStatement statement;
            String query = "INSERT INTO IMAGE (title, description, keywords,"
                    + "author, creator, capture_date, storage_date, filename) "
                    + "VALUES (?,?,?,?,?,?,?,?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setString(3, keywords);
            statement.setString(4, author);
            statement.setString(5, creator);
            statement.setString(6, crDate);
            statement.setString(7, actDate);
            statement.setString(8, fileName);
            int rs = statement.executeUpdate();
            return true;
            //ResultSet rs = statement.executeUpdate();
            //return rs.next();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    public Integer get_max_ID (Connection connection) {
        try {
            PreparedStatement statement;
            String query = "SELECT MAX(ID) AS MAX_ID FROM pr2.IMAGE";
            statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            int maxID = 0;
            if (rs.next()) {
                return maxID = rs.getInt("MAX_ID");
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            System.out.println("[DB] get_max_ID ok");
        }
        return 0;
    }
    
    public String get_file_name (int id, Connection connection) {
        try {
            PreparedStatement statement;
            String query = "SELECT FILENAME FROM PR2.IMAGE WHERE ID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1,id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) return rs.getString("FILENAME");
            return null;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public List list_images(Connection connection) {
        List<Image> llista = new ArrayList<>();
        
        try {
            PreparedStatement statement;
            String query = "SELECT *FROM PR2.IMAGE";
            
            statement = connection.prepareStatement(query);
            
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                int ID = rs.getInt("ID");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String keywords = rs.getString("KEYWORDS");
                String author = rs.getString("AUTHOR");
                String creator = rs.getString("CREATOR");
                String filename = rs.getString("FILENAME");
                llista.add(new Image(ID, title, description, keywords, author, creator, filename));
            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("[OK] DB list_images");
        }
       
        return llista;
    }
    
    public List search_images(String title, String description, String keywords, 
            String author, String creator, String crDate, Connection connection) {
        List<Image> llista = new ArrayList<>();
        
        try {
            PreparedStatement statement;
            String query = "SELECT * FROM PR2.IMAGE WHERE 1=1 "
                    + "AND TITLE LIKE ? "
                    + "AND DESCRIPTION LIKE ? "
                    + "AND KEYWORDS LIKE ? "
                    + "AND AUTHOR LIKE ? "
                    + "AND CREATOR LIKE ? "
                    + "AND CAPTURE_DATE LIKE ?";
            
            statement = connection.prepareStatement(query);
            
            if (title != null && !title.isEmpty()) {
                statement.setString(1, "%" + title + "%");
            }
            else statement.setString(1, "%");
            if (description != null && !description.isEmpty()) {
                statement.setString(2, "%" + description + "%");
            }
            else statement.setString(2, "%");
            if (keywords != null && !keywords.isEmpty()) {
                statement.setString(3, "%" + keywords + "%");
            }
            else statement.setString(3, "%");
            if (author != null && !author.isEmpty()) {
                statement.setString(4, "%" + author + "%");
            }
            else statement.setString(4, "%");
            if (creator != null && !creator.isEmpty()) {
                statement.setString(5, "%" + creator + "%");
            }
            else statement.setString(5, "%");
            if (crDate != null && !crDate.isEmpty()) {
                statement.setString(6, "%" + crDate + "%");
            }
            else statement.setString(6, "%");
            
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                int ID = rs.getInt("ID");
                title = rs.getString("TITLE");
                description = rs.getString("DESCRIPTION");
                keywords = rs.getString("KEYWORDS");
                author = rs.getString("AUTHOR");
                creator = rs.getString("CREATOR");
                String filename = rs.getString("FILENAME");
                llista.add(new Image(ID, title, description, keywords, author, creator, filename));
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally {
            System.out.print("[OK] DB search_images");
        }
        
        return llista;
    }
    
    public Image search_ID(int id, Connection connection) {
            Image img = new Image(null, null, null, null, null, null, null);
        
        try {
            PreparedStatement statement;
            String query = "SELECT * FROM PR2.IMAGE WHERE ID = ? ";
            
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, id);
            
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
                img.ID = rs.getInt("ID");
                img.title = rs.getString("TITLE");
                img.description = rs.getString("DESCRIPTION");
                img.keywords = rs.getString("KEYWORDS");
                img.author = rs.getString("AUTHOR");
                img.creator = rs.getString("CREATOR");
                img.filename = rs.getString("FILENAME");
            }            
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("[OK] DB search_ID");
        }
        return img;
    }
    
    public boolean delete_img (String id, Connection connection) {
        try {
            int imageID = Integer.parseInt(id);
            PreparedStatement statement;
            String query = "DELETE FROM IMAGE WHERE ID = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, imageID);
            int rs = statement.executeUpdate();
            return true;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
    
    public boolean update_img (int id, String title, String description, 
            String keywords, String author, String crDate, String fileName, 
            Connection connection) {
        try {
            PreparedStatement statement;
            String query;
            int r;
            if (title != null && !title.isEmpty()) {
                query = "UPDATE IMAGE SET TITLE = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,title);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            }
            if (description != null && !description.isEmpty()) {
                query = "UPDATE IMAGE SET DESCRIPTION = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,description);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            }
            if (keywords != null && !keywords.isEmpty()) {
                query = "UPDATE IMAGE SET KEYWORDS = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,keywords);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            }
            if (author != null && !author.isEmpty()) {
                query = "UPDATE IMAGE SET AUTHOR = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,author);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            }
            if (crDate != null && !crDate.isEmpty()) {
                query = "UPDATE IMAGE SET CAPTURE_DATE = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,crDate);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            } 
            if (fileName != null && !fileName.isEmpty()) {
                query = "UPDATE IMAGE SET FILENAME = ? WHERE ID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1,fileName);
                statement.setInt(2,id);
                r = statement.executeUpdate();
            }
            return true;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}
