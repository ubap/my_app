package pg.eti.inz.engineer.rest.route;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import pg.eti.inz.engineer.R;
import pg.eti.inz.engineer.data.DbManager;
import pg.eti.inz.engineer.data.Trip;
import pg.eti.inz.engineer.data.TripResultList;
import pg.eti.inz.engineer.rest.account.AccountManager;
import pg.eti.inz.engineer.rest.util.RestAddressHelper;

/**
 * Klasa odpowiadająca za odbieranie, wysyłanie i przetwarzanie danych z serwisu.
 */

public class RouteManager {

    private Context context;
    private AccountManager accountManager;

    public RouteManager(Context context, AccountManager accountManager) {
        this.context = context;
        this.accountManager = accountManager;
    }

    public void synchronizeTripsFromService(final DbManager dbManager) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        client.setBasicAuth(accountManager.getUsername(), accountManager.getUserPassword());

        client.get(RestAddressHelper.getRoutesEndpointAddress(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                TripResultList tripResultList = new TripResultList(response);
                List<Trip> trips = dbManager.getAllTrips();

                for (Trip trip : trips) {
                    if (trip.getIsSynchronized() == 0) {
                        continue;
                    } else {
                        for (Trip tripFromService : tripResultList.getTripList()) {
                            if (tripFromService.getId() == trip.getRemoteId()) {
                                break;
                            }
                            tripFromService.setIsSynchronized(1);
                            tripFromService.setRemoteId(tripFromService.getId());
                            dbManager.saveTrip(tripFromService);
                        }
                    }
                }
            }
        });
    }

    public void synchronizeTripsWithService(List<Trip> unsynchronizedRoutes, final DbManager dbManager) {
        for (final Trip trip : unsynchronizedRoutes) {
            AsyncHttpClient client = new AsyncHttpClient();
            StringEntity entity = null;
            JSONObject params = trip.toJSONObject();

            client.setBasicAuth(accountManager.getUsername(), accountManager.getUserPassword());

            try {
                entity = new StringEntity(params.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(context, RestAddressHelper.getRoutesEndpointAddress(), entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    ByteBuffer wrapper = ByteBuffer.wrap(responseBody);
                    int remoteTripId = wrapper.getInt();

                    trip.setRemoteId(remoteTripId);
                    trip.setIsSynchronized(1);

                    dbManager.updateTripSynchronization(trip);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, R.string.toast_synchronizeWithServiceFailed, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
