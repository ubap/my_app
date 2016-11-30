package pg.eti.inz.engineer.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.data.TripAdapter;

public class TripsActivity extends AppCompatActivity {
    Context context;
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);

        context = this;
        ListView tripList = (ListView) findViewById(R.id.tripsTripList);
        if (dbManager == null) {
            dbManager = new DbManager(this);
        }

        tripList.setAdapter(new TripAdapter(this, dbManager.getTripsCursor()));
        tripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor mycursor = (Cursor) parent.getItemAtPosition(position);
                Trip trip = new Trip(mycursor);

                Intent mapIntent = new Intent(context, ViewTripActivity.class);
                mapIntent.putExtra("trip", trip);
                startActivity(mapIntent);
            }
        });
    }
}
