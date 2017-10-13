package ws.tilda.anastasia.biotopeevchargersapp.view.chargerdetails.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.model.XmlParser;
import ws.tilda.anastasia.biotopeevchargersapp.model.networking.ApiClient;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingLot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSpot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Registration;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.User;


public class EvSpotListActivity extends AppCompatActivity {

    private static final String EXTRA = "evParkingSpotsList";
    private static final String EXTRA_PARKING_LOT = "parkingLot";
    private static final String TAG = "EvSpotListActivity";

    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<ParkingSpot> evParkingSpotsList;
    private ParkingLot parkingLot;
    private Registration registration;
    private User user;
    private List<User> users;
    private XmlParser parser;
    private EvParkingSpotsAdapter evParkingSpotsAdapter;
    private String parkingLotId;





    static double parkingLotLat;
    static double parkingLotLon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ev_spot_list);

        evParkingSpotsList = getIntent().getParcelableArrayListExtra(EXTRA);
        parkingLot = getIntent().getParcelableExtra(EXTRA_PARKING_LOT);
        parkingLotId = parkingLot.getId();
        parkingLotLat = parkingLot.getPosition().getLatitude();
        parkingLotLon = parkingLot.getPosition().getLongitude();

        parser = new XmlParser();



        user = new User();
        user.setUsername("TK");
        user.setPassword("CurrentPassword");
        registration = new Registration();
        registration.addUser(user);
        users = registration.getUsers();



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        recyclerView = (RecyclerView) findViewById(R.id.activity_ev_spot_list_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_ev_spot_list);

        evParkingSpotsAdapter = new EvParkingSpotsAdapter(evParkingSpotsList, this, parkingLot);
        recyclerView.setAdapter(evParkingSpotsAdapter);

    }


    public User getUser() {
        return user;
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

    public void reserveEvParkingSpot(View view, int position) {
        ParkingSpot evParkingspot = evParkingSpotsList.get(position);
        String evParkingLotId = parkingLot.getId();
        String evParkingSpotId = evParkingspot.getId();
        String username = user.getUsername();

        String isAvailable = "false";

        new ReserveParkingTask(evParkingSpotsAdapter, position).execute(evParkingLotId, evParkingSpotId, username, isAvailable);
    }

    public void leaveEvParkingSpot(View view, int position) {
        ParkingSpot evParkingspot = evParkingSpotsList.get(position);
        String evParkingLotId = parkingLot.getId();
        String evParkingSpotId = evParkingspot.getId();
        String username = user.getUsername();
        String isAvailable = "true";

        new ReserveParkingTask(evParkingSpotsAdapter, position).execute(evParkingLotId, evParkingSpotId, username, isAvailable);
    }

    public void openChargerLid(View v, int position) {
        ParkingSpot evParkingspot = evParkingSpotsList.get(position);
        String evParkingLotId = parkingLot.getId();
        String evParkingSpotId = evParkingspot.getId();
        String username = user.getUsername();

        String lidStatus = "Open";

        new UseChargerTask(evParkingSpotsAdapter, position).execute(evParkingLotId, evParkingSpotId, username, lidStatus);
    }

//    public void lockChargerLid(View v, int position) {
//        ParkingSpot evParkingspot = evParkingSpotsList.get(position);
//        String evParkingLotId = parkingLot.getId();
//        String evParkingSpotId = evParkingspot.getId();
//        String username = user.getUsername();
//        String lidStatus = "Locked";
//
//        new UseChargerTask(evParkingSpotsAdapter, position).execute(evParkingLotId, evParkingSpotId, username, lidStatus);
//    }

    private String parse(InputStream stream) {
        String responseCode = "";
        try {
            responseCode = parser.parseResponseCode(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseCode;
    }

    private class ReserveParkingTask extends AsyncTask<String, Object, String> {
        private String response;
        private String responseCode;

        private EvParkingSpotsAdapter evAdapter;
        private int position;

        public ReserveParkingTask(EvParkingSpotsAdapter evAdapter, int position) {
            this.evAdapter = evAdapter;
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {

            String evParkingLotId = params[0];
            String evParkingSpotId = params[1];
            String username = params[2];
            String isAvalable = params[3];

            String bookingQuery = getBookingQueryString(evParkingLotId, evParkingSpotId, username, isAvalable);

            Call<String> call = callingApi(bookingQuery);
            InputStream stream = null;
            try {
                stream = new ByteArrayInputStream(getResponse(call).getBytes("UTF-8"));
                responseCode = parse(stream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            }
            return responseCode;
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

        private Call<String> callingApi(String queryString) {
            ApiClient.RetrofitService retrofitService = ApiClient.getApi();
            return retrofitService.getResponse(queryString);
        }

        private String getBookingQueryString(String evParkingLotId, String enParkingSpotId, String username, String isAvailable) {

            return String.format(Locale.US,
                    getString(R.string.query_book_evspot),
                    evParkingLotId,
                    enParkingSpotId,
                    username,
                    isAvailable);
        }


        @Override
        protected void onPostExecute(String responseCode) {
            evAdapter.changeParkingBookState(position, responseCode);
        }

    }


    private class UseChargerTask extends AsyncTask<String, Object, String> {
        private String response;
        private String responseCode;

        private EvParkingSpotsAdapter evAdapter;
        private int position;

        public UseChargerTask(EvParkingSpotsAdapter evAdapter, int position) {
            this.evAdapter = evAdapter;
            this.position = position;
        }

        @Override
        protected String doInBackground(String... params) {

            String evParkingLotId = params[0];
            String evParkingSpotId = params[1];
            String username = params[2];
            String lidStatus = params[3];

            String bookingQuery = getBookingQueryString(evParkingLotId, evParkingSpotId, username, lidStatus);

            Call<String> call = callingApi(bookingQuery);
            InputStream stream = null;
            try {
                stream = new ByteArrayInputStream(getResponse(call).getBytes("UTF-8"));
                responseCode = parse(stream);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            }
            return responseCode;
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

        private Call<String> callingApi(String queryString) {
            ApiClient.RetrofitService retrofitService = ApiClient.getApi();
            return retrofitService.getResponse(queryString);
        }

        private String getBookingQueryString(String evParkingLotId, String enParkingSpotId, String username, String lidStatus) {

            return String.format(Locale.US,
                    getString(R.string.query_use_charger),
                    evParkingLotId,
                    enParkingSpotId,
                    username,
                    lidStatus);
        }


        @Override
        protected void onPostExecute(String responseCode) {
            evAdapter.changeUseChargerState(position, responseCode);
        }

    }

}
