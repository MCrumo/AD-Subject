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
    
    
}
