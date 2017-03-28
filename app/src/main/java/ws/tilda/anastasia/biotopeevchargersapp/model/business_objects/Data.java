
package ws.tilda.anastasia.biotopeevchargersapp.model.business_objects;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("searchByArea")
    @Expose
    private List<Charger> chargers = null;

    public List<Charger> getChargers() {
        return chargers;
    }

    public void setChargers(List<Charger> chargers) {
        this.chargers = chargers;
    }

}
