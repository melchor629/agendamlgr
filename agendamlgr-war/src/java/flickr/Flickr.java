package flickr;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import service.TokensUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapted from https://github.com/melchor629/melchor629.github.com/blob/master/assets/js/flickrApi.coffee
 * and https://github.com/melchor629/melchor629.github.com/blob/master/assets/js/photoGallery.coffee
 * @author Melchor Alejo Garau Madrigal
 */
public class Flickr {

    private static final String apiUrl = "https://api.flickr.com/services/rest/";
    private static final Map<String, CachedResult> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> doRequest(String method, Map<String, String> params) throws IOException {
        params = new HashMap<>(params);
        params.put("api_key", TokensUtils.flickrApiKey);
        params.put("format", "json");
        params.put("method", method);
        String url = buildUrl(params);

        cache.entrySet().stream().filter(entry -> !entry.getValue().isValid()).forEach(entry -> cache.remove(entry.getKey()));
        CachedResult result = cache.get(url);
        if(result != null) return (Map<String, T>) result.result;

        HttpClient httpClient = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet(url);
        HttpResponse res = httpClient.execute(request);

        //We get a JSONP not a json, we must discard two parts from it
        StringBuilder sb = new StringBuilder(" ");
        InputStreamReader inputStreamReader = new InputStreamReader(res.getEntity().getContent());
        while(sb.charAt(sb.length() - 1) != (char) 65535) {
            sb.append((char) inputStreamReader.read());
        }
        inputStreamReader.close();

        String json = sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1).substring(15);
        Map<String, T> jsonParsed = new Gson().fromJson(json, Map.class);
        cache.put(url, new CachedResult((Map<String, Object>) jsonParsed));
        return jsonParsed;
    }

    private static String buildUrl(final Map<String, String> params) {
        StringBuilder sb = new StringBuilder(apiUrl);
        sb.append('?');
        params.forEach((key, value) -> sb.append(key).append('=').append(value).append('&'));
        if(sb.lastIndexOf("&") == sb.length() - 1) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static Map<String, String> fromPairedArgs(String... args) {
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < args.length; i += 2) {
            map.put(args[i], args[i+1]);
        }
        return map;
    }

    public static class Photosets {

        /**
         * @param userId User ID of the owner of the photoset
         * @param photosetId Photoset ID
         * @return List of photos from Photoset
         * @throws IOException IO problems
         * @see <a href="https://www.flickr.com/services/api/flickr.photosets.getPhotos.html>flickr.photosets.getPhotos</a>
         */
        @SuppressWarnings("unchecked")
        public static PhotoSetPhotos getPhotos(String userId, String photosetId) throws IOException {
            Map<String, Object> res = doRequest(
                "flickr.photosets.getPhotos",
                fromPairedArgs("user_id", userId, "photoset_id", photosetId)
            );
            Map<String, Object> photoset = (Map<String, Object>) res.get("photoset");
            return photoset != null ? new PhotoSetPhotos(photoset) : null;
        }

        /**
         * @param userId User ID of the owner of the photoset
         * @param photosetId Photoset ID
         * @return Information about the Photoset
         * @throws IOException IO problems
         * @see <a href="https://www.flickr.com/services/api/flickr.photosets.getInfo.html>flickr.photosets.getInfo</a>
         */
        @SuppressWarnings("unchecked")
        public static PhotoSetInfo getInfo(String userId, String photosetId) throws IOException {
            Map<String, Object> res = doRequest(
                    "flickr.photosets.getInfo",
                    fromPairedArgs("user_id", userId, "photoset_id", photosetId)
            );
            Map<String, Object> photoset = (Map<String, Object>) res.get("photoset");
            return photoset != null ? new PhotoSetInfo(photoset) : null;
        }

    }

    private static class CachedResult {
        private Map<String, Object> result;
        private long validUntil;

        private CachedResult(Map<String, Object> result) {
            this.result = result;
            this.validUntil = System.currentTimeMillis() + 10 * 60 * 1000; //10 min
        }

        private boolean isValid() {
            return System.currentTimeMillis() < validUntil;
        }
    }

}
