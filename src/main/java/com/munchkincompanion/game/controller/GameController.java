package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class GameController {
    private final List<Player> players = new ArrayList<>();
    private final List<Consumer<List<Player>>> consumers = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public void addUpdateListener(Consumer<List<Player>> consumer) {
        consumers.add(consumer);
    }

    public void removeUpdateListener(Consumer<List<Player>> consumer) {
        consumers.remove(consumer);
    }

    public abstract void createPlayer(PlayerData player);

    public abstract void updatePlayer(Player player);

    public abstract void deletePlayer(int playerId);

    protected void replacePlayers(Collection<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        emitUpdate();
    }

    protected void appendLocallyPlayer(Player player) {
        players.add(player);
        emitUpdate();
    }

    protected void updateLocallyPlayer(Player player) {
        players.stream()
                .filter(p -> p.getId() == player.getId())
                .findFirst()
                .ifPresent(p -> p.adaptData(player));
        emitUpdate();
    }

    protected void deleteLocallyPlayer(int playerId) {
        players.removeIf(p -> p.getId() == playerId);
        emitUpdate();
    }

    protected void emitUpdate() {
        consumers.forEach(consumer -> consumer.accept(players));
    }
}
