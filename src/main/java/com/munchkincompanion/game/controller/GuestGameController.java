package com.munchkincompanion.game.controller;

import com.munchkincompanion.game.entity.Player;
import com.munchkincompanion.game.entity.PlayerData;
import com.munchkincompanion.game.exception.GameException;
import com.recadel.sjp.messenger.SjpMessenger;
import com.recadel.sjp.messenger.SjpMessengerReceiver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.IntStream;

public class GuestGameController extends GameController {
	private final SjpMessenger messenger;

	public GuestGameController(SjpMessenger messenger) {
		this.messenger = messenger;
		messenger.addReceiver(new GuestGameReceiver());
	}

	public void synchronizePlayers() {
		messenger.emit("players/get");
	}

	@Override
	public void createPlayer(PlayerData data) {
		messenger.emit("players/create", data.toJSON());
	}

	@Override
	public void updatePlayer(Player player) {
		messenger.emit("players/update", player.toJSON());
	}

	@Override
	public void deletePlayer(int playerId) {
		messenger.emit("players/delete", playerId);
	}

	class GuestGameReceiver implements SjpMessengerReceiver {
		@Override
		public void onEvent(String action, Object data) {
			System.out.println("[RECEIVER] Get event " + action);
			switch (action) {
				case "players/create" -> {
					if (!(data instanceof JSONObject player)) {
						throw new GameException("Bad data format");
					}
					appendLocallyPlayer(Player.fromJSON(player));
				}
				case "players/update" -> {
					if (!(data instanceof JSONObject player)) {
						throw new GameException("Bad data format");
					}
					updateLocallyPlayer(Player.fromJSON(player));
				}
				case "players/delete" -> {
					if (!(data instanceof Integer playerId)) {
						throw new GameException("Bad data format");
					}
					deleteLocallyPlayer(playerId);
				}
				case "players/synchronize" -> {
					if (!(data instanceof JSONArray array)) {
						throw new GameException("Bad data format");
					}

					List<Player> players = IntStream.range(0, array.length())
							.mapToObj(array::getJSONObject)
							.map(Player::fromJSON)
							.toList();
					replacePlayers(players);
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
			System.out.println("[GUEST] Closed");
		}
	}
}
