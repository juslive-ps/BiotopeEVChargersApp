package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class GeoCoordinates implements Parcelable {
    private double latitude;
    private double longitude;
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GeoCoordinates() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeParcelable(this.address, flags);
    }

    protected GeoCoordinates(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Creator<GeoCoordinates> CREATOR = new Creator<GeoCoordinates>() {
        @Override
        public GeoCoordinates createFromParcel(Parcel source) {
            return new GeoCoordinates(source);
        }

        @Override
        public GeoCoordinates[] newArray(int size) {
            return new GeoCoordinates[size];
        }
    };
}
