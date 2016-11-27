package pg.eti.inz.engineer.activities;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

import lombok.libs.org.objectweb.asm.Label;
import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.MeasurePoint;
import pg.eti.inz.engineer.data.Trip;

public class ViewTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Trip trip;
    private GoogleMap map;

    private boolean mapReady;
    private boolean layoutInflated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        mapReady = false;
        layoutInflated = false;
        trip = (Trip) getIntent().getSerializableExtra("trip");
        TextView averageSpeedView = (TextView) findViewById(R.id.tripViewAverageSpeedView);
        averageSpeedView.setText(String.format("%.1f", trip.getAvgSpeed()));

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.view_trip_map);
        mapFragment.getMapAsync(this);

        // to jest potrzebne poniewaz mape mozna ustawic dopiero kiedy znamy jej rozmiar
        View mapView = mapFragment.getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            layoutInflated = true;
                            if (mapReady && layoutInflated) {
                                zoomCameraToPath(trip.getPath());
                            }
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMaxZoomPreference(17.0f);
        drawPath(trip.getPath());
        mapReady = true;
        if (mapReady && layoutInflated) {
            zoomCameraToPath(trip.getPath());
        }
    }

    private void zoomCameraToPath(List<MeasurePoint> path) {
        LatLngBounds bounds = getPathBounds(path);
        if (bounds != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
        }
    }

    private void drawPath(List<MeasurePoint> path) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (MeasurePoint measure : path) {
            polylineOptions.add(new LatLng(measure.getLatitude(), measure.getLongitude()));
        }
        map.addPolyline(polylineOptions.width(5).color(Color.RED));
    }

    @Nullable
    private LatLngBounds getPathBounds(List<MeasurePoint> path) {
        if (path.size() == 0) {
            return null;
        }
        double north = path.get(0).getLatitude();
        double east = path.get(0).getLongitude();
        double south = path.get(0).getLatitude();
        double west = path.get(0).getLongitude();
        for (MeasurePoint measurePoint : path) {
            if (measurePoint.getLatitude() > north) {
                north = measurePoint.getLatitude();
            }
            if (measurePoint.getLatitude() < south) {
                south = measurePoint.getLatitude();
            }
            if (measurePoint.getLongitude() > east) {
                east = measurePoint.getLongitude();
            }
            if (measurePoint.getLongitude() < west) {
                west = measurePoint.getLongitude();
            }
        }
        LatLng southWest = new LatLng(south, west);
        LatLng northEast = new LatLng(north, east);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        return bounds;
    }
}
