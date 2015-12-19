package com.scottrosenquist.magiccollectiontracker;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;

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

public class Sets extends AppCompatActivity {
    private List<SetObj> sets = new ArrayList<>();
    private RecyclerView setsRecyclerView;
    private SetsAdapter setsAdapter;
    private RecyclerView.LayoutManager setsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("scottdebug", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        setsRecyclerView = (RecyclerView) findViewById(R.id.list);
        setsRecyclerView.hasFixedSize();

        setsLayoutManager = new LinearLayoutManager(this);
        setsRecyclerView.setLayoutManager(setsLayoutManager);

        setsAdapter = new SetsAdapter();
        new LoadAllSets(setsAdapter).execute();
        setsRecyclerView.setAdapter(setsAdapter);
    }

    private class LoadAllSets extends AsyncTask<Void, Integer, Void> {
        private SetsAdapter setsAdapter;
        private ProgressDialog progressDialog;

        public LoadAllSets(SetsAdapter setsAdapter) {
            Log.d("scottdebug","LoadAllSets()");
            this.setsAdapter = setsAdapter;
        }

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
                    reader.nextName();
                    sets.add(new SetObj(new JSONObject(gson.fromJson(reader, JsonObject.class).toString())));
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
            Log.d("scottdebug","sets: "+sets);
            setsAdapter.addAllSets(sets);
            progressDialog.dismiss();
        }

        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }
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
