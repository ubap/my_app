package pg.eti.inz.engineer.components;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;

public class TripmeterComponent extends LinearLayout {

    private final String DEFAULT_UNIT = getContext().getString(R.string.map_km);
    private final String DEFAULT_TRIP_VALUE = "0.0";
    private final Integer UPDATE_DELAY = 1000;

    private TextView value;
    private TextView unit;
    private Runnable updateTrip;
    private Handler tripUpdateHandler;

    public TripmeterComponent(Context context) {
        super(context);
        init(context);
    }

    public TripmeterComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TripmeterComponent(Context context, AttributeSet attrs, int defStyleAttr) {
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
        View.inflate(context, R.layout.trip_meter, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value = (TextView) findViewById(R.id.tripMeterTripView);
        unit = (TextView) findViewById(R.id.tripMeterUnitView);
        tripUpdateHandler = new Handler();

        updateTrip = new Runnable() {
            @Override
            public void run() {
                Trip trip = GPSServiceProvider2.getInstance().getTrip();
                if (trip != null) {
                    String speed = String.format("%.1f", trip.getDistance() / 1000);
                    value.setText(speed);
                }

                tripUpdateHandler.postDelayed(this, UPDATE_DELAY);
            }
        };

        value.setText(R.string.map_speedmeter_widthtemplate);
        unit.setText(R.string.map_km);

        tripUpdateHandler.post(updateTrip);
    }
}
