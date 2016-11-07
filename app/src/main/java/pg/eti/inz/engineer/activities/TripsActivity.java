package pg.eti.inz.engineer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.TripAdapter;

import static android.R.id.list;

public class TripsActivity extends AppCompatActivity {
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        ListView tripList = (ListView) findViewById(R.id.tripsTripList);
        if (dbManager == null) {
            dbManager = new DbManager(this);
        }

        tripList.setAdapter(new TripAdapter(this, dbManager.getTripsCursor()));
    }
}
