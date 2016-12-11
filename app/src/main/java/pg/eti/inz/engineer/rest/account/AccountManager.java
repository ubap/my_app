package pg.eti.inz.engineer.rest.account;

import android.content.SharedPreferences;

import pg.eti.inz.engineer.utils.Constants;

/**
 * Klasa odpowiedzialna za zarządzanie kontem użytkownika
 */

public class AccountManager {

    SharedPreferences userSharedPreferences;

    public AccountManager(SharedPreferences sharedPreferences) {
        this.userSharedPreferences = sharedPreferences;
    }

    public boolean isUserLogged() {
        return userSharedPreferences.contains(Constants.REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY) &&
                userSharedPreferences.contains(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY);
    }

    /**
     * Wylogowanie użytkownika
     */
    public void logout() {
        validateUser();
        SharedPreferences.Editor editor = userSharedPreferences.edit();

        editor.remove(Constants.REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY);
        editor.remove(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY);
        editor.apply();
    }

    /**
     * Pomyślne zalogowanie do serwisu
     * @param username  nazwa użytkownika
     * @param password  haslo
     */
    public void logIn(String username, String password) {
        validateUser(username, password);
        SharedPreferences.Editor editor = userSharedPreferences.edit();

        editor.putString(Constants.REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY, username);
        editor.putString(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY, password);

        editor.apply();
    }

    public String getUsername() {
        return userSharedPreferences.getString(Constants.REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY, null);
    }

    public String getUserPassword() {
        return userSharedPreferences.getString(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY, null);
    }

    public void setUserPassword(String password) {
        SharedPreferences.Editor editor = userSharedPreferences.edit();

        editor.putString(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY, password);
        editor.apply();
    }

    private void validateUser() {
        validateUser(getUsername(), getUserPassword());
    }

    private void validateUser(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("User is not valid.");
        }
    }

}
