
package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetChargersResponse implements Parcelable {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
    }

    public GetChargersResponse() {
    }

    protected GetChargersResponse(Parcel in) {
        this.data = in.readParcelable(Data.class.getClassLoader());
    }

    public static final Parcelable.Creator<GetChargersResponse> CREATOR = new Parcelable.Creator<GetChargersResponse>() {
        @Override
        public GetChargersResponse createFromParcel(Parcel source) {
            return new GetChargersResponse(source);
        }

        @Override
        public GetChargersResponse[] newArray(int size) {
            return new GetChargersResponse[size];
        }
    };
}
