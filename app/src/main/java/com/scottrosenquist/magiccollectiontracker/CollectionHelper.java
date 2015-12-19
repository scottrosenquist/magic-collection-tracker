package com.scottrosenquist.magiccollectiontracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CollectionHelper {
    private static final String APP_SIMPLE_NAME = CollectionHelper.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences preferences;
    private Editor editor;
    private static final String COLLECTION = "collection";

    public CollectionHelper(Context context) {
        this.preferences = context.getSharedPreferences(APP_SIMPLE_NAME, Activity.MODE_PRIVATE);
        this.editor = preferences.edit();
    }

    public int getCardQuantity(String setCode, String cardName) {
        int quantity = 0;
        String rawCollection = preferences.getString(COLLECTION, "{}");
        try {
            JSONObject collection = new JSONObject(rawCollection);
            if (collection.has(setCode) && collection.getJSONObject(setCode).has(cardName)) {
                    quantity = collection.getJSONObject(setCode).getJSONObject(cardName).getInt("Quantity");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quantity;
    }

    public void setCardQuantity(final String setCode, String cardName, int quantity, boolean foil) {
        String rawCollection = preferences.getString(COLLECTION, "{}");
        try {
            JSONObject collection = new JSONObject(rawCollection);
            JSONObject set;
            if (collection.has(setCode)) {
                set = collection.getJSONObject(setCode);
            } else {
                set = new JSONObject();
            }
            if (quantity > 0) {
                JSONObject card = new JSONObject();
                card.put("Quantity", quantity);
                if (foil) {
                    card.put("Foil", true);
                }
                set.put(cardName, card);
            } else {
                set.remove(cardName);
            }

            if (set.length() > 0) {
                collection.put(setCode, set);
            } else {
                collection.remove(setCode);
            }
            editor.putString(COLLECTION, collection.toString());
            editor.apply();

            Log.d("scottdebug", "collection: " + collection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
