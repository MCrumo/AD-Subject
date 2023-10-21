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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    //Retorna la informació de les fotos que quadren amb alfun dels tags de "references"
    public List<List<String>> getSetImages(String reference){
        openConnection();
        
        String sql = "SELECT filename,titol,descripcio,tags,autor,datac,username,id FROM imatges WHERE titol LIKE ? OR descripcio LIKE ? OR autor LIKE ? OR datac LIKE ? OR tags LIKE ? ORDER BY datac DESC";
        
        ArrayList<String> filename = new ArrayList<String>();
        ArrayList<String> tittle = new ArrayList<String>();
        ArrayList<String> description = new ArrayList<String>();
        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> author = new ArrayList<String>();
        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> user = new ArrayList<String>();
        ArrayList<Integer> id = new ArrayList<Integer>();
        
        List<List<String>> result = new ArrayList<>();
       
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            String referencia = '%' + reference + '%';
            preparedStatement.setString(1, referencia);
            preparedStatement.setString(2, referencia);
            preparedStatement.setString(3, referencia);
            preparedStatement.setString(4, referencia);
            preparedStatement.setString(5, referencia);
            
            ResultSet rs = preparedStatement.executeQuery();
            
            if (!rs.next()) {
                //filename.add("Error400");
                ;
            }
            else{
                filename.add(rs.getString(1));
                tittle.add(rs.getString(2));
                description.add(rs.getString(3));
                tags.add(rs.getString(4));
                author.add(rs.getString(5));
                date.add(rs.getString(6));
                user.add(rs.getString(7));
                id.add(rs.getInt(8));
            }
            while(rs.next()){
                filename.add(rs.getString(1));
                tittle.add(rs.getString(2));
                description.add(rs.getString(3));
                tags.add(rs.getString(4));
                author.add(rs.getString(5));
                date.add(rs.getString(6));
                user.add(rs.getString(7));
                id.add(rs.getInt(8));
            }  
            List<String> idStrings = new ArrayList<>();
            for (Integer value : id) idStrings.add(value.toString());
            
            result.add(filename);
            result.add(tittle);
            result.add(description);
            result.add(tags);
            result.add(author);
            result.add(date);
            result.add(user);
            result.add(idStrings);
        }
        
        catch (Exception e){
            System.err.println(e.getMessage());
            //response.sendRedirect("menu.jsp");
        }
        
        return result;
    }
}
