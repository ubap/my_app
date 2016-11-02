package pg.eti.inz.engineer.activities;

import android.app.Instrumentation;
import android.support.test.espresso.action.ViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import pg.eti.inz.engineer.R;

import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.InstrumentationRegistry.*;

import static junit.framework.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainMenuActivityTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityRule = new ActivityTestRule<>(MainMenuActivity.class);

    @Before
    public void unlockScreen() {
        final MainMenuActivity activity = mActivityRule.getActivity();
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    @Test
    public void shouldGoToSettingsActivity() {
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation()
                .addMonitor(SettingsActivity.class.getName(), null, false);

        onView(withId(R.id.settingsMainMenuBtn)).perform(ViewActions.click());

        SettingsActivity launchedActivity = (SettingsActivity) getInstrumentation()
                .waitForMonitorWithTimeout(activityMonitor, 5000);

        assertNotNull(launchedActivity);
        launchedActivity.finish();
    }
}
