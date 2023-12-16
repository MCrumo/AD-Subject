/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Aux;

/**
 *
 * @author nacho
 */

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ImatgeAdapter extends TypeAdapter<Imatge> {
    @Override
    public void write(JsonWriter out, Imatge value) throws IOException {
        // Implementa la lógica de escritura si es necesario
    }

    @Override
    public Imatge read(JsonReader in) throws IOException {
        Imatge imatge = new Imatge("", "", "", "", "", "", null, "");
        System.out.println("imatgeAdapter ok");
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "ID":
                    // Convierte ID a String y asigna a id
                    imatge.setId(in.nextString());
                    break;
                case "title":
                    imatge.setTitle(in.nextString());
                    break;
                case "description":
                    imatge.setDescription(in.nextString());
                    break;
                case "keywords":
                    imatge.setKeywords(in.nextString());
                    break;
                case "author":
                    imatge.setAuthor(in.nextString());
                    break;
                case "creator":
                    imatge.setCreator(in.nextString());
                    break;
                //case "captureDate":
                    // Asegúrate de manejar la conversión de formato de fecha si es necesario
                //    imatge.setCaptureDate(in.nextString());
                 //   break;
                case "filename":
                    imatge.setFilename(in.nextString());
                    break;
                default:
                    // Ignora otros campos que no coincidan
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        return imatge;
    }
}