package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Plug implements Parcelable {
    private String id;
    private String chargingSpeed;
    private String voltage;
    private String power;
    private String plugType;
    private boolean isCableAvailable;
    private boolean isLockerAvailable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChargingSpeed() {
        return chargingSpeed;
    }

    public void setChargingSpeed(String chargingSpeed) {
        this.chargingSpeed = chargingSpeed;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPlugType() {
        return plugType;
    }

    public void setPlugType(String plugType) {
        this.plugType = plugType;
    }

    public boolean isCableAvailable() {
        return isCableAvailable;
    }

    public void setCableAvailable(boolean cableAvailable) {
        isCableAvailable = cableAvailable;
    }

    public boolean isLockerAvailable() {
        return isLockerAvailable;
    }

    public void setLockerAvailable(boolean lockerAvailable) {
        isLockerAvailable = lockerAvailable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.chargingSpeed);
        dest.writeString(this.voltage);
        dest.writeString(this.power);
        dest.writeString(this.plugType);
        dest.writeByte(this.isCableAvailable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLockerAvailable ? (byte) 1 : (byte) 0);
    }

    public Plug() {
    }

    protected Plug(Parcel in) {
        this.id = in.readString();
        this.chargingSpeed = in.readString();
        this.voltage = in.readString();
        this.power = in.readString();
        this.plugType = in.readString();
        this.isCableAvailable = in.readByte() != 0;
        this.isLockerAvailable = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Plug> CREATOR = new Parcelable.Creator<Plug>() {
        @Override
        public Plug createFromParcel(Parcel source) {
            return new Plug(source);
        }

        @Override
        public Plug[] newArray(int size) {
            return new Plug[size];
        }
    };
}
