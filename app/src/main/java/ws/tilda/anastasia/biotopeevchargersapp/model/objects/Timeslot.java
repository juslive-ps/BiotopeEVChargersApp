
package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timeslot implements Parcelable {

    @SerializedName("gte")
    @Expose
    private String startTime;
    @SerializedName("lte")
    @Expose
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Timeslot() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
    }

    protected Timeslot(Parcel in) {
        this.startTime = in.readString();
        this.endTime = in.readString();
    }

    public static final Creator<Timeslot> CREATOR = new Creator<Timeslot>() {
        @Override
        public Timeslot createFromParcel(Parcel source) {
            return new Timeslot(source);
        }

        @Override
        public Timeslot[] newArray(int size) {
            return new Timeslot[size];
        }
    };
}
