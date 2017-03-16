package ws.tilda.anastasia.biotopeevchargersapp.charger_details.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Position;
import ws.tilda.anastasia.biotopeevchargersapp.model.fetch.ChargerFetcher;

import static ws.tilda.anastasia.biotopeevchargersapp.R.id.mapView;

public class ChargerDetailActivity extends AppCompatActivity {
    private static final String CHARGER_EXTRA = "CHARGER_EXTRA";
    private String chargerId;
    private GoogleMap mMap;

    private MapView mMapView;
    private TextView mChargerId;
    private TextView mChargerLocation;
    private TextView mChargerSpeed;
    private TextView mChargerReservePrice;
    private Button mReserveButton;

    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charger_detail);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ctx = this;

        Intent intent = getIntent();
        chargerId = intent.getStringExtra(CHARGER_EXTRA);

        mMapView = (MapView) findViewById(mapView);
        mChargerId = (TextView) findViewById(R.id.charger_id);
        mChargerLocation = (TextView) findViewById(R.id.charger_location);
        mChargerSpeed = (TextView) findViewById(R.id.charger_speed);
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


        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);

        MapsInitializer.initialize(ctx);

        Position position = charger.getPosition();
        LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());

        MarkerOptions itemMarker = new MarkerOptions().position(latLng);
        itemMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mMap.addMarker(itemMarker);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.moveCamera(cameraUpdate);
    }

    private class GetChargerTask extends AsyncTask<Void, Void, Charger> {

        @Override
        protected Charger doInBackground(Void... params) {
            ChargerFetcher fetch = new ChargerFetcher(ctx);
            return fetch.downloadCharger(chargerId);
        }

        @Override
        protected void onPostExecute(Charger charger) {
            if(charger == null){
                Toast.makeText(ctx, R.string.no_content, Toast.LENGTH_SHORT).show();
                return;
            }

            fillChargerInfo(charger);
        }
    }
}
