package pg.eti.inz.engineer.components;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.base.SwitchThemeComponent;
import pg.eti.inz.engineer.components.base.RefreshableComponent;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;

public class SpeedometerComponent extends LinearLayout implements RefreshableComponent, SwitchThemeComponent {

    private final String DEFAULT_UNIT = getContext().getString(R.string.map_kmph);
    private final String DEFAULT_SPEED_VALUE = getContext().getString(R.string.map_speedmeter_widthtemplate);
    private final Integer UPDATE_DELAY = 1000;
    private final Double SPEED_FACTOR = 3.6;
    public final static int SIZE_RATIO_X = 2;
    public final static int SIZE_RATIO_Y = 1;

    private TextView value;
    private TextView unit;

    public SpeedometerComponent (Context context) {
        this(context, null);
    }

    public SpeedometerComponent (Context context, AttributeSet attrs) {
        this(context, null, 0);
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

    private void init(Context context) {
        View.inflate(context, R.layout.speedometer_customizable, this);
        value = (TextView) findViewById(R.id.speedometer_customizable_speed_value);
        unit = (TextView) findViewById(R.id.speedometer_customizable_speed_unit);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value.setText(DEFAULT_SPEED_VALUE);
        unit.setText(DEFAULT_UNIT);
    }

    public void update() {
        Location location = GPSServiceProvider2.getInstance().getLocation();
        if (location != null) {
            String speed = String.format("%.1f", location.getSpeed() * SPEED_FACTOR);
            value.setText(speed);
        }
    }
}
