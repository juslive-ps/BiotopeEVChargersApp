package ws.tilda.anastasia.biotopeevchargersapp.view.chargersmap.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import ws.tilda.anastasia.biotopeevchargersapp.R;

public class ParkingMapActivity extends AppCompatActivity {
    public static final int REQUEST_ERROR = 0;
    private static final String TAG = "ParkingMapActivity";


    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_parking_map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());


        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_support_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLang = place.getLatLng();
                double latitude = latLang.latitude;
                double longitude = latLang.longitude;
                onFragmentInteraction(latitude, longitude);
                Log.i(TAG, "LatLong " + latitude + " " + longitude);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            });

            errorDialog.show();
        }
    }

    public void onFragmentInteraction(double latitude, double longitude ) {
        ParkingMapFragment parkingMapFragment = (ParkingMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        parkingMapFragment.findEvParkingLotBySearchAutocomplete(location);
    }

//    private interface OnFragmentInteractionListener {
//        public void onFragmentInteraction(double latitude, double longitude);
//    }


}
