
package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Charger implements Parcelable {

    @SerializedName("charger_id")
    @Expose
    private String chargerId;
    @SerializedName("charging_speed")
    @Expose
    private String chargingSpeed;
    @SerializedName("available")
    @Expose
    private Boolean available;
    @SerializedName("position")
    @Expose
    private Position position;
    @SerializedName("reservations")
    @Expose
    private List<Reservation> reservations = null;

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public String getChargingSpeed() {
        return chargingSpeed;
    }

    public void setChargingSpeed(String chargingSpeed) {
        this.chargingSpeed = chargingSpeed;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chargerId);
        dest.writeString(this.chargingSpeed);
        dest.writeValue(this.available);
        dest.writeParcelable(this.position, flags);
        dest.writeTypedList(this.reservations);
    }

    public Charger() {
    }

    protected Charger(Parcel in) {
        this.chargerId = in.readString();
        this.chargingSpeed = in.readString();
        this.available = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.position = in.readParcelable(Position.class.getClassLoader());
        this.reservations = in.createTypedArrayList(Reservation.CREATOR);
    }

    public static final Parcelable.Creator<Charger> CREATOR = new Parcelable.Creator<Charger>() {
        @Override
        public Charger createFromParcel(Parcel source) {
            return new Charger(source);
        }

        @Override
        public Charger[] newArray(int size) {
            return new Charger[size];
        }
    };
}
