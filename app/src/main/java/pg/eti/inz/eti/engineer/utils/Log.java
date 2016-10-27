package pg.eti.inz.eti.engineer.utils;

/**
 * Created by ubap on 2016-10-27.
 */

public class Log {
    static private String TAG = "myApp";

    static public void d() {
        android.util.Log.d(TAG, getMethodNameFull());
    }

    static public void d(String log) {
        android.util.Log.d(TAG, getMethodNameFull() + ": " + log);
    }

    static public void v() {
        android.util.Log.v(TAG, getMethodNameFull());
    }

    static public void v(String log) {
        android.util.Log.v(TAG, getMethodNameFull() + ": " + log);
    }

    static public void i() {
        android.util.Log.i(TAG, getMethodNameFull());
    }

    static public void i(String log) {
        android.util.Log.i(TAG, getMethodNameFull() + ": " + log);
    }

    // to jest hack bo nie umialem tego zrobic. call stack jest jakis dziwny i niedeterministyczny
    // szukam pierwszego wolanie z pack name i nie z tej klasy.
    static private String getMethodNameFull() {
        String pack = "pg.eti.inz.eti.engineer";
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
            return "!!ERROR!! Couldn't determine full method name";
        }

        String className = method.getClassName();
        String methodName = method.getMethodName();
        return className + "." + methodName;
    }
}
