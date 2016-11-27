package pg.eti.inz.engineer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.CompassComponent;
import pg.eti.inz.engineer.components.SpeedometerComponent;
import pg.eti.inz.engineer.components.TripmeterComponent;

/**
 * Klasa obslugujaca aktywnosc zawierajaca liczniki i wskazniki
 */
public class DashboardActivity extends AppCompatActivity implements SensorEventListener {

    private RelativeLayout dashboardLayout;
    private SpeedometerComponent speedometer;
    private CompassComponent compass;
    private TripmeterComponent tripmeter;
    private Boolean isNightMode = Boolean.FALSE;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] lastAccelerometerMeasure;
    private float[] lastMagnetometerMeasure;
    private boolean isLastAccelerometerMeasureSet;
    private boolean isLastMagnetometerMeasureSet;
    private float[] rotation;
    private float[] orientation;
    private float currentDegree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(myToolbar);

        dashboardLayout = (RelativeLayout) findViewById(R.id.dashboard_layout);
        speedometer = (SpeedometerComponent) findViewById(R.id.dashboard_speedometer);
        compass = (CompassComponent) findViewById(R.id.dashboard_compass);
        tripmeter = (TripmeterComponent) findViewById(R.id.dashboard_trip_meter);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        lastAccelerometerMeasure = new float[3];
        lastMagnetometerMeasure = new float[3];
        isLastAccelerometerMeasureSet = false;
        isLastMagnetometerMeasureSet = false;
        rotation = new float[9];
        orientation = new float[3];
        currentDegree = 0f;


        RelativeLayout dashboardViewLayout = (RelativeLayout) findViewById(R.id.dashboard_view_layout);


        float density = getResources().getDisplayMetrics().density;
        float widthDp = (float)dashboardViewLayout.getWidth() / density;
    }


    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer id = item.getItemId();

        switch (id) {
            case R.id.dashboard_brightness:
                if (!isNightMode) {
                    item.setIcon(R.drawable.ic_brightness_5_black_48dp);
                    isNightMode = Boolean.TRUE;
                    dashboardLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    speedometer.setBrightTheme();
                    compass.setBrightPointer();
                    tripmeter.setBrightTheme();
                } else {
                    item.setIcon(R.drawable.ic_brightness_3_black_48dp);
                    isNightMode = Boolean.FALSE;
                    dashboardLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    speedometer.setDarkTheme();
                    compass.setDarkPointer();
                    tripmeter.setDarkTheme();
                }
                return true;
            case R.id.action_switch_to_maps:
                Intent mapsIntent = new Intent(this, MapsActivity.class);
                mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mapsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometerMeasure, 0, sensorEvent.values.length);
            isLastAccelerometerMeasureSet = true;
        } else if (sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometerMeasure, 0, sensorEvent.values.length);
            isLastMagnetometerMeasureSet = true;
        }

        if (isLastAccelerometerMeasureSet && isLastMagnetometerMeasureSet) {
            SensorManager.getRotationMatrix(rotation, null, lastAccelerometerMeasure, lastMagnetometerMeasure);
            SensorManager.getOrientation(rotation, orientation);

            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            compass.rotatePointer(currentDegree, azimuthInDegrees);

            currentDegree = -azimuthInDegrees;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
