
package ws.tilda.anastasia.biotopeevchargersapp.model.business_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation {

    @SerializedName("reservation_id")
    @Expose
    private String reservationId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("timeslot")
    @Expose
    private Timeslot timeslot;

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

}
