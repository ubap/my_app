package pg.eti.inz.engineer.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TripContainer {
    List<Trip> trips = new ArrayList<>();

    public void addTrip(Trip trip) {
        trips.add(trip);
        Log.d("myApp", "Added trip!!");
    }
}
