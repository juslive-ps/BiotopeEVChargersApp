package ws.tilda.anastasia.biotopeevchargersapp.main.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.charger_details.ui.ChargerDetailActivity;
import ws.tilda.anastasia.biotopeevchargersapp.model.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.GetChargersResponse;
import ws.tilda.anastasia.biotopeevchargersapp.model.Position;
import ws.tilda.anastasia.biotopeevchargersapp.model.fetch.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.fetch.Query;


public class ChargerMapFragment extends SupportMapFragment {
    public static final String TAG = "ChargerMapFragment";
    public static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final String CHARGER_EXTRA = "CHARGER_EXTRA";
    public static final String CHARGER_LAT_EXTRA = "CHARGER_LAT_EXTRA";
    public static final String CHARGER_LON_EXTRA = "CHARGER_LON_EXTRA";

    private static final float RADIUS = 1200f;

    private Location mCurrentLocation;
    private GoogleApiClient mClient;
    private GoogleMap mMap;


    public static ChargerMapFragment newInstance() {
        return new ChargerMapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();

                        findCharger();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getTag() == null) {
                            Toast.makeText(getActivity(), "This is my location ", Toast.LENGTH_SHORT).show();
                        } else {
                            Charger charger = (Charger) marker.getTag();

                            Intent intent = new Intent(getActivity(), ChargerDetailActivity.class);
                            intent.putExtra(CHARGER_EXTRA, charger.getChargerId());
                            intent.putExtra(CHARGER_LAT_EXTRA, charger.getPosition().getLatitude());
                            intent.putExtra(CHARGER_LON_EXTRA, charger.getPosition().getLongitude());
                            startActivity(intent);

///                            Log.d(TAG, "onInfoWindowClick: " + charger.getChargerId());
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_charger_map, menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (hasLocationPermission()) {
                    findCharger();
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findCharger();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void findCharger() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mClient, request, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a fix: " + location);
                        mCurrentLocation = location;
                        new SearchTask().execute(location);
                    }
                });
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void updateUI(List<Charger> chargers) {
        if (mMap == null) {
            return;
        }
        mMap.clear();
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());

        boundsBuilder.include(myPoint);

        addMarkersToMap(chargers, boundsBuilder);

        LatLngBounds bounds = boundsBuilder.build();

        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);

        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.moveCamera(update);
    }

    private void addMarkersToMap(List<Charger> chargers, LatLngBounds.Builder boundsBuilder) {
        for (Charger charger : chargers) {
            Position position = charger.getPosition();

            LatLng itemPoint = new LatLng(position.getLatitude(), position.getLongitude());

            Marker itemMarker = addMarkerToMap(itemPoint, BitmapDescriptorFactory.HUE_GREEN);

            String snippetTitle = getResources().getString(R.string.map_charger_snippet_title,
                    charger.getChargerId());
            itemMarker.setTitle(snippetTitle);

            String snippetText = getResources().getString(R.string.map_charger_snippet_description,
                    charger.getChargingSpeed());
            itemMarker.setSnippet(snippetText);

            itemMarker.setTag(charger);

            boundsBuilder.include(itemPoint);
        }
    }

//    private Marker addMarkerToMap(LatLng itemPoint) {
//        return addMarkerToMap(itemPoint, BitmapDescriptorFactory.HUE_RED);
//    }

    private Marker addMarkerToMap(LatLng itemPoint, float colorMask) {
        MarkerOptions itemMarker = new MarkerOptions().position(itemPoint);
        itemMarker.icon(BitmapDescriptorFactory.defaultMarker(colorMask));

        return mMap.addMarker(itemMarker);
    }

    private class SearchTask extends AsyncTask<Object, Object, List<Charger>> {
        private Query mQuery = new Query();
        private List<Charger> chargers = new ArrayList<>();

        @Override
        protected List<Charger> doInBackground(Object... params) {
            float currentLatitude = (float) mCurrentLocation.getLatitude();
            float currentLongitude = (float) mCurrentLocation.getLongitude();

            String queryText = String.format(Locale.US, getString(R.string.query_chargerId_speed_position),
                    currentLatitude,
                    currentLongitude,
                    RADIUS);

            ApiClient.ChargerService chargerService = ApiClient.getApi();
            mQuery.setQuery(queryText);
            Call<GetChargersResponse> call = chargerService.getChargers(mQuery);

            if (call == null) {
                Log.e(TAG, "Call is null");
            } else {
                try {
                    GetChargersResponse getChargersResponse = call.execute().body();
                    if (getChargersResponse == null) {
                        Log.e(TAG, "Response is null");
                    } else {
                        chargers = getChargersResponse.getData().getChargers();
                    }
                } catch (IOException e) {
                    e.getMessage();
                    Toast.makeText(getActivity(), "Exception received: " + e, Toast.LENGTH_SHORT)
                                   .show();
                }
            }

            return chargers;
        }

        @Override
        protected void onPostExecute(List<Charger> chargers) {
            updateUI(chargers);
        }
    }
}
