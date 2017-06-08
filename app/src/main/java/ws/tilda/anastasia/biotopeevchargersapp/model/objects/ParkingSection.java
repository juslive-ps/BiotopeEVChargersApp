package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParkingSection implements Parcelable {
    private String id;
    private double maxLength;
    private double maxWidth;
    private int numberOfSpots;
    private int numberOfSpotsAvailable;
    private double hourlyPrice;
    private List<ParkingSpot> mParkingSpots;
    private Vehicle vehicle;

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public double getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setNumberOfSpots(int numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }

    public int getNumberOfSpotsAvailable() {
        return numberOfSpotsAvailable;
    }

    public void setNumberOfSpotsAvailable(int numberOfSpotsAvailable) {
        this.numberOfSpotsAvailable = numberOfSpotsAvailable;
    }

    public double getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(double hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public List<ParkingSpot> getParkingSpots() {
        return mParkingSpots;
    }

    public void setParkingSpots(List<ParkingSpot> parkingSpots) {
        this.mParkingSpots = parkingSpots;
    }

    public void addSpot(ParkingSpot parkingSpot) {
        mParkingSpots.add(parkingSpot);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParkingSection() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.maxLength);
        dest.writeDouble(this.maxWidth);
        dest.writeInt(this.numberOfSpots);
        dest.writeInt(this.numberOfSpotsAvailable);
        dest.writeDouble(this.hourlyPrice);
        dest.writeTypedList(this.mParkingSpots);
        dest.writeParcelable(this.vehicle, flags);
    }

    protected ParkingSection(Parcel in) {
        this.id = in.readString();
        this.maxLength = in.readDouble();
        this.maxWidth = in.readDouble();
        this.numberOfSpots = in.readInt();
        this.numberOfSpotsAvailable = in.readInt();
        this.hourlyPrice = in.readDouble();
        this.mParkingSpots = in.createTypedArrayList(ParkingSpot.CREATOR);
        this.vehicle = in.readParcelable(Vehicle.class.getClassLoader());
    }

    public static final Creator<ParkingSection> CREATOR = new Creator<ParkingSection>() {
        @Override
        public ParkingSection createFromParcel(Parcel source) {
            return new ParkingSection(source);
        }

        @Override
        public ParkingSection[] newArray(int size) {
            return new ParkingSection[size];
        }
    };
}
