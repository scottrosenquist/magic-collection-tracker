package com.scottrosenquist.magiccollectiontracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class Sets extends AppCompatActivity {
    private CollectionHelper collectionHelper;
    private JSONObject sets = new JSONObject();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SubSetsPagerAdapter subSetsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectionHelper = new CollectionHelper(getApplicationContext(), Sets.this);
        setContentView(R.layout.sets_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        subSetsPagerAdapter = new SubSetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(subSetsPagerAdapter);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        new LoadAllSets().execute();
    }

    private class LoadAllSets extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Sets.this);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(188);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading Sets....");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStream is = getResources().openRawResource(R.raw.all_sets_trimmed);
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(is,"UTF-8"));
                reader.beginObject();
                int loadedSets=0;
                while (reader.hasNext()) {
                    String setName = reader.nextName();
                    JSONObject set = new JSONObject(gson.fromJson(reader, JsonObject.class).toString());
                    String type = set.getString("type");
                    JSONObject subSet = new JSONObject();
                    if (sets.has(type)) {
                        subSet = sets.getJSONObject(type);
                    }
                    subSet.put(setName, set);
                    sets.put(type,subSet);
                    loadedSets++;
                    publishProgress(loadedSets);
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
            Iterator<String> setsIterator = sets.keys();
            while (setsIterator.hasNext()) {
                String subSetsType = setsIterator.next();

                try {
                    subSetsPagerAdapter.addFragment(subSetsType, sets.getJSONObject(subSetsType));
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            tabLayout.setupWithViewPager(viewPager);
            progressDialog.dismiss();
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.export_to_deckbox_csv) {
            collectionHelper.exportToDeckboxCsv();
        } else if (id == R.id.import_from_deckbox_csv) {
            collectionHelper.importFromDeckboxCsv();
        } else if (id == R.id.clear_collection) {
            collectionHelper.clearCollection();
        }

        return super.onOptionsItemSelected(item);
    }
}
