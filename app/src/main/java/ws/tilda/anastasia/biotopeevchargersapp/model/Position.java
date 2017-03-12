package ws.tilda.anastasia.biotopeevchargersapp.model;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("lat")
     double mLatitude;

    @SerializedName("lon")
     double mLongitude;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
