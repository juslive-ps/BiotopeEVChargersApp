package ws.tilda.anastasia.biotopeevchargersapp.model;

import com.google.gson.annotations.SerializedName;

public class Timeslot {
    @SerializedName("gte")
     String startTime;

    @SerializedName("lte")
     String endTime;

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
}
