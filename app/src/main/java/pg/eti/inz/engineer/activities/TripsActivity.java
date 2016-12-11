package pg.eti.inz.engineer.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.Trip;

public class TripsActivity extends AppCompatActivity {
    Context context;
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.trips_toolbar);
        setSupportActionBar(myToolbar);

        context = this;
        ListView tripList = (ListView) findViewById(R.id.tripsTripList);
        if (dbManager == null) {
            dbManager = new DbManager(this);
        }

        tripList.setAdapter(new TripAdapter(this, dbManager.getTripsCursor()));
        tripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor myCursor = (Cursor) parent.getItemAtPosition(position);
                Trip trip = new Trip(myCursor);

                Intent mapIntent = new Intent(context, ViewTripActivity.class);
                mapIntent.putExtra("trip", trip);
                startActivity(mapIntent);
            }
        });
    }
}
