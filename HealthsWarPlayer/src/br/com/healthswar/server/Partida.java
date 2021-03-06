package br.com.healthswar.server;

import java.io.IOException;
import java.util.Arrays;

import br.com.healthswar.comunication.MatchRequest;
import br.com.healthswar.comunication.MatchResponse;
import br.com.healthswar.comunication.Request;
import br.com.healthswar.comunication.Response;
import br.com.healthswar.gameplay.Combatente;
import br.com.healthswar.gameplay.Game;
import br.com.healthswar.gameplay.Player;

public class Partida extends Thread {

	private final Request MATCH_TYPE;

	private Player[] players;
	private int playersConneted;
	private boolean completo;

	private Game game;

	public Partida(Request MATCH_TYPE) {
		this.MATCH_TYPE = MATCH_TYPE;
		this.playersConneted = 0;
		this.completo = false;

		switch (this.MATCH_TYPE) {
			case PLAY_A_SOLO_MATCH:
				this.players = new Player[1];
				break;
	
			case PLAY_A_DUO_MATCH:
				this.players = new Player[2];
				break;
				
			case PLAY_A_SQUAD_MATCH:
				break;
				
			default:
				break;
	
		}
	}

	/**
	 * Adiona players enquanto tiver espaco na partida
	 */
	public void addPlayer(Player player) {
		if (!completo) {
			this.players[playersConneted] = player;
			this.playersConneted++;
			if (playersConneted == players.length) {
				completo = true;
			}
		}
	}

	public boolean getCompleto() {
		return this.completo;
	}
	
	@Override
	public void run() {
		game = new Game(players);
		players = game.sortDeck();
		for (Player player : players) {
			try {
				player.out.writeObject(Response.MATCH_READY);
				player.out.writeInt(player.getHealthsPoint());
				player.out.writeObject(player.getDeck());
				player.out.writeObject(player.getHand());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		while (game.isAtivo()) {
			int vez = game.getTurno() % 2 == 0 ? 0 : 1;
			int outro = game.getTurno() % 2 != 0 ? 0 : 1;
			Player player = players[vez];
			
			try {
				player.out.writeObject(MatchResponse.YOUR_TURN);
				player.out.writeObject(game.getPhase());
				players[outro].out.writeObject(MatchResponse.OPPONENT_TURN);
				
				MatchRequest request = (MatchRequest) player.in.readObject();

				switch (request) {
					case DRAW_A_CARD:
						game.comprarCarta(player);
						break;

					case SEND_A_FIGHTER:
						game.enviarCombatente(player);
						break;
						
					case USE_AN_ITEM:
						game.usarItem(player);
						break;
						
					case ATACK_THE_OPONENT:
						game.atacar(new Combatente(), new Combatente());
						break;
					
					case END_THE_TURN:
						game.encerrarTurno(player);
						break;
						
					case GET_PHASE:
						break;
				}

			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public String toString() {
		return "Partida [MATCH_TYPE=" + MATCH_TYPE + ", players=" + Arrays.toString(players) + ", playersConneted="
				+ playersConneted + ", completo=" + completo + ", game=" + game + "]";
	}

}
