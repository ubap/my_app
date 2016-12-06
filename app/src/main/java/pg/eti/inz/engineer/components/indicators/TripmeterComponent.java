package pg.eti.inz.engineer.components.indicators;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.indicators.base.ResizeableComponent;
import pg.eti.inz.engineer.components.indicators.base.SwitchThemeComponent;
import pg.eti.inz.engineer.components.indicators.base.RefreshableComponent;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.gps.GPSServiceProvider;

public class TripmeterComponent extends LinearLayout
        implements RefreshableComponent, SwitchThemeComponent, ResizeableComponent {

    private final String DEFAULT_UNIT = getContext().getString(R.string.map_km);
    private final String DEFAULT_VALUE = getContext().getString(R.string.map_speedmeter_widthtemplate);
    public final static int SIZE_RATIO_X = 2;
    public final static int SIZE_RATIO_Y = 1;

    private TextView value;
    private TextView unit;

    public TripmeterComponent (Context context) {
        this(context, null);
    }

    public TripmeterComponent (Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public TripmeterComponent (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.tripmeter_component, this);
        value = (TextView) findViewById(R.id.tripmeter_trip_value);
        unit = (TextView) findViewById(R.id.tripmeter_trip_unit);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value.setText(DEFAULT_VALUE);
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
        Trip trip = GPSServiceProvider.getInstance().getTrip();
        if (trip != null) {
            String speed = String.format("%.1f", trip.getDistance() / 1000);
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
}
