package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;
import com.munchkincompanion.game.exception.GameException;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpMessengerReceiver;
import com.recadel.sjp.messenger.SjpServerMessenger;
import com.recadel.sjp.messenger.SjpServerMessengerListener;

import org.json.JSONException;

public class HostGameController extends LocalGameController {
    private SjpServerMessenger serverMessenger;

    public HostGameController(SjpServerMessenger messenger) {
        messenger.addListener(new HostGameServerMessengerListener());
    }

    @Override
    public void createPlayer(PlayerData player) {
        try {
            serverMessenger.broadcast("players/create", player.toJSON());
        } catch (JSONException e) {
            throw new GameException("Error creating player", e);
        }
    }

    @Override
    public void updatePlayer(Player player) {
        try {
            serverMessenger.broadcast("players/update", player.toJSON());
        } catch (JSONException e) {
            throw new GameException(e);
        }

    }

    @Override
    public void deletePlayer(int playerId) {
        serverMessenger.broadcast("players/delete", playerId);
    }

    class HostGameServerMessengerListener implements SjpServerMessengerListener {
        @Override
        public void onConnect(SjpMessenger messenger) {
            messenger.addReceiver(new HostGameMessengerReceiver(messenger));
        }

        @Override
        public void onClose() {
        }

        @Override
        public void onError(Throwable throwable) {
        }
    }

    class HostGameMessengerReceiver implements SjpMessengerReceiver {
        final SjpMessenger messenger;

        HostGameMessengerReceiver(SjpMessenger messenger) {
            this.messenger = messenger;
        }

        @Override
        public void onEvent(String action, Object data) {
//            switch (action) {
//                case "player/create":
//                    createLocallyPlayer(data);
//                    serverMessenger.broadcast("player/create", data);
//                    break;
//                case "player/update":
//                    break;
//                case "player/delete":
//
//            }
        }

        @Override
        public void onRequest(String action, Object data) {

        }
    }
}
