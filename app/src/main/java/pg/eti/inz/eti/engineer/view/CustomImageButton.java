package pg.eti.inz.eti.engineer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import pg.eti.inz.eti.engineer.R;

public class CustomImageButton extends ImageView {

    public CustomImageButton(Context context) {
        this(context, null);
    }

    public CustomImageButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setClickable(true);
        registerTouchListener();
    }

    private void registerTouchListener() {
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // padding is getting lost when background is changed
                int pL = getPaddingLeft();   int pT = getPaddingTop();
                int pR = getPaddingRight();  int pB = getPaddingBottom();
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    setBackgroundResource(R.drawable.gpsfixbackgroundclicked);
                    setPadding(pL, pT, pR, pB);
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    setBackgroundResource(R.drawable.gpsfixbackground);
                    setPadding(pL, pT, pR, pB);
                    callOnClick();
                }
                return true;
            }
        });
    }
}
