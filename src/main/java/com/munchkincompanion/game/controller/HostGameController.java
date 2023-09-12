package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;
import com.munchkincompanion.game.exception.GameException;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpMessengerReceiver;
import com.recadel.sjp.messenger.SjpServerMediator;
import com.recadel.sjp.messenger.SjpServerMessengerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HostGameController extends LocalGameController {
    private final SjpServerMediator mediator;

    public HostGameController(SjpServerMediator mediator) {
        this.mediator = mediator;
        mediator.addListener(new HostGameServerMessengerListener());
    }

    @Override
    public void createPlayer(PlayerData data) {
        Player player = createLocallyPlayer(data);
        try {
            mediator.broadcast("players/create", player.toJSON());
        } catch (JSONException e) {
            throw new GameException("Error creating player", e);
        }
    }

    @Override
    public void updatePlayer(Player player) {
        updateLocallyPlayer(player);
        try {
            mediator.broadcast("players/update", player.toJSON());
        } catch (JSONException e) {
            throw new GameException(e);
        }
    }

    @Override
    public void deletePlayer(int playerId) {
        deleteLocallyPlayer(playerId);
        mediator.broadcast("players/delete", playerId);
    }

    class HostGameServerMessengerListener implements SjpServerMessengerListener {
        @Override
        public void onConnect(SjpMessenger messenger) {
			System.out.println("[HOST] Connecting");
            messenger.addReceiver(new HostGameMessengerReceiver(messenger));
        }

        @Override
        public void onClose() {
			System.out.println("[HOST] Closing");
        }

        @Override
        public void onError(Throwable ex) {
			ex.printStackTrace();
        }
    }

    class HostGameMessengerReceiver implements SjpMessengerReceiver {
        final SjpMessenger messenger;

        HostGameMessengerReceiver(SjpMessenger messenger) {
            this.messenger = messenger;
        }

        @Override
        public void onEvent(String action, Object data) {
			switch (action) {
				case "players/create" -> {
					if (!(data instanceof JSONObject playerData)) {
						throw new GameException("Bad data format");
					}
					Player player = createLocallyPlayer(PlayerData.fromJSON(playerData));
					mediator.broadcast("players/create", player);
				}
				case "players/update" -> {
					if (!(data instanceof JSONObject player)) {
						throw new GameException("Bad data format");
					}
					updateLocallyPlayer(Player.fromJSON(player));
					mediator.broadcast("players/update", player);
				}
				case "players/delete" -> {
					if (!(data instanceof Integer playerId)) {
						throw new GameException("Bad data format");
					}
					deleteLocallyPlayer(playerId);
					mediator.broadcast("players/delete", playerId);
				}
				case "players/get" -> {
					JSONArray array = new JSONArray(getPlayers().stream().map(Player::toJSON).toArray());
					messenger.emit("players/synchronize", array);
				}
			}
        }

        @Override
        public void onRequest(String action, Object data) {
        }

		@Override
		public void onError(Throwable ex) {
			ex.printStackTrace();
		}

		@Override
		public void onClose() {
			System.out.println("[HOST] Closed client");
		}
	}
}
