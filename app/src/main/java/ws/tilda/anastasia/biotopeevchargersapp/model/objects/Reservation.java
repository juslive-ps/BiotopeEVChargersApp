
package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation implements Parcelable {

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

    public Reservation() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reservationId);
        dest.writeString(this.userId);
        dest.writeParcelable(this.timeslot, flags);
    }

    protected Reservation(Parcel in) {
        this.reservationId = in.readString();
        this.userId = in.readString();
        this.timeslot = in.readParcelable(Timeslot.class.getClassLoader());
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel source) {
            return new Reservation(source);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };
}
