package pg.eti.inz.eti.engineer.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import pg.eti.inz.eti.engineer.settings.utils.locale.ActiveLocale;

public class LocaleSettingHelper {

    public static void setActivityLocale(Activity activity) {
        Configuration config = new Configuration();
        Resources resources = activity.getResources();

        config.locale = ActiveLocale.getInstance(activity).getActiveLocale();

        /*
        * Także tutaj moja historyjka o locale. Jako, że tablica nazw języków składa się z nazw
        * języków np. "Polski", "English". A locale to kody języków nie ich nazwy, proponuję
        * zastosować się do IETF BCP 47. Czyli zostować np. "pl", "en-US".
        * Walidator: http://schneegans.de
        * Wymagana będzie tablica translacji Nazwa języką <-> Kod języka
        *
        * Ogólnie przy uruchomieniu pierwszej aktywności (onCreate) sprawdzałbym czy odpowiedni wpis
        * w SharedPreference istnieje, jeżeli nie to wpisywałbym tam nazwę języka jaki jest
        * aktualnie używany (jeżeli obsługiwany, jezeli nie to English).
        * */
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
