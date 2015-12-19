package com.scottrosenquist.magiccollectiontracker;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scottrosenquist.magiccollectiontracker.R;

import java.util.ArrayList;
import java.util.List;

public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.ViewHolder> {
    private List<SetObj> setsDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView setsTextView;
        public ViewHolder(TextView v) {
            super(v);
            setsTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SetsAdapter() {
        setsDataset = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.set_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
//        ...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setsTextView.setText(setsDataset.get(position).getName());
        holder.setsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CardsInSet.class);
                intent.putExtra("set_data", setsDataset.get(position).getRawSetData().toString());
                v.getContext().startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return setsDataset.size();
    }

    public void addAllSets(List<SetObj> sets) {
        setsDataset.addAll(sets);
    }
}