package pg.eti.inz.eti.engineer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.gps.CoreService;
import pg.eti.inz.eti.engineer.gps.GPSServiceProvider2;

/**
 * Main menu of application
 */
public class MainMenuActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        GPSServiceProvider2.getInstance().init(this);
        Intent intent = new Intent(this, CoreService.class);
        startService(intent);

    }

    public void navigateToSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void navigateToMap(View view) {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }
}
