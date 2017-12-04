/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geolocation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author john
 */
public class Geolocation {
    public static String encontrarCoordenadas(String direccion, String token) throws IOException{
        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(direccion, StandardCharsets.UTF_8.name())+"&key="+token);
        System.out.println("URL peticion: "+"https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(direccion, StandardCharsets.UTF_8.name())+"&key="+token);
        HttpResponse res = httpClient.execute(request);
        // Imprimir respuesta
       /* BufferedReader br = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        String readLine;
String responseBody = "";
while (((readLine = br.readLine()) != null)) {
  responseBody += "\n" + readLine;
}
br.close();
System.out.println(responseBody); */
        //
        Map<String, Object> resultadoJSON = new Gson().fromJson(
            new InputStreamReader(res.getEntity().getContent()),
            Map.class
        );
        
        List<Map<String, Object>> results = (List<Map<String, Object>>) resultadoJSON.get("results");
        Map<String,Object> geometry = (Map<String,Object>) results.get(0).get("geometry");
        Map<String,Object> location = (Map<String,Object>) geometry.get("location");
        Double lat = (Double) location.get("lat");
        Double lng = (Double) location.get("lng");
        //texto.results[0].geometry.location.lat
        return lat+","+lng;
    }
}
