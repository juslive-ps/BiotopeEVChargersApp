package ws.tilda.anastasia.biotopeevchargersapp.view.chargersmap.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import ws.tilda.anastasia.biotopeevchargersapp.BiotopeEvChargersApp;
import ws.tilda.anastasia.biotopeevchargersapp.R;
import ws.tilda.anastasia.biotopeevchargersapp.view.authentication.EmailPasswordActivity;

public class ParkingMapActivity extends AppCompatActivity {
    public static final int REQUEST_ERROR = 0;
    private static final String TAG = "ParkingMapActivity";

    //Firebase Authentication variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Navigation Drawer variables
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private FirebaseUser mUser;
    private TextView mUserEmail;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_parking_map;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        ActionBar supportActionBar = setingActionBar();

        setTransparentActionBar(supportActionBar);

        checkAuthentication();

        initializingNavigDrawer();

        settingGooglePlacesSearchBar();
    }

    private void setTransparentActionBar(ActionBar supportActionBar) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @NonNull
    private ActionBar setingActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setTitle("");
        return supportActionBar;
    }

    private void checkAuthentication() {
        //Checking if the user is authenticated
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(getBaseContext(), EmailPasswordActivity.class);
                    startActivity(intent);

                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void settingGooglePlacesSearchBar() {
        // Adding Google Places search bar to the activity
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


    public void onFragmentInteraction(double latitude, double longitude) {
        ParkingMapFragment parkingMapFragment = (ParkingMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
            parkingMapFragment.findEvParkingLot(location);
    }

    private void initializingNavigDrawer() {
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

//                    case R.id.account:
//                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.payments:
//                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.starred:
//                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.settings:
//                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
//                        return true;
                    case R.id.logout:
//                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        Intent intent = new Intent(getBaseContext(), EmailPasswordActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), R.string.update_needed, Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerShadow(new ColorDrawable(Color.TRANSPARENT), GravityCompat.START);
        setupDrawer();
        mUserEmail = (TextView) findViewById(R.id.email);
        //mUserEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
