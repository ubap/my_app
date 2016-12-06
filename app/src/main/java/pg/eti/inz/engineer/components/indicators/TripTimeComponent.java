package pg.eti.inz.engineer.components.indicators;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.gps.GPSServiceProvider;

public class TripTimeComponent extends LinearLayout {

    private final String DEFAULT_TRIP_VALUE = "0.0";
    private final Integer UPDATE_DELAY = 1000;

    private TextView value;
    private TextView unit;
    private Runnable updateTrip;
    private Handler tripUpdateHandler;

    public TripTimeComponent(Context context) {
        super(context);
        init(context);
    }

    public TripTimeComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TripTimeComponent(Context context, AttributeSet attrs, int defStyleAttr) {
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
        View.inflate(context, R.layout.trip_timer, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        value = (TextView) findViewById(R.id.tripmeter_trip_unit);
        unit = (TextView) findViewById(R.id.tripmeter_trip_value);
        tripUpdateHandler = new Handler();

        updateTrip = new Runnable() {
            @Override
            public void run() {
                Trip trip = GPSServiceProvider.getInstance().getTrip();
                if (trip != null) {
                    String speed = String.format("%.1f", trip.getDistance() / 1000);
                    value.setText(speed);
                }

                tripUpdateHandler.postDelayed(this, UPDATE_DELAY);
            }
        };

        value.setText(DEFAULT_TRIP_VALUE);

        tripUpdateHandler.post(updateTrip);
    }
}
