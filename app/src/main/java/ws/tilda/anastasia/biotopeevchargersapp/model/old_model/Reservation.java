package ws.tilda.anastasia.biotopeevchargersapp.model.old_model;

import com.google.gson.annotations.SerializedName;

public class Reservation {
    @SerializedName("reservation_id")
     String mReservationId;

    @SerializedName("user_id")
     String mUserId;

    @SerializedName("timeslot")
     Timeslot timeslot;

    public String getReservationId() {
        return mReservationId;
    }

    public void setReservationId(String reservationId) {
        mReservationId = reservationId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }
}
