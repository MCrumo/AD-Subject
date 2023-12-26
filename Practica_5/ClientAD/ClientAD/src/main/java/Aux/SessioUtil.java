package Aux;

import jakarta.servlet.http.HttpSession;
import java.io.OutputStream;

import java.net.URL;
import java.net.HttpURLConnection;

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
                if(responseCode != HttpURLConnection.HTTP_OK) return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -2;
            }
        } else { //Si no hi ha una sessió Http en peu, envia a la pàgina d'error pertinent
           return -2;
       }
        
        return 0;
    }
}
