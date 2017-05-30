package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingSpot implements Parcelable {
    private String id;
    private boolean isAvailable;
    private String user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByte(this.isAvailable ? (byte) 1 : (byte) 0);
        dest.writeString(this.user);
    }

    public ParkingSpot() {
    }

    protected ParkingSpot(Parcel in) {
        this.id = in.readString();
        this.isAvailable = in.readByte() != 0;
        this.user = in.readString();
    }

    public static final Parcelable.Creator<ParkingSpot> CREATOR = new Parcelable.Creator<ParkingSpot>() {
        @Override
        public ParkingSpot createFromParcel(Parcel source) {
            return new ParkingSpot(source);
        }

        @Override
        public ParkingSpot[] newArray(int size) {
            return new ParkingSpot[size];
        }
    };
}
