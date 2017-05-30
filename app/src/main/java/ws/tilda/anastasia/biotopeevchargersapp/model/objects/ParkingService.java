package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ParkingService implements Parcelable {
    private String id;
    private List<ParkingLot> mParkingLots;

    public ParkingService() {
        mParkingLots = new ArrayList<>();
    }

    public List<ParkingLot> getParkingLots() {
        return mParkingLots;
    }

    public void setParkingLots(List<ParkingLot> parkingLots) {
        mParkingLots = parkingLots;
    }

    public void addParkingLot(ParkingLot parkingLot) {
        mParkingLots.add(parkingLot);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeList(this.mParkingLots);
    }

    protected ParkingService(Parcel in) {
        this.id = in.readString();
        this.mParkingLots = new ArrayList<ParkingLot>();
        in.readList(this.mParkingLots, ParkingLot.class.getClassLoader());
    }

    public static final Parcelable.Creator<ParkingService> CREATOR = new Parcelable.Creator<ParkingService>() {
        @Override
        public ParkingService createFromParcel(Parcel source) {
            return new ParkingService(source);
        }

        @Override
        public ParkingService[] newArray(int size) {
            return new ParkingService[size];
        }
    };
}
