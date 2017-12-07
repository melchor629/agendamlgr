package flickr;

import java.util.Map;

/**
 * @author Melchor Alejo Garau Madrigal
 */
public class Photo {
    public final String id;
    public final String title;
    public final String thumbnailUrl;
    public final String mediumSizeUrl;
    public final String kindLargeSizeUrl;

    Photo(Map<String, Object> res) {
        this.id = (String) res.getOrDefault("primary", res.get("id"));
        String secret = (String) res.get("secret");
        String server = (String) res.get("server");
        int farm = (int) (double) (Double) res.get("farm");
        this.title = res.get("title") instanceof String ? (String) res.get("title") : null;
        this.thumbnailUrl = String.format(
                "https://farm%s.staticflickr.com/%s/%s_%s_q.jpg",
                farm, server, id, secret
        );
        this.mediumSizeUrl = String.format(
                "https://farm%s.staticflickr.com/%s/%s_%s.jpg",
                farm, server, id, secret
        );
        this.kindLargeSizeUrl = String.format(
                "https://farm%s.staticflickr.com/%s/%s_%s_b.jpg",
                farm, server, id, secret
        );
    }
}
