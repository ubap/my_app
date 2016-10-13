package pg.eti.inz.eti.engineer.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.gps.GPSServiceProvider2;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerSelf;
    private TextView speedMeter;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Location location = GPSServiceProvider2.getInstance().getLocation();
            if (location != null) {
                updateMapPosition(location);
                updateSpeedMeter(location.getSpeed());
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // update map thread
        handler.post(runnable);

        // Init controls
        LinearLayout speedMeterLayout = (LinearLayout) findViewById(R.id.speedMeterLayout);
        speedMeterLayout.setMinimumWidth(speedMeterLayout.getWidth());  // need to block the width so incase we display more tight digits the speedMeterLayout stays at same size

        speedMeter = (TextView) findViewById(R.id.MapSpeedMeter);
        updateSpeedMeter(0);    // set speed at 0
    }

    private void updateMapPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (markerSelf == null) {
            markerSelf = mMap.addMarker(new MarkerOptions().position(latLng));
        }
        markerSelf.setPosition(latLng);
        // TODO: Płynne przesuwanie kamery
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    // Speed in meters per second
    @SuppressLint("DefaultLocale")
    private void updateSpeedMeter(float speed) {
        // TODO: use only supported locales, if not fallback to default (en)
        speedMeter.setText(String.format("%.1f",speed * (60 * 60) / 1000));
    }
}
