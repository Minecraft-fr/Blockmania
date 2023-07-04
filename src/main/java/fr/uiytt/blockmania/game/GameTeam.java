package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.utils.ColorLink;
import fr.uiytt.blockmania.utils.PlayerFromUUIDNotFoundException;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameTeam {

	private final List<UUID> playersUUIDs = new ArrayList<>();

	private final ColorLink color;
	private final String name;
	private final String suffix;

	private int score = 0;

	/**
	 * A team of players
	 * Teams already exist before the game start, to allow players to join.
	 * @param color {@link ColorLink} of the team
	 * @param name Name of the team
	 *
	 * @see GameData for all the instances of the teams
	 */
	public GameTeam(ColorLink color, String name, int number) {
		this.color = color;
		if(number != 1) {
			this.name = name + " [" + number + "]";
			this.suffix = " [" + number + "]";
		} else {
			this.name = name;
			this.suffix = "";
		}
	}


	/**
	 * This return only a COPY of the list of players, you cannot modify the players here,
	 * @see #addPlayer(UUID)
	 * @see #removePlayer(UUID)
	 */
	public List<UUID> getPlayersUUIDs() {
		return List.copyOf(playersUUIDs);
	}
	
	/**
	 * Add a player to the team
	 * @param playerUUID if of the player, produce an error if the player is not online
	 */
	public void addPlayer(UUID playerUUID) throws PlayerFromUUIDNotFoundException {
		Player player = Bukkit.getPlayer(playerUUID);
		if(player == null) {
			throw new PlayerFromUUIDNotFoundException(playerUUID);
		}
		addTeamToTab(player);
		player.setPlayerListFooter("Plugin de uiytt, Max et Kosmyc_");
		playersUUIDs.add(playerUUID);
		Blockmania.getGameManager().getGameData().getPlayersTeam().put(playerUUID, this);
	}

	public void addTeamToTab(Player player) {
		player.setPlayerListName(color.getTabColor() + player.getDisplayName() + suffix);
	}

	public static void removePlayerFromTab(Player player) {
		player.setPlayerListName(player.getDisplayName());
	}

	public void removePlayer(UUID playerUUID) {
		playersUUIDs.remove(playerUUID);
		Blockmania.getGameManager().getGameData().getPlayersTeam().remove(playerUUID);
		Player player = Bukkit.getPlayer(playerUUID);
		if(player != null) {
			player.setPlayerListName(ChatColor.WHITE + player.getDisplayName());
		}
	}

	public static void removePlayerFromAllTeams(UUID playerUUID) {
		GameTeam team = Blockmania.getGameManager().getGameData().getPlayersTeam().get(playerUUID);
		if(team != null) {
			team.removePlayer(playerUUID);
		}
	}

	/**
	 * Remove all players from this team.
	 */
	public void removeAllPlayers() {
		Blockmania.getGameManager().getGameData().getPlayersTeam().clear();
		playersUUIDs.clear();
		Bukkit.getOnlinePlayers().forEach(GameTeam::removePlayerFromTab);
	}
	
	/**
	 * This register new teams depending on the number of player, the size of the teams etc...
	 */
	public static void reorganizeTeam() {
		GameData gameData = Blockmania.getGameManager().getGameData();
		if(Blockmania.getConfigManager().getTeamSize() == 1) {
			gameData.getGameTeams().forEach(GameTeam::removeAllPlayers);
			gameData.getGameTeams().clear();
			return;
		}
		List<ColorLink> colors = Arrays.asList(ColorLink.values());

		//Create teams with color, if more than one team for a color, add number
		// ex : RED,BlUE,YELLOW, etc.. RED[1],BLUE[1]
		int numberTeam = Math.max((int) Math.ceil((double) Bukkit.getOnlinePlayers().size() / (double) Blockmania.getConfigManager().getTeamSize()), 2);
		if(numberTeam == gameData.getGameTeams().size()) {
				return;
		}

		gameData.getGameTeams().forEach(GameTeam::removeAllPlayers);
		gameData.getGameTeams().clear();

		int colorindex = 0;
		int numberOfSameColor = 1;
		for(int i=0;i<numberTeam;i++ ) {
			ColorLink color = colors.get(colorindex);
			gameData.getGameTeams().add(new GameTeam(color, color.name(), numberOfSameColor));

			colorindex++;
			if(colorindex == ColorLink.values().length) {
				colorindex = 0;
				numberOfSameColor++;
			}
		}



	}

	public ColorLink getColor() {
		return color;
	}
	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
		getPlayersUUIDs().forEach(uuid -> {
			GameScoreboard.getPlayersScoreboard().get(uuid).updateTabScore();
			Blockmania.getGameManager().getGameData().getPlayersData().get(uuid).setScore(score);
		});
	}
}
