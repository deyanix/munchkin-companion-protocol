package com.munchkincompanion.game.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Player extends PlayerData {
    public static Player fromJSON(JSONObject object) throws JSONException {
        PlayerData data = PlayerData.fromJSON(object);
        return new Player(object.getInt("id"), data);
    }

    private final int id;

    public Player(int id) {
        this.id = id;
    }

    public Player(int id, PlayerData data) {
        this(id);
        adaptData(data);
    }

    public void adaptData(PlayerData data) {
        setName(data.getName());
        setLevel(data.getLevel());
        setGear(data.getGear());
        setGender(data.getGender());
        setGenderChanged(data.isGenderChanged());
    }

    public int getId() {
        return id;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = super.toJSON();
        object.put("id", id);
        return object;
    }
}
