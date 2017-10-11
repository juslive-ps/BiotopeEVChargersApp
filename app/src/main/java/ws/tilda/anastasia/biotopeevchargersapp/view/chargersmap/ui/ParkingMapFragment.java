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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import ws.tilda.anastasia.biotopeevchargersapp.R;
//import ws.tilda.anastasia.biotopeevchargersapp.model.XmlParser;
import ws.tilda.anastasia.biotopeevchargersapp.model.XmlParser;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.GeoCoordinates;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingLot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingService;
import ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui.ParkingDetailActivity;


public class ParkingMapFragment extends SupportMapFragment {
    public static final String TAG = "ChargerMapFragment";
    public static final int REQUEST_LOCATION_PERMISSIONS = 0;

    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    public static final String PARKINGLOT_EXTRA = "PARKINGLOT_EXTRA";
    public static final String PARKINGLOT_LAT_EXTRA = "PARKINGLOT_LAT_EXTRA";
    public static final String PARKINGLOT_LON_EXTRA = "PARKINGLOT_LON_EXTRA";

    private GoogleApiClient mClient;
    private GoogleMap mMap;

    private XmlParser xmlParser;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        xmlParser = new XmlParser();

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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_charger_map, menu);
//
//        MenuItem searchItem = menu.findItem(R.id.action_locate);
//        searchItem.setEnabled(mClient.isConnected());
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_locate:
//                if (hasLocationPermission()) {
//                    findEvParkingLotByCurrentLocation();
//                } else {
//                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_LOCATION_PERMISSIONS:
//                if (hasLocationPermission()) {
//                    findEvParkingLotByCurrentLocation();
//                }
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    private void getMapAsync() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        ParkingLot parkingLot = (ParkingLot) marker.getTag();

                        Intent intent = setIntent(parkingLot);

                        startActivity(intent);
                    }

                    @NonNull
                    private Intent setIntent(ParkingLot parkingLot) {
                        Intent intent = new Intent(getActivity(), ParkingDetailActivity.class);
                        intent.putExtra(PARKINGLOT_EXTRA, parkingLot);
                        intent.putExtra(PARKINGLOT_LAT_EXTRA, parkingLot.getPosition().getLatitude());
                        intent.putExtra(PARKINGLOT_LON_EXTRA, parkingLot.getPosition().getLongitude());
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

//                  Only if we want to find nearest parking according current location
//                        findEvParkingLotByCurrentLocation();
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


//    private void findEvParkingLotByCurrentLocation() {
//        LocationRequest request = LocationRequest.create();
//        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        request.setNumUpdates(1);
//        request.setInterval(0);
//
//        requestLocationUpdate(request);
//    }

//    private void requestLocationUpdate(LocationRequest request) {
//        // Checking permissions, necessary to request location update
//        if (ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        LocationServices.FusedLocationApi
//                .requestLocationUpdates(mClient, request, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        Log.i(TAG, "Got a fix: " + location);
//                        new SearchParkingTask().execute(location);
//                    }
//                });
//    }

    public void findEvParkingLotBySearchAutocomplete(Location location) {
        new SearchParkingTask().execute(location);
    }

//    private void findEvParkingLot(Location location) {
//        new SearchParkingTask().execute(location);
//    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void updateUI(ParkingService ps) {
        if (mMap == null) {
            return;
        }
        mMap.clear();
        enableMyLocation();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        addAllMarkersToMap(ps, boundsBuilder);

        setZoom(boundsBuilder);
    }

    private void setZoom(LatLngBounds.Builder boundsBuilder) {
        LatLngBounds bounds = boundsBuilder.build();
        int margin = 10;
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

    private void addAllMarkersToMap(ParkingService ps, LatLngBounds.Builder boundsBuilder) {
        List<ParkingLot> parkingLots = ps.getParkingLots();
        for (ParkingLot parkingLot : parkingLots) {
            GeoCoordinates position = parkingLot.getPosition();
            int numberOfSpotsAvailable = parkingLot.getParkingSectionList().get(0).getNumberOfSpotsAvailable();

            LatLng itemPoint = new LatLng(position.getLatitude(), position.getLongitude());


            Marker itemMarker = addMarkerToMap(itemPoint, getMarkerColor(numberOfSpotsAvailable));

            String snippetTitle = getResources().getString(R.string.map_parking_snippet_title,
                    parkingLot.getId());
            itemMarker.setTitle(snippetTitle);

            String snippetText = getResources().getString(R.string.click_to_see_details);
            itemMarker.setSnippet(snippetText);

            itemMarker.setTag(parkingLot);

            boundsBuilder.include(itemPoint);
        }
    }

    private float getMarkerColor(int numberOfSpotsAvailable){
        if(numberOfSpotsAvailable == 0) {
            return BitmapDescriptorFactory.HUE_RED;
        }
        return BitmapDescriptorFactory.HUE_GREEN;
    }

    private Marker addMarkerToMap(LatLng itemPoint, float colorMask) {
        MarkerOptions itemMarker = new MarkerOptions().position(itemPoint);
        itemMarker.icon(BitmapDescriptorFactory.defaultMarker(colorMask));

        return mMap.addMarker(itemMarker);
    }

    private ParkingService parse(InputStream stream) {
        ParkingService parkingService = new ParkingService();
        try {
            parkingService = xmlParser.parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parkingService;
    }

    private class SearchParkingTask extends AsyncTask<Location, Object, ParkingService> {
        private ParkingService parkingService = new ParkingService();
        private String response;

        @Override
        protected ParkingService doInBackground(Location... params) {
            Location location = params[0];

            Call<String> call = callingApi(location);
            InputStream stream = null;
            try {
                stream = new ByteArrayInputStream(getResponse(call).getBytes("UTF-8"));
                parkingService = parse(stream);
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

        private Call<String> callingApi(Location location) {
            ApiClient.RetrofitService retrofitService = ApiClient.getApi();
//            return retrofitService.getResponse(getString(R.string.query_find_evspot));
            return retrofitService.getResponse(getQueryFormattedString(location));

        }


        private String getQueryFormattedString(Location location) {
            float desiredLatitude = (float) location.getLatitude();
            float desiredLongitude = (float) location.getLongitude();

            return String.format(Locale.US,
                    getString(R.string.query_find_evspot),
                    desiredLatitude,
                    desiredLongitude);
        }

        @Override
        protected void onPostExecute(ParkingService parkingService) {
            updateUI(parkingService);
        }
    }

}
