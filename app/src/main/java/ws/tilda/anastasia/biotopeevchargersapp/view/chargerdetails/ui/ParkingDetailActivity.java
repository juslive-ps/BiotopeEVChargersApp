package ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.model.XmlParser2;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.GeoCoordinates;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingLot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSection;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingService;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSpot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Plug;

public class ParkingDetailActivity extends AppCompatActivity {
    public static final String TAG = "ParkingDetailActivity";
    public static final String PARKINGLOT_EXTRA = "PARKINGLOT_EXTRA";
    private static final String EXTRA = "evParkingSpotsList";
    private static final String EXTRA_PARKING_LOT = "parkingLot";

    private ParkingLot mParkingLot;
    private GoogleMap mMap;

    private Context mContext;

    static ArrayList<ParkingSpot> evParkingSpots = new ArrayList<>();

    @BindView(R.id.mapView)
    MapView mMapView;
    @BindView(R.id.parkingLot_id)
    TextView mParkingLotId;
    @BindView(R.id.parkingLot_owner)
    TextView mParkingLotOwner;
    @BindView(R.id.parkingLot_opening_hours)
    TextView mOpeningHours;
    @BindView(R.id.parkingLot_address)
    TextView mAddress;
    @BindView(R.id.parkingLot_hourly_price)
    TextView mHourlyPrice;
    @BindView(R.id.parking_spot_dimensions)
    TextView mEvSpotDimensions;
    @BindView(R.id.max_spots)
    TextView mMaxSpots;
    @BindView(R.id.spots_available)
    TextView mSpotsAvailable;

    private XmlParser2 parser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_detail);
        ButterKnife.bind(this);

        parser = new XmlParser2();


        mContext = this;

        mParkingLot = getIntent().getParcelableExtra(PARKINGLOT_EXTRA);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        GeoCoordinates parkingLotPosition = mParkingLot.getPosition();

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                fillParkingLotInfo(mParkingLot);
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

    private void fillParkingLotInfo(ParkingLot parkingLot) {
        mParkingLotId.setText(parkingLot.getId());
        mParkingLotOwner.setText(parkingLot.getOwner());
        mOpeningHours.setText(parkingLot.getOpeningHours().getOpen()
                + " - " + parkingLot.getOpeningHours().getClose());
        mAddress.setText(parkingLot.getPosition().getAddress().toString());
        fillEvSectionInfo(parkingLot);

        enableMyLocation();
        MapsInitializer.initialize(mContext);
        addMarkerToMapView(parkingLot);
        setZoom(parkingLot);
    }

    private void fillEvSectionInfo(ParkingLot parkingLot) {
        List<ParkingSection> parkingSections = parkingLot.getParkingSectionList();
        ParkingSection evParkingSection = extractEvParkingSection(parkingSections);
        mHourlyPrice.setText(Double.toString(evParkingSection.getHourlyPrice()) + " euro");
        mEvSpotDimensions.setText(evParkingSection.getVehicle().getDepth() + " x "
                + evParkingSection.getVehicle().getWidth() + " x "
                + evParkingSection.getVehicle().getHeight());
        mMaxSpots.setText(String.valueOf(evParkingSection.getNumberOfSpots()));
        mSpotsAvailable.setText(String.valueOf(evParkingSection.getNumberOfSpotsAvailable()));

        evParkingSpots = (ArrayList<ParkingSpot>) evParkingSection.getParkingSpots();
}

    private ParkingSection extractEvParkingSection(List<ParkingSection> parkingSections) {
        ParkingSection evParkingSection = null;
        for (ParkingSection parkingSection : parkingSections) {
            if (parkingSection.getId().equals("ElectricVehicleParkingSpace")) {
                evParkingSection = parkingSection;
            } else {
                evParkingSection = null;
            }
        }
        return evParkingSection;
    }

    private void setZoom(ParkingLot parkingLot) {
        LatLng latLng = getParkingLotLatLng(parkingLot);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.moveCamera(cameraUpdate);
    }

    @NonNull
    private void addMarkerToMapView(ParkingLot parkingLot) {
        LatLng latLng = getParkingLotLatLng(parkingLot);

        MarkerOptions itemMarker = new MarkerOptions().position(latLng);
        itemMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(itemMarker);
    }

    @NonNull
    private LatLng getParkingLotLatLng(ParkingLot parkingLot) {
        GeoCoordinates position = parkingLot.getPosition();
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

    private ParkingService parseParkingService(InputStream stream) {
        ParkingService parkingService = new ParkingService();
        try {
            parkingService = parser.parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parkingService;
    }

    public void openEvSpotListActivity(View view) {
        new SearchParkingLotsListTask().execute(mParkingLot.getId());

        Intent intent = new Intent(ParkingDetailActivity.this, EvSpotListActivity.class);
        intent.putParcelableArrayListExtra(EXTRA, evParkingSpots);
        intent.putExtra(EXTRA_PARKING_LOT, mParkingLot);
        startActivity(intent);
    }

    private class SearchParkingLotsListTask extends AsyncTask<String, Object, ParkingService> {
        private ParkingService parkingService = new ParkingService();
        private String response;

        @Override
        protected ParkingService doInBackground(String ... params) {
            String parkingLotId = params[0];

            Call<String> call = callingApi(parkingLotId);
            InputStream stream = null;
            try {
                stream = new ByteArrayInputStream(getResponse(call).getBytes("UTF-8"));
                parkingService = parseParkingService(stream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return parkingService;
        }

        private String getResponse(Call<String> call) {
            try {
                String getResponse = call.execute().body();
                if (getResponse == null) {
                    Log.e(TAG, "Response is null");
                } else {
                    response = getResponse;
                }
            } catch (IOException e) {
                e.getMessage();
            }

            return response;
        }

        private Call<String> callingApi(String parkingLotId) {
            ApiClient.RetrofitService retrofitService = ApiClient.getApi();
            return retrofitService.getResponse(getQueryFormattedString(parkingLotId));

        }


        private String getQueryFormattedString(String parkingLotId) {

            return String.format(Locale.US,
                    getString(R.string.query_find_evspotsList),
                    parkingLotId);
        }

        @Override
        protected void onPostExecute(ParkingService parkingService) {
            List<ParkingLot> parkingLots = parkingService.getParkingLots();
            for (ParkingLot parkingLot : parkingLots) {
                if (parkingLot.getId().equals(mParkingLot.getId())) {
                    evParkingSpots =
                            (ArrayList) parkingLot.getParkingSectionList().get(0).getParkingSpots();

                }
            }

        }
    }

}
