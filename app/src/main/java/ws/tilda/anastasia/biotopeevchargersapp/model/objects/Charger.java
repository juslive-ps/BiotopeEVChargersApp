
package ws.tilda.anastasia.biotopeevchargersapp.model.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Charger implements Parcelable {
        private String id;
        private String model;
        private String brand;
        private Plug plug;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public Plug getPlug() {
            return plug;
        }

        public void setPlug(Plug plug) {
            this.plug = plug;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.model);
        dest.writeString(this.brand);
        dest.writeParcelable(this.plug, flags);
    }

    public Charger() {
    }

    protected Charger(Parcel in) {
        this.id = in.readString();
        this.model = in.readString();
        this.brand = in.readString();
        this.plug = in.readParcelable(Plug.class.getClassLoader());
    }

    public static final Parcelable.Creator<Charger> CREATOR = new Parcelable.Creator<Charger>() {
        @Override
        public Charger createFromParcel(Parcel source) {
            return new Charger(source);
        }

        @Override
        public Charger[] newArray(int size) {
            return new Charger[size];
        }
    };
}
