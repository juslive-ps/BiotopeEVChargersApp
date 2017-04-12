package ws.tilda.anastasia.biotopeevchargersapp.view.chargersmap.ui;

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
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.GetChargersResponse;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Position;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.Query;
import ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui.ChargerDetailActivity;


public class ChargerMapFragment extends SupportMapFragment {
    public static final String TAG = "ChargerMapFragment";
    public static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public static final String CHARGER_EXTRA = "CHARGER_EXTRA";
    public static final String CHARGER_LAT_EXTRA = "CHARGER_LAT_EXTRA";
    public static final String CHARGER_LON_EXTRA = "CHARGER_LON_EXTRA";
    public static final float RADIUS = 1200f;

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

        mClient = getGoogleApiClient();

        getMapAsync();
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
        unregisterForContextMenu(getView());

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

    private void getMapAsync() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Charger charger = (Charger) marker.getTag();

                        Intent intent = setIntent(charger);

                        startActivity(intent);
                        Log.d(TAG, "onInfoWindowClick: " + charger.getChargerId());
                    }

                    @NonNull
                    private Intent setIntent(Charger charger) {
                        Intent intent = new Intent(getActivity(), ChargerDetailActivity.class);
                        intent.putExtra(CHARGER_EXTRA, charger.getChargerId());
                        intent.putExtra(CHARGER_LAT_EXTRA, charger.getPosition().getLatitude());
                        intent.putExtra(CHARGER_LON_EXTRA, charger.getPosition().getLongitude());
                        return intent;
                    }
                });
            }
        });
    }

    @NonNull
    private GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(getActivity())
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
    }


    private void findCharger() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        requestLocationUpdate(request);
    }

    private void requestLocationUpdate(LocationRequest request) {
        // Checking permissions, necessary to request location update
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        enableMyLocation();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        addAllMarkersToMap(chargers, boundsBuilder);

        setZoom(boundsBuilder);
    }

    private void setZoom(LatLngBounds.Builder boundsBuilder) {
        LatLngBounds bounds = boundsBuilder.build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        mMap.moveCamera(update);
    }

    private void enableMyLocation() {
        // Checking permissions, necessary to setMyLocationEnabled
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
    }

    private void addAllMarkersToMap(List<Charger> chargers, LatLngBounds.Builder boundsBuilder) {
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
            Call<GetChargersResponse> call = callingApi();
            return getChargers(call);
        }

        private List<Charger> getChargers(Call<GetChargersResponse> call) {
            try {
                GetChargersResponse getChargersResponse = call.execute().body();
                if (getChargersResponse == null) {
                    Log.e(TAG, "Response is null");
                } else {
                    chargers = getChargersResponse.getData().getChargers();
                }
            } catch (IOException e) {
                e.getMessage();
            }

            return chargers;
        }

        private Call<GetChargersResponse> callingApi() {
            mQuery = getQueryBody(mQuery);
            ApiClient.ChargerService chargerService = ApiClient.getApi();
            return chargerService.getChargers(mQuery);
        }

        private Query getQueryBody(Query query) {
//            String queryText = getQueryFormattedString();
            String queryText = getString(R.string.query_chargerId_speed_position_dummy);
            query.setQuery(queryText);
            return query;
        }

        private String getQueryFormattedString() {
            float currentLatitude = (float) mCurrentLocation.getLatitude();
            float currentLongitude = (float) mCurrentLocation.getLongitude();

            return String.format(Locale.US,
                    getString(R.string.query_chargerId_speed_position),
                    currentLatitude,
                    currentLongitude,
                    RADIUS);
        }

        @Override
        protected void onPostExecute(List<Charger> chargers) {
            updateUI(chargers);
        }
    }
}
