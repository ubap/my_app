package pg.eti.inz.engineer.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa przechowująca listę tras otrzymaną z serwisu
 */

public class TripResultList {

    private List<Trip> tripList;

    public TripResultList(JSONArray jsonArray) {
        tripList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Trip serviceTrip = new Trip(jsonObject);

                tripList.add(serviceTrip);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Trip> getTripList() {
        return tripList;
    }
}
