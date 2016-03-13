package com.scottrosenquist.magiccollectiontracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CardObj {
    private String name;
    private String number;
    private String id;
    private int multiverseid;
    private List<String> colours;
    private List<String> types;
    private JSONObject rawCardData;
    private int quantity;
    private int quantityFoil;

    public CardObj(JSONObject rawCardData) {
        this.rawCardData = rawCardData;
        name = rawCardData.optString("name");
        number = rawCardData.optString("number");
        id = rawCardData.optString("id");
        multiverseid = rawCardData.optInt("multiverseid");

        colours = new ArrayList<>();
        JSONArray rawColourData = rawCardData.optJSONArray("colors");
        if (rawColourData != null) {
            for (int i = 0; i < rawColourData.length(); i++) {
                colours.add(rawColourData.optString(i));
            }
        }

        types = new ArrayList<>();
        JSONArray rawTypeData = rawCardData.optJSONArray("types");
        if (rawTypeData != null) {
            for (int i = 0; i < rawTypeData.length(); i++) {
                types.add(rawTypeData.optString(i));
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public Integer getMultiverseid() {
        return multiverseid;
    }

    public List<String> getColours() {
        return colours;
    }

    public List<String> getTypes() {
        return types;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantityFoil() {
        return quantityFoil;
    }

    public void setQuantityFoil(int quantityFoil) {
        this.quantityFoil = quantityFoil;
    }

    public String cardDataDump() {
        return rawCardData.toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}
