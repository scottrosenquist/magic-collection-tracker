package com.scottrosenquist.magiccollectiontracker;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONObject;

public class SetObj {
    private String name;
    private String code;
    private String type;
    private JSONObject rawSetData;

    public SetObj(JSONObject rawSetData) {
        this.rawSetData = rawSetData;
        name = rawSetData.optString("name");
        code = rawSetData.optString("code");
        type = rawSetData.optString("type");
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public JSONObject getRawSetData() {
        return rawSetData;
    }

    @Override
    public String toString() {
        return getName();
    }
}
