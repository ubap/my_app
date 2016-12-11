package pg.eti.inz.engineer.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.rest.account.AccountManager;
import pg.eti.inz.engineer.rest.util.RestAddressHelper;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Ekran rejestracji do serwisu
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText loginEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        loginEditText = (EditText) findViewById(R.id.registerView_loginEditText);
        passwordEditText = (EditText) findViewById(R.id.registerView_passwordEditText);
    }

    public void registerIntoService (View view) {
        RequestParams params = new RequestParams();

        params.put(Constants.REST_SERVICE_USERNAME_SHARED_PREFERENCES_KEY, loginEditText.getText().toString());
        params.put(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY, passwordEditText.getText().toString());

        params.setUseJsonStreamer(true);
        createRestUser(params);
    }

    private void createRestUser(final RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");

        client.post(RestAddressHelper.getUserEndpointAdress(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.CREDENTIALS_SHARED_PREFERENCES_NAME,
                        MODE_PRIVATE);
                AccountManager accountManager = new AccountManager(sharedPreferences);

                accountManager.logIn(loginEditText.getText().toString(), passwordEditText.getText().toString());

                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RegisterActivity.this, R.string.toast_registerFailed, Toast.LENGTH_LONG).show();
            }
        });
    }
}
