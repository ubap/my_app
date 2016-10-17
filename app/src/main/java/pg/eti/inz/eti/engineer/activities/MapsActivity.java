package pg.eti.inz.eti.engineer.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.util.List;
import java.util.Timer;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.gps.GPSServiceProvider2;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap mMap;
    private Marker markerSelf;
    private TextView speedMeter;
    private TextView tripMeter;
    private CheckBox followPositionCheckBox;

    private Boolean followPosition;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Location location = GPSServiceProvider2.getInstance().getLocation();
            if (location != null) {
                updateMapPosition(location);
                updateSpeedMeter(location.getSpeed());
                updateTripMeter(GPSServiceProvider2.getInstance().getPathLength());
                drawPath(GPSServiceProvider2.getInstance().getPath());
            }
            // keep tracking pos
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("myApp", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("myApp", "An error occurred: " + status);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        switch (item.getItemId()) {
            case R.id.action_search:
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(this);

        // update map thread
        handler.post(runnable);

        // Init controls
//        LinearLayout speedMeterLayout = (LinearLayout) findViewById(R.id.speedMeterLayout);
//        speedMeterLayout.setMinimumWidth(speedMeterLayout.getWidth());

        speedMeter = (TextView) findViewById(R.id.MapSpeedMeter);
        speedMeter.setMinimumWidth(speedMeter.getWidth()); // HACK! cus of some sort of BUG! need to block the width so incase we display more tight digits the speedMeterLayout stays at same size
        updateSpeedMeter(0);    // set speed at 0

        tripMeter = (TextView) findViewById(R.id.MapTripMeter);
        tripMeter.setMinimumWidth((tripMeter.getWidth())); // HACK! cus of some sort of BUG! need to block the width so incase we display more tight digits the speedMeterLayout stays at same size
        updateTripMeter(0);

        followPositionCheckBox = (CheckBox) findViewById(R.id.MapFollowPosCheckBox);
        followPosition = followPositionCheckBox.isChecked();
        followPositionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                followPosition = isChecked;
                if (followPosition) {
                    handler.removeCallbacks(runnable);
                    handler.post(runnable);
                }
            }
        });
    }
    @Override
    public void onCameraMoveStarted(int reason) {
        Log.d("MyApp", "MapsActivity::onCameraMoveStarted ");
        if (reason == REASON_GESTURE) {
            followPositionCheckBox.setChecked(false);
        }
    }

    private void drawPath(List<Location> path) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Location location : path) {
            polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        Polyline line = mMap.addPolyline(polylineOptions.width(5).color(Color.RED));
    }

    private void updateMapPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (markerSelf == null) {
            markerSelf = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        markerSelf.setPosition(latLng);
        // TODO: Płynne przesuwanie kamery
        if (followPosition) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    // Speed in meters per second
    @SuppressLint("DefaultLocale")
    private void updateSpeedMeter(float speed) {
        // TODO: use only supported locales, if not fallback to default (en)
        speedMeter.setText(String.format("%.1f",speed * (60 * 60) / 1000));
    }

    // trip in meters
    @SuppressLint("DefaultLocale")
    private void updateTripMeter(double trip) {
        // TODO: use only supported locales, if not fallback to default (en)
        tripMeter.setText(String.format("%.1f", trip / 1000));
    }
}
