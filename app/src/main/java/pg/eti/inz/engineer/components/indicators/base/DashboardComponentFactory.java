package pg.eti.inz.engineer.components.indicators.base;

import android.content.Context;
import android.widget.RelativeLayout;

import pg.eti.inz.engineer.activities.MapsActivity;
import pg.eti.inz.engineer.utils.Util;

/**
 * Created by ubap on 2016-12-04.
 */

public class DashboardComponentFactory {
    public static DashboardComponent createSpeedmeterComponent(Context context) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.pxFromDp(context, 32*4), Util.pxFromDp(context, 32*2));
        params.topMargin = Util.pxFromDp(context, 0);
        params.leftMargin = Util.pxFromDp(context, 0);
        DashboardComponent component = new DashboardComponent(context, DashboardComponent.ComponentType.SPEEDMETER, params, true);
        return component;
    }

    public static DashboardComponent createTripmeterComponent(Context context) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Util.pxFromDp(context, 32*4), Util.pxFromDp(context, 32*2));
        params.topMargin = Util.pxFromDp(context, 0);
        params.leftMargin = Util.pxFromDp(context, 0);
        DashboardComponent component = new DashboardComponent(context, DashboardComponent.ComponentType.TRIPMETER, params, true);
        return component;
    }
}
