package com.scottrosenquist.magiccollectiontracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardsInSet extends AppCompatActivity {
    private CollectionHelper collectionHelper;
    private JSONObject rawSetData;
    private List<CardObj> cards = new ArrayList<>();
    private RecyclerView cardsRecyclerView;
    private CardsInSetAdapter cardsAdapter;
    private RecyclerView.LayoutManager cardsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectionHelper = new CollectionHelper(getApplicationContext());
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        try {
            rawSetData = new JSONObject(getIntent().getStringExtra("set_data"));
            toolbar.setTitle(rawSetData.optString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

            cardsRecyclerView = (RecyclerView) findViewById(R.id.list);
            cardsRecyclerView.hasFixedSize();

            cardsLayoutManager = new LinearLayoutManager(this);
            cardsRecyclerView.setLayoutManager(cardsLayoutManager);

            cardsAdapter = new CardsInSetAdapter(getLayoutInflater(),rawSetData.optString("code"));
            new LoadAllCards(cardsAdapter).execute();
            cardsRecyclerView.setAdapter(cardsAdapter);
    }

    private class LoadAllCards extends AsyncTask<Void, Integer, Void> {
        private CardsInSetAdapter cardsInSetAdapter;
        private ProgressDialog progressDialog;

        public LoadAllCards(CardsInSetAdapter cardsInSetAdapter) {
            this.cardsInSetAdapter = cardsInSetAdapter;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CardsInSet.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(rawSetData.optInt("cards"));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading Cards....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String fileName = rawSetData.optString("code").toLowerCase();
                String[] alphanumeric = fileName.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
                switch (alphanumeric[0]) {
                    case "2":
                        fileName = "second"+alphanumeric[1];
                        break;
                    case "3":
                        fileName = "third"+alphanumeric[1];
                        break;
                    case "4":
                        fileName = "fourth"+alphanumeric[1];
                        break;
                    case "5":
                        fileName = "fifth"+alphanumeric[1];
                        break;
                    case "6":
                        fileName = "sixth"+alphanumeric[1];
                        break;
                    case "7":
                        fileName = "seventh"+alphanumeric[1];
                        break;
                    case "8":
                        fileName = "eight"+alphanumeric[1];
                        break;
                    case "9":
                        fileName = "ninth"+alphanumeric[1];
                        break;
                    case "10":
                        fileName = "tenth"+alphanumeric[1];
                        break;
                }
                InputStream is = getResources().openRawResource(getResources().getIdentifier(fileName, "raw", getPackageName()));
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(is,"UTF-8"));
                reader.beginObject();
                int loadedCards=0;
                while (reader.hasNext()) {
                    if(reader.nextName().equals("cards")) {
                        reader.beginArray();
                        while (reader.hasNext()) {
                            cards.add(new CardObj(new JSONObject(gson.fromJson(reader, JsonObject.class).toString())));
                            loadedCards++;
                            publishProgress(loadedCards);
                        }
                        reader.endArray();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            cardsInSetAdapter.addAllCards(cards);
            progressDialog.dismiss();
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sets, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
