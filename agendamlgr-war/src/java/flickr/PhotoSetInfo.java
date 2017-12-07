package flickr;

import java.util.Map;

/**
 * @author Melchor Alejo Garau Madrigal
 */
public class PhotoSetInfo {
    public final String id;
    public final Photo primary;
    public final String owner;
    public final String username;
    public final int photos;
    public final String title;
    public final String description;

    @SuppressWarnings("unchecked")
    PhotoSetInfo(Map<String, Object> res) {
        this.id = (String) res.get("id");
        this.primary = new Photo(res);
        this.owner = (String) res.get("owner");
        this.username = (String) res.get("username");
        this.photos = (int) (double) (Double) res.get("photos");
        this.title = ((Map<String, String>) res.get("title")).get("_content");
        this.description = ((Map<String, String>) res.get("description")).get("_content");
    }
}
