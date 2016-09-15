package pg.eti.inz.eti.engineer.mainmenu.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import pg.eti.inz.eti.engineer.R;
import pg.eti.inz.eti.engineer.activities.LocaleSettingHelper;
import pg.eti.inz.eti.engineer.activities.SettingsActivity;

/**
 * Main menu of application
 */
public class MainMenuActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleSettingHelper.setActivityLocale(this);
        setContentView(R.layout.main_menu_layout);
    }


    public void navigateToSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
