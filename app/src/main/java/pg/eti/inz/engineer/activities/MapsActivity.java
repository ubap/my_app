package pg.eti.inz.engineer.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.MeasurePoint;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.gps.CoreService;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;
import pg.eti.inz.engineer.utils.Log;
import pg.eti.inz.engineer.components.CustomImageButton;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveStartedListener {

    static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    static final String ACTIVITY_NAME = "MapsActivity";

    private GoogleMap map;
    private DbManager dbManager;
    private TextView speedMeter;
    private TextView tripMeter;
    private TextView gpsStatusDisplay;
    private CustomImageButton followPositionButton;
    private CustomImageButton stopTrackingButton;
    private Button startTrackingButton;
    private LinearLayout speedMeterLayout;

    private boolean followPosition;
    private Polyline pathPolyLine;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            GPSServiceProvider2 GPSService = GPSServiceProvider2.getInstance();
            // update gps status display
            CoreService.GPSStatus gpsStatus = GPSService.getGPSStatus();
            switch (gpsStatus) {
                case DISABLED:
                    gpsStatusDisplay.setText("GPS DISABLED");
                    break;
                case NOT_FIXED:
                    gpsStatusDisplay.setText("GPS NOT FIXED");
                    break;
                case FIXED:
                    gpsStatusDisplay.setText("GPS FIXED");
                    break;
            }


            Location location = GPSService.getLocation();
            if (location != null) {
                // update camera position
                if (followPosition) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                updateSpeedMeter(location.getSpeed());
                if (GPSService.isTracking()) {
                    Trip trip = GPSService.getTrip();
                    updateTripMeter(trip.getDistance());
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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences sharedPreferences = getSharedPreferences("lastActivity", MODE_PRIVATE);
        SharedPreferences.Editor lastActivityEditor = sharedPreferences.edit();
        lastActivityEditor.putString("name", ACTIVITY_NAME);
        lastActivityEditor.commit();

        startTrackingButton = (Button) findViewById(R.id.mapStartTrackingBtn);
        stopTrackingButton = (CustomImageButton) findViewById(R.id.mapStopTrackingBtn);
        followPositionButton = (CustomImageButton) findViewById(R.id.mapFollowPositionButton);
        speedMeterLayout = (LinearLayout) findViewById(R.id.mapSpeedMeterLayout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(myToolbar);

        followPosition = true;

        // initial visible buttons
        if (GPSServiceProvider2.getInstance().isTracking()) {
            startTrackingButton.setVisibility(View.INVISIBLE);
            stopTrackingButton.setVisibility(View.VISIBLE);
            speedMeterLayout.setVisibility(View.VISIBLE);
        } else {
            startTrackingButton.setVisibility(View.VISIBLE);
            stopTrackingButton.setVisibility(View.INVISIBLE);
            speedMeterLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                updateFollowPositionButton(false);
                map.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                Log.i(place.getName().toString());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d(status.getStatusMessage());

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
                    Intent intent = new PlaceAutocomplete.
                            IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                return true;
            case R.id.action_switch_to_dashboard:
                Intent dashboardIntent = new Intent(this, DashboardActivity.class);
                dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dashboardIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setLocationSource(GPSServiceProvider2.getInstance().getLocationSource());
        Location lastKnownLocation = GPSServiceProvider2.getInstance().getLastKnownLocation();
        map.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
        map.setOnCameraMoveStartedListener(this);
        try {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        // update map thread
        handler.post(runnable);

        speedMeter = (TextView) findViewById(R.id.MapSpeedMeter);
        speedMeter.setMinimumWidth(speedMeter.getWidth()); // HACK!
        updateSpeedMeter(0);    // set speed at 0

        tripMeter = (TextView) findViewById(R.id.MapTripMeter);
        tripMeter.setMinimumWidth((tripMeter.getWidth())); // HACK!
        updateTripMeter(0);

        gpsStatusDisplay = (TextView) findViewById(R.id.textView);

        updateFollowPositionButton();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        Log.d();
        if (reason == REASON_GESTURE) {
            followPosition = false;
            updateFollowPositionButton();
        }
    }

    private void drawPath(List<MeasurePoint> path) {
        if (pathPolyLine == null) {
            PolylineOptions polylineOptions = new PolylineOptions();
            for (MeasurePoint measure : path) {
                polylineOptions.add(new LatLng(measure.getLatitude(), measure.getLongitude()));
            }
            pathPolyLine = map.addPolyline(polylineOptions.width(5).color(Color.RED));
        } else {
            List<LatLng> latLngList = new LinkedList<>();
            for (MeasurePoint measure : path) {
                latLngList.add(new LatLng(measure.getLatitude(), measure.getLongitude()));
            }
            pathPolyLine.setPoints(latLngList);
        }
    }

    private void clearPath() {
        Log.d();
        if (pathPolyLine != null) {
            pathPolyLine.remove();
        }
        pathPolyLine = null;
    }

    // Speed in meters per second
    @SuppressLint("DefaultLocale")
    private void updateSpeedMeter(float speed) {
        // TODO: use only supported locale, if not fallback to default (en)
        speedMeter.setText(String.format("%.1f", speed * (60 * 60) / 1000));
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
        if (followPosition) {
            followPositionButton.setImageResource(R.drawable.ic_gps_fixed_black_48dp);
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        } else {
            followPositionButton.setImageResource(R.drawable.ic_gps_not_fixed_black_48dp);
        }
    }

    public void startTrackingBtnClickHandler(View view) {
        Button startTrackingButton = (Button) findViewById(R.id.mapStartTrackingBtn);
        startTrackingButton.setVisibility(View.INVISIBLE);
        CustomImageButton stopTrackingButton = (CustomImageButton)
                findViewById(R.id.mapStopTrackingBtn);
        stopTrackingButton.setVisibility(View.VISIBLE);
        LinearLayout speedMeterLayout = (LinearLayout) findViewById(R.id.mapSpeedMeterLayout);
        speedMeterLayout.setVisibility(View.VISIBLE);

        GPSServiceProvider2.getInstance().startTracking(new Trip());
    }

    public void stopTrackingBtnClickHandler(View view) {
        Button startTrackingButton = (Button) findViewById(R.id.mapStartTrackingBtn);
        startTrackingButton.setVisibility(View.VISIBLE);
        CustomImageButton stopTrackingButton = (CustomImageButton)
                findViewById(R.id.mapStopTrackingBtn);
        stopTrackingButton.setVisibility(View.INVISIBLE);
        LinearLayout speedMeterLayout = (LinearLayout) findViewById(R.id.mapSpeedMeterLayout);
        speedMeterLayout.setVisibility(View.INVISIBLE);

        Trip trip = GPSServiceProvider2.getInstance().stopTracking();
        if (dbManager == null) {
            dbManager = new DbManager(this);
        }
        dbManager.saveTrip(trip);

        clearPath();
    }

    public void followPositionBtnClickHandler(View view) {
        followPosition = !followPosition;
        updateFollowPositionButton();
    }

    public void zoomInBtnClickHandler(View view) {
        float newZoom = map.getCameraPosition().zoom + 1.0f;
        map.animateCamera(CameraUpdateFactory.zoomTo(newZoom));
        Log.d("new zoom level: " + Float.toString(newZoom));
    }

    public void zoomOutBtnClickHandler(View view) {
        float newZoom = map.getCameraPosition().zoom - 1.0f;
        map.animateCamera(CameraUpdateFactory.zoomTo(newZoom));
        Log.d("new zoom level: " + Float.toString(newZoom));
    }
}
