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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;
import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.rest.account.AccountManager;
import pg.eti.inz.engineer.rest.util.RestAddressHelper;
import pg.eti.inz.engineer.utils.Constants;

/**
 * Widok zmiany hasła użytkownika
 */

public class ChangePasswordActivity  extends AppCompatActivity {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText repeatedNewPasswordEditText;
    private AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_layout);

        oldPasswordEditText = (EditText) findViewById(R.id.changePassword_oldPassword);
        newPasswordEditText = (EditText) findViewById(R.id.changePassword_newPassword);
        repeatedNewPasswordEditText = (EditText) findViewById(R.id.changePassword_repeatedNewPassword);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.CREDENTIALS_SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        accountManager = new AccountManager(sharedPreferences);
    }

    public void changeUserPassword(View view) {
        String oldPassword = oldPasswordEditText.getText().toString();
        if (!accountManager.getUserPassword().equals(oldPassword)) {
            Toast.makeText(this, R.string.toast_changePassword_formError, Toast.LENGTH_LONG).show();
            return;
        }

        changeUserPassword();
    }

    private void changeUserPassword() {
        final String newPassword = newPasswordEditText.getText().toString();
        String repeatedNewPassword = repeatedNewPasswordEditText.getText().toString();

        if (newPassword.equals(repeatedNewPassword)) {
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;

            client.setBasicAuth(accountManager.getUsername(), accountManager.getUserPassword());

            try {
                jsonParams.put(Constants.REST_SERVICE_PASSWORD_SHARED_PREFERENCES_KEY, newPassword);
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            client.put(this, RestAddressHelper.getUserEndpointAdress(), entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    accountManager.setUserPassword(newPassword);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(ChangePasswordActivity.this, R.string.toast_passwordChangeFailed, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, R.string.toast_changePassword_formError, Toast.LENGTH_LONG).show();
        }
    }
}
