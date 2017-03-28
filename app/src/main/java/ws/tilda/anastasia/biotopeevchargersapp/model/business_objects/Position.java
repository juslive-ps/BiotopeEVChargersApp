
package ws.tilda.anastasia.biotopeevchargersapp.model.business_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Position {

    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lon")
    @Expose
    private float lon;

    public float getLatitude() {
        return lat;
    }

    public void setLatitude(float lat) {
        this.lat = lat;
    }

    public float getLongitude() {
        return lon;
    }

    public void setLongitude(float lon) {
        this.lon = lon;
    }

}
