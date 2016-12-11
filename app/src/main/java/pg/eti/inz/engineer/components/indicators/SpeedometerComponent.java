package pg.eti.inz.engineer.components.indicators;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.indicators.base.ResizeableComponent;
import pg.eti.inz.engineer.components.indicators.base.SwitchThemeComponent;
import pg.eti.inz.engineer.components.indicators.base.RefreshableComponent;
import pg.eti.inz.engineer.gps.GPSServiceProvider;
import pg.eti.inz.engineer.utils.Constants;
import pg.eti.inz.engineer.utils.Util;

public class SpeedometerComponent extends LinearLayout
        implements RefreshableComponent, SwitchThemeComponent, ResizeableComponent {

    private final String DEFAULT_UNIT = getContext().getString(R.string.map_kmph);
    private final String DEFAULT_SPEED_VALUE = getContext().getString(R.string.map_speedmeter_widthtemplate);
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

    private void init(Context context) {
        View.inflate(context, R.layout.speedmeter_component, this);
        value = (TextView) findViewById(R.id.speedometer_customizable_speed_value);
        unit = (TextView) findViewById(R.id.speedometer_customizable_speed_unit);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value.setText(DEFAULT_SPEED_VALUE);
        unit.setText(DEFAULT_UNIT);
    }

    // SwitchThemeComponent
    @Override
    public void setBrightTheme() {
        value.setTextColor(getResources().getColor(R.color.white));
        unit.setTextColor(getResources().getColor(R.color.white));
    }

    // SwitchThemeComponent
    @Override
    public void setDarkTheme() {
        value.setTextColor(getResources().getColor(R.color.black));
        unit.setTextColor(getResources().getColor(R.color.black));
    }

    // RefreshableComponent
    @Override
    public void update() {
        Location location = GPSServiceProvider.getInstance().getLocation();
        if (location != null) {
            String speed = String.format("%.1f", location.getSpeed() * SPEED_FACTOR);
            value.setText(speed);
        }
    }

    // ResizeableComponent
    @Override
    public int getWidthRatio() {
        return 2;
    }

    // ResizeableComponent
    @Override
    public int getHeightRatio() {
        return 1;
    }

    @Override
    public int getInitialWidth() {
        return getWidthRatio() * Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
    }

    @Override
    public int getInitialHeight() {
        return getHeightRatio() * Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
    }
}
