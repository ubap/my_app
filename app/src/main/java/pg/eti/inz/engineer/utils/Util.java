package pg.eti.inz.engineer.utils;

import android.content.Context;

/**
 * Created by ubap on 2016-11-30.
 */

public class Util {
    public static int dpFromPx(final Context context, final int px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(final Context context, final int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }
}
