package pg.eti.inz.engineer.components;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.gps.GPSServiceProvider2;
import pg.eti.inz.engineer.utils.Log;

public class AverageSpeedComponent extends LinearLayout {

    private final String DEFAULT_UNIT = getContext().getString(R.string.map_kmph);
    private final String DEFAULT_SPEED_VALUE = "0.0";
    private final Integer UPDATE_DELAY = 1000;
    private final Double SPEED_FACTOR = 3.6;

    private TextView value;
    private TextView unit;
    private Runnable updateSpeed;
    private Handler speedUpdateHandler;


    private int posX;
    private int posY;
    private int clickedPosX;
    private int clickedPosY;

    public AverageSpeedComponent(Context context) {
        this(context, null);
    }

    public AverageSpeedComponent(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public AverageSpeedComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //setOnLongClickListener();
        //setOnTouchListener(onTouchListener);

        setOnDragListener(onDragListener);

        View.inflate(context, R.layout.speedometer_customizable, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);


        value = (TextView) findViewById(R.id.speedometer_customizable_speed_value);
        unit = (TextView) findViewById(R.id.speedometer_customizable_speed_unit);
        speedUpdateHandler = new Handler();

        updateSpeed = new Runnable() {
            @Override
            public void run() {
                Trip trip = GPSServiceProvider2.getInstance().getTrip();
                if (trip != null) {
                    String speed = String.format("%.1f", trip.getAvgSpeed() * SPEED_FACTOR);
                    value.setText(speed);
                }
                speedUpdateHandler.postDelayed(this, UPDATE_DELAY);
            }
        };

        value.setText(DEFAULT_SPEED_VALUE);
        unit.setText(DEFAULT_UNIT);

        speedUpdateHandler.post(updateSpeed);
    }

    public void setBrightTheme() {
        value.setTextColor(getResources().getColor(R.color.white));
        unit.setTextColor(getResources().getColor(R.color.white));
    }

    public void setDarkTheme() {
        value.setTextColor(getResources().getColor(R.color.black));
        unit.setTextColor(getResources().getColor(R.color.black));
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
//            clickedPosX = v.
            return false;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    clickedPosX = (int) event.getX();
                    clickedPosY = (int) event.getY();
                    Log.d("ACTION_DOWN clickedPosX: "+ Integer.toString(clickedPosX) +", clickedPosY: " + Integer.toString(clickedPosY));
                    break;
                default:
                    clickedPosX = (int) event.getX();
                    clickedPosY = (int) event.getY();
                    Log.d("default clickedPosX: "+ Integer.toString(clickedPosX) +", clickedPosY: " + Integer.toString(clickedPosY));

                    break;
            }
            return false;
        }
    };

    private View.OnDragListener onDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //break;
                case DragEvent.ACTION_DRAG_ENDED:
                    clickedPosX = (int) event.getX();
                    clickedPosY = (int) event.getY();
                    Log.d("onDrag clickedPosX: "+ Integer.toString(clickedPosX) +", clickedPosY: " + Integer.toString(clickedPosY));

                    break;
                default:
                    break;
            }

            return false;
        }
    };


}
