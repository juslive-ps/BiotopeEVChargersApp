package ws.tilda.anastasia.biotopeevchargersapp.model.fetch;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Position;
import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Reservation;
import ws.tilda.anastasia.biotopeevchargersapp.model.old_model.Timeslot;

public class ChargerFetcher {
    public static final String TAG = "ChargerFetcher";

    private AssetManager mAssets;

    public ChargerFetcher(Context context) {
        mAssets = context.getAssets();
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream inputStream = mAssets.open("chargers_json.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];

            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");
            Log.i(TAG, "JSON: " + json);


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public Charger downloadCharger(String chargerId) {
        List<Charger> chargers = new ArrayList<>();

        try {
            String jsonString = loadJSONFromAsset();
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(chargers, jsonBody);

            for (Charger charger : chargers) {
                if (charger.getChargerId().equals(chargerId)) {
                    return charger;
                }
            }

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return null;
    }

    public List<Charger> downloadChargers() {
        List<Charger> chargers = new ArrayList<>();

        try {
            String jsonString = loadJSONFromAsset();
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(chargers, jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }

        return chargers;
    }

    private void parseItems(List<Charger> chargers, JSONObject jsonBody)
            throws IOException, JSONException {
        JSONObject dataJsonObject = jsonBody.getJSONObject("data");
        JSONArray chargersJsonArray = dataJsonObject.getJSONArray("searchByArea");

        for (int i = 0; i < chargersJsonArray.length(); i++) {
            JSONObject chargerJsonObject = chargersJsonArray.getJSONObject(i);

            Charger charger = new Charger();
            charger.setChargerId(chargerJsonObject.getString("charger_id"));
            charger.setChargingSpeed(chargerJsonObject.getString("charging_speed"));

            JSONObject positionJsonObject = chargerJsonObject.getJSONObject("position");

            Position position = new Position();
            position.setLatitude(positionJsonObject.getDouble("lat"));
            position.setLongitude(positionJsonObject.getDouble("lon"));

            charger.setPosition(position);

            JSONArray reservationsJsonArray = chargerJsonObject.getJSONArray("reservations");
            List<Reservation> reservations = new ArrayList<>();

            for (int f = 0; f < reservationsJsonArray.length(); f++) {
                JSONObject reservationJsonObject = reservationsJsonArray.getJSONObject(f);

                Reservation reservation = new Reservation();
                reservation.setReservationId(reservationJsonObject.getString("reservation_id"));
                reservation.setUserId(reservationJsonObject.getString("user_id"));


                JSONObject timeslotJsonObject = reservationJsonObject.getJSONObject("timeslot");
                Timeslot timeslot = new Timeslot();
                timeslot.setStartTime(timeslotJsonObject.getString("gte"));
                timeslot.setEndTime(timeslotJsonObject.getString("lte"));

                reservation.setTimeslot(timeslot);

                reservations.add(reservation);

            }

            charger.setReservations(reservations);
            chargers.add(charger);
        }
    }

}
