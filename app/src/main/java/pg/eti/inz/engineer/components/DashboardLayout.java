package pg.eti.inz.engineer.components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import pg.eti.inz.engineer.utils.Constants;
import pg.eti.inz.engineer.utils.Util;

/**
 * Created by ubap on 2016-11-30.
 */

public class DashboardLayout extends RelativeLayout {
    public DashboardLayout(Context context) {
        this(context, null);
    }

    public DashboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint mPaint;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(0xffffffff);
        //canvas.drawLine(50, 50, 100, 0, mPaint);

        Resources r = getResources();
        int squareSizePx = Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        for (int i = squareSizePx; i <screenWidth; i+=squareSizePx) {
            canvas.drawLine(i, 0, i, screenHeight, mPaint);
        }

        for (int i = squareSizePx; i <screenHeight; i+=squareSizePx) {
            canvas.drawLine(0, i, screenWidth, i, mPaint);
        }

        super.dispatchDraw(canvas);
    }
}
