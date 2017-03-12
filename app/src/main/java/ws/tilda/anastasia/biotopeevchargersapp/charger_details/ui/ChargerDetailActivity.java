package ws.tilda.anastasia.biotopeevchargersapp.charger_details.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ws.tilda.anastasia.biotopeevchargersapp.R;

public class ChargerDetailActivity extends AppCompatActivity {
    private static final String CHARGER_EXTRA = "CHARGER_EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger_detail);

        int index = getIntent().getIntExtra(CHARGER_EXTRA, -1);
    }
}
