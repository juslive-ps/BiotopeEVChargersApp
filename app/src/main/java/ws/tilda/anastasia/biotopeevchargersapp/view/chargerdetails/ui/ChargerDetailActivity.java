package ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.GetChargersResponse;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Position;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.Query;

import static ws.tilda.anastasia.biotopeevchargersapp.R.id.mapView;

public class ChargerDetailActivity extends AppCompatActivity {
    public static final String TAG = "ChargerDetailActivity";
    private static final String CHARGER_EXTRA = "CHARGER_EXTRA";
    public static final String CHARGER_LAT_EXTRA = "CHARGER_LAT_EXTRA";
    public static final String CHARGER_LON_EXTRA = "CHARGER_LON_EXTRA";
    private static final float RADIUS = 0f;
    private String chargerId;
    private GoogleMap mMap;

    private float mChargerLatitude;
    private float mChargerLongitude;

    private MapView mMapView;
    private TextView mChargerId;
    private TextView mChargerLocation;
    private TextView mChargerSpeed;
    private TextView mChargerStatus;
    private TextView mChargerReservePrice;

    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charger_detail);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mContext = this;

        Intent intent = getIntent();
        chargerId = intent.getStringExtra(CHARGER_EXTRA);
        mChargerLatitude = intent.getFloatExtra(CHARGER_LAT_EXTRA, -1);
        mChargerLongitude = intent.getFloatExtra(CHARGER_LON_EXTRA, -1);


        mMapView = (MapView) findViewById(mapView);
        mChargerId = (TextView) findViewById(R.id.charger_id);
        mChargerLocation = (TextView) findViewById(R.id.charger_location);
        mChargerSpeed = (TextView) findViewById(R.id.charger_speed);
        mChargerStatus = (TextView) findViewById(R.id.charger_status);
        mChargerReservePrice = (TextView) findViewById(R.id.charger_price);

        mMapView.onCreate(savedInstanceState);


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                new GetChargerTask().execute();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    private void fillChargerInfo(Charger charger) {
        mChargerId.setText(charger.getChargerId());
        mChargerSpeed.setText(charger.getChargingSpeed());
        mChargerReservePrice.setText("0.0 eur/min");

        setChargerStatus(charger);
        setChargerAddress(charger);

        enableMyLocation();

        MapsInitializer.initialize(mContext);

        addMarkerToMapView(charger);

        setZoom(charger);
    }

    private void setZoom(Charger charger) {
        LatLng latLng = getChargerLatLng(charger);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.moveCamera(cameraUpdate);
    }

    @NonNull
    private void addMarkerToMapView(Charger charger) {
        LatLng latLng = getChargerLatLng(charger);

        MarkerOptions itemMarker = new MarkerOptions().position(latLng);
        itemMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(itemMarker);
    }

    @NonNull
    private LatLng getChargerLatLng(Charger charger) {
        Position position = charger.getPosition();
        return new LatLng(position.getLatitude(), position.getLongitude());
    }

    private void enableMyLocation() {
        //Necessary permissions check
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    private void setChargerAddress(Charger charger) {
        double chargerLatitude = (double) charger.getPosition().getLatitude();
        double chargerLongitude = (double) charger.getPosition().getLongitude();
        String address = getAddress(chargerLatitude, chargerLongitude);
        mChargerLocation.setText(address);
    }

    private void setChargerStatus(Charger charger) {
        boolean isAvailable = charger.getAvailable();
        if (!isAvailable) {
            mChargerStatus.setText(R.string.status_not_available);
        } else {
            mChargerStatus.setText(R.string.status_available);
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.US);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);
            if (addresses != null && addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        return adr.getLocality() + ", "
                                + adr.getCountryName();
                    }
                }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return null;
    }


    private class GetChargerTask extends AsyncTask<Object, Object, Charger> {
        private Query mQuery = new Query();
        private List<Charger> chargers = new ArrayList<>();
        private Charger mPickedCharger;

        @Override
        protected Charger doInBackground(Object... params) {

            Call<GetChargersResponse> call = callingApi();

            return getCharger(call);
        }

        private Charger getCharger(Call<GetChargersResponse> call) {
            try {
                GetChargersResponse getChargersResponse = call.execute().body();
                if (getChargersResponse == null) {
                    Log.e(TAG, "Response is null");
                } else {
                    chargers = getChargersResponse.getData().getChargers();
                    for (Charger charger : chargers) {
                        if (charger.getChargerId().equals(chargerId)) {
                            mPickedCharger = charger;
                        }
                    }
                }
            } catch (IOException e) {
                e.getMessage();
            }

            return mPickedCharger;
        }

        private Call<GetChargersResponse> callingApi() {
            Query queryBody = getQueryBody(mQuery);
            ApiClient.ChargerService chargerService = ApiClient.getApi();
            return chargerService.getChargers(queryBody);
        }

        private Query getQueryBody(Query query) {
//            String queryText = getQueryString();
            String queryText = getString(R.string.query_chargerId_speed_position_available_reservations_dummy);
            query.setQuery(queryText);
            return query;
        }

        private String getQueryString() {
            return String.format(Locale.US,
                            getString(R.string.query_chargerId_speed_position_available_reservations),
                            mChargerLatitude,
                            mChargerLongitude,
                            RADIUS);
        }

        @Override
        protected void onPostExecute(Charger charger) {
            if (charger == null) {
                Toast.makeText(mContext, R.string.no_content, Toast.LENGTH_SHORT).show();
                return;
            }

            fillChargerInfo(charger);
        }
    }
}
