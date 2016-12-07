package pg.eti.inz.engineer.utils;

/**
 * Created by ubap on 2016-10-27.
 */

public class Log {
    static private String TAG = "myApp";

    static public void d() {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.d(TAG, getMethodNameFull());
    }

    static public void d(String log) {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.d(TAG, getMethodNameFull() + ": " + log);
    }

    static public void v() {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.v(TAG, getMethodNameFull());
    }

    static public void v(String log) {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.v(TAG, getMethodNameFull() + ": " + log);
    }

    static public void i() {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.i(TAG, getMethodNameFull());
    }

    static public void i(String log) {
        if (!Constants.DEBUG) {
            return;
        }
        android.util.Log.i(TAG, getMethodNameFull() + ": " + log);
    }

    static private String getMethodNameFull() {
        String pack = "pg.eti.inz.engineer";
        StackTraceElement method = null;

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : trace) {
            String elClassName = el.getClassName();
            if (elClassName.contains(pack) && !elClassName.contains(Log.class.getName())) {
                method = el;
                break;
            }
        }
        if (method == null) {
            return "Log !!ERROR!! Couldn't determine full method name";
        }

        String className = method.getClassName();
        String methodName = method.getMethodName();
        return className + "." + methodName;
    }
}
