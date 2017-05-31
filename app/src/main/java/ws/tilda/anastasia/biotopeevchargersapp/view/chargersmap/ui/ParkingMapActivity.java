package ws.tilda.anastasia.biotopeevchargersapp.view.chargersmap.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ParkingMapActivity extends SingleFragmentActivity {
    public static final int REQUEST_ERROR = 0;


    @Override
    protected Fragment createFragment() {
        return ParkingMapFragment.newInstance();
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
}
