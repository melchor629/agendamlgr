package flickr;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Melchor Alejo Garau Madrigal
 */
public class PhotoSetPhotos {
    public final String id;
    public Photo primary;
    public final String owner;
    public final String ownername;
    public final List<Photo> photos;

    @SuppressWarnings("unchecked")
    PhotoSetPhotos(Map<String, Object> res) {
        this.id = (String) res.get("id");
        String primaryid = (String) res.get("primary");
        this.owner = (String) res.get("owner");
        this.ownername = (String) res.get("ownername");
        List<Map<String, Object>> photos = (List<Map<String, Object>>) res.get("photo");
        this.photos = photos.stream().map(Photo::new).collect(Collectors.toList());
        this.photos.stream()
                .filter(photo -> photo.id.equals(primaryid))
                .findFirst()
                .ifPresent(photo -> primary = photo);
    }
}
