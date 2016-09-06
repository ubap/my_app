package pg.eti.inz.eti.engineer.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import pg.eti.inz.eti.engineer.settings.utils.locale.ActiveLocale;

/**
 * Parent activity for all of activities in this app
 */
public class EngineersActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityLocale();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setActivityLocale();
        recreate();
    }

    private void setActivityLocale() {
        Configuration config = new Configuration();
        Resources resources = getResources();

        config.locale = ActiveLocale.getInstance(this).getActiveLocale();

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
