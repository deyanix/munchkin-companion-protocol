package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;

public class LocalGameController extends GameController {
    private int nextPlayerId = 1;

    @Override
    public void createPlayer(PlayerData data) {
        createLocallyPlayer(data);
    }

    @Override
    public void updatePlayer(Player player) {
        updateLocallyPlayer(player);
    }

    @Override
    public void deletePlayer(int playerId) {
        deleteLocallyPlayer(playerId);
    }

    protected Player createLocallyPlayer(PlayerData data) {
        Player player = new Player(nextPlayerId++, data);
        appendLocallyPlayer(player);
        return player;
    }
}
