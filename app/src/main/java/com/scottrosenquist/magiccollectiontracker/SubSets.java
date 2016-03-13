package com.scottrosenquist.magiccollectiontracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

public class SubSets extends Fragment {
    private JSONObject rawSetsData;
    private RecyclerView setsRecyclerView;
    private SetsAdapter setsAdapter;
    private RecyclerView.LayoutManager setsLayoutManager;

    public static SubSets newInstance(JSONObject subSetsData) {
        SubSets subSets = new SubSets();

        Bundle args = new Bundle();
        args.putString("sub_sets_data", subSetsData.toString());
        subSets.setArguments(args);

        return subSets;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.sub_sets_list, container, false);

        try {
            rawSetsData = new JSONObject(getArguments().getString("sub_sets_data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setsRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        setsRecyclerView.hasFixedSize();

        setsLayoutManager = new LinearLayoutManager(getActivity());
        setsRecyclerView.setLayoutManager(setsLayoutManager);

        setsAdapter = new SetsAdapter(rawSetsData);
        setsRecyclerView.setAdapter(setsAdapter);

        return rootView;
    }
}
