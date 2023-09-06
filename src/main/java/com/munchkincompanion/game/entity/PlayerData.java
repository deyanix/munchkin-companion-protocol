package com.munchkincompanion.game.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerData {
    public static PlayerData fromJSON(JSONObject object) throws JSONException {
        PlayerData data = new PlayerData();
        data.setName(object.getString("name"));
        data.setLevel(object.getInt("level"));
        data.setGear(object.getInt("gear"));
        data.setGender(PlayerGender.valueOf(object.getString("gender")));
        data.setGenderChanged(object.getBoolean("genderChanged"));
        return data;
    }

    private String name;
    private int level;
    private int gear;
    private PlayerGender gender;
    private boolean genderChanged;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public PlayerGender getGender() {
        return gender;
    }

    public void setGender(PlayerGender gender) {
        this.gender = gender;
    }

    public boolean isGenderChanged() {
        return genderChanged;
    }

    public void setGenderChanged(boolean genderChanged) {
        this.genderChanged = genderChanged;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("level", level);
        object.put("gear", gear);
        object.put("gender", gender.toString());
        object.put("genderChanged", genderChanged);
        return object;
    }
}
