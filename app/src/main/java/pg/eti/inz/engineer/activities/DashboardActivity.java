package pg.eti.inz.engineer.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.List;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.indicators.base.DashboardComponentFactory;
import pg.eti.inz.engineer.components.indicators.base.RefreshableComponent;
import pg.eti.inz.engineer.components.indicators.base.SwitchThemeComponent;

/**
 * Klasa obslugujaca aktywnosc zawierajaca liczniki i wskazniki
 */
public class DashboardActivity extends AppCompatActivity implements SensorEventListener, PopupMenu.OnMenuItemClickListener {

    private RelativeLayout dashboardLayout;
//    private SpeedometerComponent speedmeter_component;
//    private CompassComponent compass;
//    private TripmeterComponent tripmeter;
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

    private Handler handler;
    private Runnable runnable;

    private List<View> components;
    private RelativeLayout dashboardViewLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(myToolbar);

         dashboardLayout = (RelativeLayout) findViewById(R.id.dashboard_layout);
//        speedmeter_component = (SpeedometerComponent) findViewById(R.id.dashboard_speedometer);
//        compass = (CompassComponent) findViewById(R.id.dashboard_compass);
//        tripmeter = (TripmeterComponent) findViewById(R.id.dashboard_trip_meter);

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

        dashboardViewLayout = (RelativeLayout) findViewById(R.id.dashboard_view_layout);
        components = new LinkedList<>();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                for (View component : components) {
                    if (component instanceof RefreshableComponent) {
                        ((RefreshableComponent) component).update();
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    public void addNewComponent(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.dashboard_add_components, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
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
    public boolean onMenuItemClick(MenuItem item) {
        View component;
        switch (item.getItemId()) {
            case R.id.dashboard_menu_add_speedometer:
                component = DashboardComponentFactory.createSpeedmeterComponent(this);
                break;
            case R.id.dashboard_menu_add_tripmeter:
                component = DashboardComponentFactory.createTripmeterComponent(this);
                break;
            default:
                return false;
        }
        components.add(component);
        dashboardViewLayout.addView(component);
        return true;
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
                    dashboardViewLayout.setBackgroundColor(getResources().getColor(R.color.black));
                    for (View component : components) {
                        if (component instanceof SwitchThemeComponent) {
                            ((SwitchThemeComponent) component).setBrightTheme();
                        }
                        if (component instanceof RefreshableComponent) {
                            ((RefreshableComponent) component).update();
                        }
                    }
                } else {
                    item.setIcon(R.drawable.ic_brightness_3_black_48dp);
                    isNightMode = Boolean.FALSE;
                    dashboardViewLayout.setBackgroundResource(0);
                    for (View component : components) {
                        if (component instanceof SwitchThemeComponent) {
                            ((SwitchThemeComponent) component).setDarkTheme();
                        }
                        if (component instanceof RefreshableComponent) {
                            ((RefreshableComponent) component).update();
                        }
                    }
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
    public void onBackPressed()
    {
        Intent mainMenuActivity = new Intent(this, MainMenuActivity.class);
        mainMenuActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainMenuActivity);
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
//            compass.rotatePointer(currentDegree, azimuthInDegrees);
            currentDegree = -azimuthInDegrees;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
