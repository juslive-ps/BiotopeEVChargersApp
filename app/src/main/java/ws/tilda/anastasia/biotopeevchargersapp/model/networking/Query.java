package ws.tilda.anastasia.biotopeevchargersapp.model.networking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Query {
    @SerializedName("query")
    @Expose
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
