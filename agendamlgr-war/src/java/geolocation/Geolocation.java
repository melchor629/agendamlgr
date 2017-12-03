/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geolocation;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
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
        HttpUriRequest request = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address="+direccion+"&key=AIzaSyBTlwdVS7zlIVZlOPhv29H___ldovUk_qE");
        HttpResponse res = httpClient.execute(request);
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
