package pg.eti.inz.engineer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.rest.account.AccountManager;
import pg.eti.inz.engineer.rest.util.RestAddressHelper;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Ekran logowania do serwisu - wyświetlany po uruchomieniu aplikacji
 */

public class LoginActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;
    private AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CREDENTIALS_SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        accountManager = new AccountManager(sharedPreferences);

        if (accountManager.isUserLogged()) {
            navigateToMainMenu(this.getCurrentFocus());
        } else {
            setContentView(R.layout.login_layout);
            loginEditText = (EditText) findViewById(R.id.login_loginEditText);
            passwordEditText = (EditText) findViewById(R.id.login_passwordEditText);
        }
    }

    @Override
    protected void onResume() {
        if (accountManager.isUserLogged()) {
            navigateToMainMenu(this.getCurrentFocus());
        }
        super.onResume();
    }

    public void signIntoService (final View view) {
        final String username = loginEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.setBasicAuth(username, password);

        client.get(RestAddressHelper.getUserEndpointAdress(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                accountManager.logIn(username, password);
                navigateToMainMenu(view);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, R.string.toast_loginFailed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void navigateToMainMenu (View view) {
        Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
        mainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainMenuIntent);
    }

    public void navigateToRegister (View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
