package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;

import java.util.ArrayList;
import java.util.List;

public abstract class GameController {
    private final List<Player> players = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public abstract void createPlayer(PlayerData player);

    public abstract void updatePlayer(Player player);

    public abstract void deletePlayer(int playerId);

    protected void createLocallyPlayer(Player player) {
        players.add(player);
    }

    protected void updateLocallyPlayer(Player player) {
        players.stream()
                .filter(p -> p.getId() == player.getId())
                .findFirst()
                .ifPresent(p -> p.adaptData(player));
    }

    protected void deleteLocallyPlayer(int playerId) {
        players.removeIf(p -> p.getId() == playerId);
    }
}
