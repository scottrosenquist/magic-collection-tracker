package com.scottrosenquist.magiccollectiontracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CollectionHelper {
    private Context activityContext;
    private static final String COLLECTION_HELPER_PREFERENCES = "collection_helper_preferences"; //  Name of the file -.xml
    private SharedPreferences preferences;
    private Editor editor;

    public CollectionHelper(Context applicationContext, Context activityContext) {
        this.activityContext = activityContext;
        this.preferences = applicationContext.getSharedPreferences(COLLECTION_HELPER_PREFERENCES, Activity.MODE_PRIVATE);
        this.editor = preferences.edit();
        this.editor.apply();
    }

    public int getCardQuantity(String setName, String cardName) {
        return getCardQuantity(setName, cardName, false);
    }

    public int getCardQuantityFoil(String setName, String cardName) {
        return getCardQuantity(setName, cardName, true);
    }

    public int getCardQuantity(String setName, String cardName, boolean getQuantityFoil) {
        int quantity = 0;
        if (preferences.contains(setName)) {
            String rawSet = preferences.getString(setName, "{}");
            try {
                JSONObject set = new JSONObject(rawSet);
                if (set.has(cardName)) {
                    JSONObject card = set.getJSONObject(cardName);
                    if (getQuantityFoil) {
                        quantity = card.optInt("QuantityFoil");
                    } else {
                        quantity = card.optInt("Quantity");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return quantity;
    }

    public Map<String, Integer> getCardQuantities(String setName, String cardName) {
        Map<String, Integer> quantities = new HashMap<>();
        quantities.put("Quantity", 0);
        quantities.put("QuantityFoil", 0);
        if (preferences.contains(setName)) {
            String rawSet = preferences.getString(setName, "{}");
            try {
                JSONObject set = new JSONObject(rawSet);
                if (set.has(cardName)) {
                    JSONObject card = set.getJSONObject(cardName);
                    quantities.put("Quantity", card.optInt("Quantity"));
                    quantities.put("QuantityFoil", card.optInt("QuantityFoil"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return quantities;
    }

    public JSONObject getSetCollection(String setName) {
        JSONObject setCollection = new JSONObject();
        if (preferences.contains(setName)) {
            String rawSetCollection = preferences.getString(setName, "{}");
            try {
                setCollection = new JSONObject(rawSetCollection);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return setCollection;
    }

    public void setCardQuantity(String setName, String cardName, int quantity) {
        setCardQuantity(setName, cardName, quantity, -1);
    }

    public void setCardQuantityFoil(String setName, String cardName, int quantityFoil) {
        setCardQuantity(setName, cardName, -1, quantityFoil);
    }

    public void setCardQuantityZero(String setName, String cardName) {
        setCardQuantity(setName, cardName, 0, 0);
    }

    public void setCardQuantity(final String setName, String cardName, int quantity, int quantityFoil) {
        String rawSetData = preferences.getString(setName, "{}");
        JSONObject set;
        JSONObject card;
        try {
            set = new JSONObject(rawSetData);
            if (set.has(cardName)) {
                card = set.getJSONObject(cardName);
            } else {
                card = new JSONObject();
            }
            if (quantity > 0) {
                card.put("Quantity", quantity);
            } else if (quantity == 0) {
                card.remove("Quantity");
            }
            if (quantityFoil > 0) {
                card.put("QuantityFoil", quantityFoil);
            } else if (quantityFoil == 0) {
                card.remove("QuantityFoil");
            }
            if (card.has("Quantity") || card.has("QuantityFoil")) {
                set.put(cardName, card);
            } else {
                set.remove(cardName);
            }
            if (set.length() > 0) {
                editor.putString(setName, set.toString());
            } else {
                editor.remove(setName);
            }
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void exportToDeckboxCsv() {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "MagicCollectionDeckbox.csv";
        String filePath = baseDir + File.separator + fileName;
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(filePath));

            List<String[]> data = new ArrayList<>();
            data.add(new String[] {"Count", "Name", "Edition", "Condition", "Language", "Foil"});

            Map<String, ?> preferencesAll = preferences.getAll();

            for (String setName : preferencesAll.keySet()) {
                String rawSet = (String) preferencesAll.get(setName);
                try {
                    JSONObject setCollection = new JSONObject(rawSet);
                    Iterator<String> setIterator = setCollection.keys();
                    while (setIterator.hasNext()) {
                        String cardName = setIterator.next();
                        JSONObject card = setCollection.getJSONObject(cardName);
                        String quantity;
                        String foil;
                        if (card.has("Quantity")) {
                            quantity = card.getString("Quantity");
                            foil = "";
                            data.add(new String[]{quantity, cardName, setName, "Near Mint", "English", foil});
                        }
                        if (card.has("QuantityFoil")) {
                            quantity = card.getString("QuantityFoil");
                            foil = "Foil";
                            data.add(new String[]{quantity, cardName, setName, "Near Mint", "English", foil});
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            writer.writeAll(data);
            writer.close();
            Snackbar snackbar = Snackbar.make(((Activity)activityContext).findViewById(android.R.id.content), "Collection exported to MagicCollectionDeckbox.csv", Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromDeckboxCsv() {
        new ImportFromDeckboxCsv().execute();
    }

    private class ImportFromDeckboxCsv extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog progressDialog;
        List<String[]> data;
        int importCount = 0;

        @Override
        protected void onPreExecute() {
            editor.clear();
            editor.apply();

            String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "MagicCollectionDeckbox.csv";
            String filePath = baseDir + File.separator + fileName;
            CSVReader reader;

            try {
                reader = new CSVReader(new FileReader(filePath));

                data = reader.readAll();

            } catch (IOException e) {
                e.printStackTrace();
            }

            progressDialog = new ProgressDialog(activityContext);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(data.size() - 1);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Importing Collection....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject collection;
            JSONObject set;
            JSONObject card;

            try {
                collection = new JSONObject();

                for (int i=1;i<data.size();i++) {
                    String[] cardEntry = data.get(i);
                    String setName = cardEntry[2];
                    String cardName = cardEntry[1];
                    if (collection.has(setName)) {
                        set = collection.getJSONObject(setName);
                    } else {
                        set = new JSONObject();
                    }
                    if (set.has(cardName)) {
                        card = set.getJSONObject(cardName);
                    } else {
                        card = new JSONObject();
                    }
                    if (cardEntry[5].equals("Foil")) {
                        card.put("QuantityFoil", Integer.parseInt(cardEntry[0]));
                    } else {
                        card.put("Quantity", Integer.parseInt(cardEntry[0]));
                    }
                    set.put(cardName, card);
                    collection.put(setName, set);
                    publishProgress(i);
                    importCount += Integer.parseInt(cardEntry[0]);
                }
                Iterator<String> collectionIterator = collection.keys();
                while (collectionIterator.hasNext()) {
                    String setName = collectionIterator.next();
                    JSONObject setCollection = collection.getJSONObject(setName);
                    editor.putString(setName, setCollection.toString());
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            Snackbar snackbar = Snackbar.make(((Activity)activityContext).findViewById(android.R.id.content), "Imported "+importCount+" cards", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }
    }

    public void clearCollection() {
        editor.clear();
        editor.apply();
        Snackbar snackbar = Snackbar.make(((Activity)activityContext).findViewById(android.R.id.content), "Collection cleared", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
