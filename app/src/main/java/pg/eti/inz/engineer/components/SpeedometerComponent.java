package pg.eti.inz.engineer.components;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;

public class SpeedometerComponent extends LinearLayout {

    private final String DEFAULT_UNIT = "km/h";
    private final String DEFAULT_SPEED_VALUE = "0.0";
    private final Integer UPDATE_DELAY = 1000;
    private final Double SPEED_FACTOR = 3.6;

    private TextView value;
    private TextView unit;
    private Runnable updateSpeed;
    private Handler speedUpdateHandler;

    public SpeedometerComponent (Context context) {
        super(context);
        init(context);
    }

    public SpeedometerComponent (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpeedometerComponent (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setBrightTheme() {
        value.setTextColor(getResources().getColor(R.color.white));
        unit.setTextColor(getResources().getColor(R.color.white));
    }

    public void setDarkTheme() {
        value.setTextColor(getResources().getColor(R.color.black));
        unit.setTextColor(getResources().getColor(R.color.black));
    }

    private void init (Context context) {
        View.inflate(context, R.layout.speedometer, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value = (TextView) findViewById(R.id.speedometer_speed_value);
        unit = (TextView) findViewById(R.id.speedometer_speed_unit);
        speedUpdateHandler = new Handler();

        updateSpeed = new Runnable() {
            @Override
            public void run() {
                Location location = GPSServiceProvider2.getInstance().getLocation();
                if (location != null) {
                    String speed = String.format("%.1f", location.getSpeed() * SPEED_FACTOR);
                    value.setText(speed);
                }
                speedUpdateHandler.postDelayed(this, UPDATE_DELAY);
            }
        };

        value.setText(DEFAULT_SPEED_VALUE);
        unit.setText(DEFAULT_UNIT);

        speedUpdateHandler.post(updateSpeed);
    }
}
