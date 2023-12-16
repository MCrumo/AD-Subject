/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

/**
 *
 * @author alumne
 */
public class ConnectionUtil {
    private static String address = "localhost";
    private static String port = "8080";
  
    public static String getServerAddr() {
        String connection = address + ":" + port;
        return connection;
    }
    
}
