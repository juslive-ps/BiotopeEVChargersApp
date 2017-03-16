package ws.tilda.anastasia.biotopeevchargersapp.model.old_model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Charger {
    @SerializedName("charger_id")
     String mChargerId;

    @SerializedName("charging_speed")
     String mChargingSpeed;

    @SerializedName("position")
     Position mPosition;

    @SerializedName("reservations")
     List<Reservation> mReservations;

    public Charger() {
        mReservations = new ArrayList<>();
    }


    public String getChargerId() {
        return mChargerId;
    }

    public void setChargerId(String chargerId) {
        mChargerId = chargerId;
    }

    public String getChargingSpeed() {
        return mChargingSpeed;
    }

    public void setChargingSpeed(String chargingSpeed) {
        mChargingSpeed = chargingSpeed;
    }

    public Position getPosition() {
        return mPosition;
    }

    public void setPosition(Position position) {
        mPosition = position;
    }

    public List<Reservation> getReservations() {
        return mReservations;
    }

    public void setReservations(List<Reservation> reservations) {
        mReservations = reservations;
    }
}


