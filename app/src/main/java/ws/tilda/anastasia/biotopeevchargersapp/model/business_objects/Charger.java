
package ws.tilda.anastasia.biotopeevchargersapp.model.business_objects;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Charger {

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

}
