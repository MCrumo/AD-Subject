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
    private String address = "localhost";
    private String port = "8080";
    
    /*
     *  CREATORS
     */
    public ConnectionUtil() {
    }
    
    public ConnectionUtil(String addres, String port){
        this.address = address;
        this.port = port;
    }
    
    
    /*
     *  SETTERS
     */
    public void setPort(String port) {
        this.port = port;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /*
     *  GETTERS
     */
    public String getConnection() {
        String connection = address + ":" + port;
        return connection;
    }
    
    public String getPort() {
        return this.port;
    }
    
    public String getAddress() {
        return this.address;
    }
}
