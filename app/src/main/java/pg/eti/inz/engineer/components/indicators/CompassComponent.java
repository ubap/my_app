package pg.eti.inz.engineer.components.indicators;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import pg.eti.inz.engineer.R;

public class CompassComponent extends LinearLayout {

    private final long ANIMATION_DURATION_IN_MILIS = 250;

    private ImageView compassPointer;

    public CompassComponent(Context context) {
        super(context);
        init(context);
    }

    public CompassComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompassComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void rotatePointer (float currentDegree, float azimuth) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                currentDegree,
                -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(ANIMATION_DURATION_IN_MILIS);
        rotateAnimation.setFillAfter(true);

        compassPointer.startAnimation(rotateAnimation);
    }

    public void setBrightPointer () {
        compassPointer.setImageResource(R.drawable.ic_navigation_white_48dp);
    }

    public void setDarkPointer () {
        compassPointer.setImageResource(R.drawable.ic_navigation_black_48dp);
    }

    private void init (Context context) {
        View.inflate(context, R.layout.compass, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        compassPointer = (ImageView) findViewById(R.id.compassPointer);
    }
}
