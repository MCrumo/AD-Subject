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
    private static String address = "ad-node-container";
    private static String port = "8082";
  
    public static String getServerAddr() {
        String connection = address + ":" + port;
        //String connection = "ad-node-container";
        return connection;
    }

    public static String getServerAddrFrontend() {
        String connection = "localhost:" + port;
        //String connection = "ad-node-container";
        return connection;
    }
    
}
