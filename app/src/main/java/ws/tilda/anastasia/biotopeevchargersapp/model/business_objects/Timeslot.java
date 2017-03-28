
package ws.tilda.anastasia.biotopeevchargersapp.model.business_objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timeslot {

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

}
