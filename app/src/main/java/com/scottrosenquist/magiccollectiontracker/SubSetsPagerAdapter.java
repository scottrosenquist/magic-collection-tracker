package com.scottrosenquist.magiccollectiontracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubSetsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> subSetFragments = new ArrayList<>();
    private List<String> subSetFragmentTypes = new ArrayList<>();

    public SubSetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return subSetFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return subSetFragmentTypes.get(position);
    }

    @Override
    public int getCount() {
        return subSetFragments.size();
    }

    public void addFragment(String subSetsType, JSONObject subSetsData) {
        subSetFragmentTypes.add(subSetsType);
        subSetFragments.add(SubSets.newInstance(subSetsData));
        notifyDataSetChanged();
    }
}
