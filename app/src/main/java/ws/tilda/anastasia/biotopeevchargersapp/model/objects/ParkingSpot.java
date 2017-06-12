package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingSpot implements Parcelable {
    private String id;
    private boolean isAvailable;
    private String user;
    private Charger charger;
    private boolean isBookedByOurUser;

    public ParkingSpot() {

    }

    public boolean isBookedByOurUser() {
        return isBookedByOurUser;
    }

    public void setBookedByOurUser(boolean bookedByOurUser) {
        isBookedByOurUser = bookedByOurUser;
    }


    public Charger getCharger() {
        return charger;
    }

    public void setCharger(Charger charger) {
        this.charger = charger;
    }

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
        dest.writeParcelable(this.charger, flags);
    }

    protected ParkingSpot(Parcel in) {
        this.id = in.readString();
        this.isAvailable = in.readByte() != 0;
        this.user = in.readString();
        this.charger = in.readParcelable(Charger.class.getClassLoader());
    }

    public static final Creator<ParkingSpot> CREATOR = new Creator<ParkingSpot>() {
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
