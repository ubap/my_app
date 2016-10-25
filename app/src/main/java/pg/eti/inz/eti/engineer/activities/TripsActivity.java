package pg.eti.inz.eti.engineer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import pg.eti.inz.eti.engineer.R;

public class TripsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        ListView tripList = (ListView) findViewById(R.id.tripsTripList);
//        tripList.setAdapter();
    }
}
