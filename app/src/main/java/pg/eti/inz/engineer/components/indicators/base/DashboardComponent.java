package pg.eti.inz.engineer.components.indicators.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.components.indicators.SpeedometerComponent;
import pg.eti.inz.engineer.components.indicators.TripmeterComponent;
import pg.eti.inz.engineer.utils.Constants;
import pg.eti.inz.engineer.utils.Log;
import pg.eti.inz.engineer.utils.Util;

public class DashboardComponent extends LinearLayout
        implements View.OnTouchListener, RefreshableComponent, SwitchThemeComponent {

    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;
    private GestureDetector longPressDetector;
    private boolean moving = false;
    private float startX = 0.f;
    private float startY = 0.f;
    private int scale = 1;

    private ImageView imageView;
    private View component;

    public enum ComponentType {
        SPEEDMETER, TRIPMETER
    }

    public DashboardComponent (Context context, ComponentType componentType, RelativeLayout.LayoutParams params, boolean customizableMode) {
        super(context);
        init(context, componentType, params, customizableMode);
    }

    public DashboardComponent (Context context) {
        this(context, null);
    }

    public DashboardComponent (Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public DashboardComponent (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null, null, false);
    }

    private void init(Context context, ComponentType componentType, RelativeLayout.LayoutParams params, boolean customizableMode) {
        View.inflate(context, R.layout.blank_layout, this);
        if (params != null) {
            setLayoutParams(params);
        }

        LinearLayout layout = (LinearLayout) findViewById(R.id.blank_layout);
        imageView = new ImageView(getContext());
        imageView.setBackgroundResource(android.R.color.transparent);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        layout.addView(imageView);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        if (componentType == null) {
            componentType = ComponentType.SPEEDMETER;
        }

        switch (componentType) {
            case TRIPMETER:
                component = new TripmeterComponent(getContext());
            break;
            case SPEEDMETER:
            default:
                component = new SpeedometerComponent(getContext());
                break;
        }

        update();

        if (customizableMode) {
            scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    scaleFactor = 1.f;
                    setBackgroundResource(R.drawable.dashboard_component_resizing);
                    return super.onScaleBegin(detector);
                }
                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    setBackgroundResource(0);
                    super.onScaleEnd(detector);
                }
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if (!(component instanceof ResizeableComponent)) {
                        return false;
                    }

                    scaleFactor *= detector.getScaleFactor();
                    Log.d("onScale, scaleFactor: " + Float.toString(scaleFactor));

                    int singleStepResizeMovePx = Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
                    if ((scaleFactor - 1.f)* getWidth() > singleStepResizeMovePx) {
                        scaleFactor = 1.f;
                        scale++;
                        updateComponentSize();
                    } else if ((scaleFactor - 1.f)* getWidth() < -singleStepResizeMovePx) {
                        scaleFactor = 1.f;
                        if (scale > 1) {
                            scale--;
                            updateComponentSize();
                        }
                    }
                    invalidate();
                    return true;
                }
            });
            longPressDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) { return false; }
                @Override
                public void onShowPress(MotionEvent e) { }
                @Override
                public boolean onSingleTapUp(MotionEvent e) { return false; }
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
                @Override
                public void onLongPress(MotionEvent e) {
                    Log.d("onLongPress");
                    moving = true;
                    startX = e.getX(); startY = e.getY();
                    setBackgroundResource(R.drawable.dashboard_component_moving);
                }
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false; }
            });
            setOnTouchListener(this);
        }
    }

    private void updateComponentSize() {
        int widthRadio = ((ResizeableComponent) component).getWidthRatio();
        int heightRatio = ((ResizeableComponent) component).getHeightRatio();
        int singleStepResizeMovePx = Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
        params.width = scale * widthRadio * singleStepResizeMovePx;
        params.height = scale * heightRatio * singleStepResizeMovePx;
        setLayoutParams(params);
    }

    public void update() {
        if (component == null)
            return;
        if (component instanceof RefreshableComponent) {
            ((RefreshableComponent) component).update();
        }
        Bitmap bitmap = getViewBitmap(component);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        longPressDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            moving = false;
            setBackgroundResource(0);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && moving) {
            float movedX = event.getX() - startX;
            float movedY = event.getY() - startY;
            Log.d("onScroll movedX: " + Float.toString(movedX) + ", movedY: " + Float.toString(movedY));

            int singleStepResizeMovePx = Util.pxFromDp(getContext(), Constants.SINGLE_STEP_SIZE_RESIZE_MOVE_DP);
            if ((int)movedX > singleStepResizeMovePx) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.leftMargin += singleStepResizeMovePx;
                setLayoutParams(params);
            } else if ((int)movedX < -singleStepResizeMovePx) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.leftMargin -= singleStepResizeMovePx;
                setLayoutParams(params);
            }

            if ((int)movedY > singleStepResizeMovePx) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.topMargin += singleStepResizeMovePx;
                setLayoutParams(params);
            } else if ((int)movedY < -singleStepResizeMovePx) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.topMargin -= singleStepResizeMovePx;
                setLayoutParams(params);
            }
        }

        return true;
    }


    @Nullable
    private Bitmap getViewBitmap(View v) {
        v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.d( "failed getViewBitmap(" + v + ")");
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    @Override
    public void setBrightTheme() {
        if (component instanceof SwitchThemeComponent) {
            ((SwitchThemeComponent) component).setBrightTheme();
        }
    }

    @Override
    public void setDarkTheme() {
        if (component instanceof SwitchThemeComponent) {
            ((SwitchThemeComponent) component).setDarkTheme();
        }
    }
}
