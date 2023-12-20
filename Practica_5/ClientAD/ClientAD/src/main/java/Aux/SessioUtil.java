/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import java.io.OutputStream;


import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nacho
 * 
 * Verifica que hi ha una sessió creada i és vàlida
 * 
 */
public class SessioUtil {
    public static int validaSessio(HttpSession sessio) {
        // Verifica si la HttpSession no es nula i si existeix un atribut "username"
        if (sessio != null && sessio.getAttribute("tokenJWT") != null) {            
            String addr = ConnectionUtil.getServerAddr();
            String token = (String) sessio.getAttribute("tokenJWT");

            try {
                URL url = new URL("http://" + addr + "/verify-token");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Enviar el token al backend
                try (OutputStream os = connection.getOutputStream()) {
                    String jsonInputString = "{\"token\": \"" + token + "\"}";
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                System.out.print(responseCode);
                if(responseCode != HttpURLConnection.HTTP_OK) return -1;
            } catch (Exception e) {
                // Manejar excepciones según tus necesidades
                e.printStackTrace();
            }








            // Crida al mètode userExists para verificar si nom d'usuari existeix en la base de dades            
            /*URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL("http://"+ addr +"/RestAD/resources/jakartaee9/userExists");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                // Estem a post, permetem la sortida de dades
                connection.setDoOutput(true);

                String postData = "username=" + username;
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException ex) {
                    Logger.getLogger(SessioUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

                int responseCode = connection.getResponseCode();
                connection.disconnect();
                
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return -1;
                }
                
                //connection.disconnect();
                
            } catch (Exception ex) {
                if (connection != null) connection.disconnect();
                Logger.getLogger(SessioUtil.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }*/
        } else { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
           return -2;
       }
        
        return 0;
    }
}
