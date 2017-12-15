package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParkingLot implements Parcelable {
    private String id;
    private String maxParkingHours;
    private String owner;
    private GeoCoordinates position;
    private OpeningHours openingHours;
    private List<ParkingSection> mParkingSectionList;
    private List<ParkingSpot> mParkingSpots;
    private int numberOfEvParkingSpots;
    private int numberOfOccupiedParkingSpots;
    private int totalCapacity;

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getNumberOfOccupiedParkingSpots() {
        return numberOfOccupiedParkingSpots;
    }

    public void setNumberOfOccupiedParkingSpots(int numberOfOccupiedParkingSpots) {
        this.numberOfOccupiedParkingSpots = numberOfOccupiedParkingSpots;
    }

    public List<ParkingSpot> getParkingSpots() {
        return mParkingSpots;
    }

    public void setParkingSpots(List<ParkingSpot> parkingSpots) {
        mParkingSpots = parkingSpots;
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        mParkingSpots.add(parkingSpot);
    }

    public int getNumberOfEvParkingSpots() {
        return numberOfEvParkingSpots;
    }

    public void setNumberOfEvParkingSpots(int numberOfEvParkingSpots) {
        this.numberOfEvParkingSpots = numberOfEvParkingSpots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaxParkingHours() {
        return maxParkingHours;
    }

    public void setMaxParkingHours(String maxParkingHours) {
        this.maxParkingHours = maxParkingHours;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GeoCoordinates getPosition() {
        return position;
    }

    public void setPosition(GeoCoordinates position) {
        this.position = position;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<ParkingSection> getParkingSectionList() {
        return mParkingSectionList;
    }

    public void setParkingSectionList(List<ParkingSection> parkingSectionList) {
        mParkingSectionList = parkingSectionList;
    }

    public void addParkingSection(ParkingSection parkingSection) {
        mParkingSectionList.add(parkingSection);
    }

    public ParkingLot() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.maxParkingHours);
        dest.writeString(this.owner);
        dest.writeParcelable(this.position, flags);
        dest.writeParcelable(this.openingHours, flags);
        dest.writeTypedList(this.mParkingSectionList);
        dest.writeTypedList(this.mParkingSpots);
    }

    protected ParkingLot(Parcel in) {
        this.id = in.readString();
        this.maxParkingHours = in.readString();
        this.owner = in.readString();
        this.position = in.readParcelable(GeoCoordinates.class.getClassLoader());
        this.openingHours = in.readParcelable(OpeningHours.class.getClassLoader());
        this.mParkingSectionList = in.createTypedArrayList(ParkingSection.CREATOR);
        this.mParkingSpots = in.createTypedArrayList(ParkingSpot.CREATOR);
    }

    public static final Creator<ParkingLot> CREATOR = new Creator<ParkingLot>() {
        @Override
        public ParkingLot createFromParcel(Parcel source) {
            return new ParkingLot(source);
        }

        @Override
        public ParkingLot[] newArray(int size) {
            return new ParkingLot[size];
        }
    };
}
