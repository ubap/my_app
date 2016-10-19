package pg.eti.inz.eti.engineer.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.data.Trip;
import pg.eti.inz.eti.engineer.gps.GPSServiceProvider2;
import pg.eti.inz.eti.engineer.view.CustomImageButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private TextView speedMeter;
    private TextView tripMeter;
    private CustomImageButton followPositionButton;

    private boolean followPosition = true;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            GPSServiceProvider2 GPSService = GPSServiceProvider2.getInstance();
            Location location = GPSService.getLocation();
            if (location != null) {
                // update the camera position
                if (followPosition) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                updateSpeedMeter(location.getSpeed());
                if(GPSService.isTracking()) {
                    Trip trip = GPSService.getTrip();
                    //updateTripMeter(trip.getLength*(;));
                    drawPath(trip.getPath());
                }
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
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        followPositionButton = (CustomImageButton) findViewById(R.id.mapFollowPositionButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                updateFollowPositionButton(false);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                Log.i("myApp", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("myApp", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) { e.printStackTrace(); }

        // update map thread
        handler.post(runnable);

        speedMeter = (TextView) findViewById(R.id.MapSpeedMeter);
        speedMeter.setMinimumWidth(speedMeter.getWidth()); // HACK!
        updateSpeedMeter(0);    // set speed at 0

        tripMeter = (TextView) findViewById(R.id.MapTripMeter);
        tripMeter.setMinimumWidth((tripMeter.getWidth())); // HACK!
        updateTripMeter(0);

        updateFollowPositionButton();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        Log.d("MyApp", "MapsActivity::onCameraMoveStarted ");
        if (reason == REASON_GESTURE) {
            followPosition = false;
            updateFollowPositionButton();
        }
    }

    private void drawPath(List<Location> path) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (Location location : path) {
            polylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        Polyline line = mMap.addPolyline(polylineOptions.width(5).color(Color.RED));
    }

    // Speed in meters per second
    @SuppressLint("DefaultLocale")
    private void updateSpeedMeter(float speed) {
        // TODO: use only supported locale, if not fallback to default (en)
        speedMeter.setText(String.format("%.1f",speed * (60 * 60) / 1000));
    }

    // trip in meters
    @SuppressLint("DefaultLocale")
    private void updateTripMeter(double trip) {
        // TODO: use only supported locale, if not fallback to default (en)
        tripMeter.setText(String.format("%.1f", trip / 1000));
    }

    private void updateFollowPositionButton(boolean followPosition) {
        this.followPosition = followPosition;
        updateFollowPositionButton();
    }

    private void updateFollowPositionButton() {
        if(followPosition) {
            followPositionButton.setImageResource(R.drawable.ic_gps_fixed_black_48dp);
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        } else {
            followPositionButton.setImageResource(R.drawable.ic_gps_not_fixed_black_48dp);
        }
    }

    public void startTrackingBtnClickHandler(View view) {
        Button startStrackingButton = (Button) findViewById(R.id.mapStartTrackingBtn);
        startStrackingButton.setVisibility(View.INVISIBLE);
        LinearLayout speedMeterLayout = (LinearLayout) findViewById(R.id.mapSpeedMeterLayout);
        speedMeterLayout.setVisibility(View.VISIBLE);

        GPSServiceProvider2.getInstance().startTracking(new Trip());
    }

    public void stopTrackingBtnClickHandler(View view) {
        GPSServiceProvider2.getInstance().stopTracking();
    }

    public void followPositionBtnClickHandler(View view) {
        followPosition = !followPosition;
        updateFollowPositionButton();
    }
}
