package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.GameType;
import fr.uiytt.blockmania.config.Language;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameScoreboard {

	private static final HashMap<UUID, GameScoreboard> playersScoreboard = new HashMap<>();
	private final Scoreboard scoreboard;
	private final List<Score> scorelist = new ArrayList<>();

	private final UUID playerUUID;

	/**
	 * A scoreboard for the game for each player
	 */
	public GameScoreboard(UUID playerUUID) {
		this.playerUUID = playerUUID;
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		scoreboard = Objects.requireNonNull(scoreboardManager).getNewScoreboard();

		playersScoreboard.put(playerUUID, this); //Store the scoreboard instance
	}

	//Based on https://www.spigotmc.org/wiki/making-scoreboard-with-teams-no-flicker/ 
	public void createScoreboard(Player player) {

		final Objective tabScore = scoreboard.registerNewObjective("BSHUF_tabscore","none","BSHUF_tabscore");
		tabScore.setRenderType(RenderType.INTEGER);
		tabScore.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		//Title
		Objective obj = scoreboard.registerNewObjective("title","dummy",ChatColor.YELLOW + "" + ChatColor.BOLD + "❂ Blockmania ❂");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);


		scorelist.add(obj.getScore(ChatColor.MAGIC + "" + ChatColor.GRAY));
		//Timer
		Team timeCounter = scoreboard.registerNewTeam("BSHUF_Timer");
		timeCounter.addEntry(ChatColor.AQUA + "");
		timeCounter.setPrefix(Language.SCOREBOARD_TIME.getMessage().replace("%s%", "00:00"));
		scorelist.add(obj.getScore(ChatColor.AQUA + ""));

		//Score
		Team scoreAmount = scoreboard.registerNewTeam("BSHUF_Score");
		scoreAmount.addEntry(ChatColor.DARK_GRAY + "");
		scoreAmount.setPrefix(Language.SCOREBOARD_POINTS.getMessage().replace("%s%", "0"));
		scorelist.add(obj.getScore(ChatColor.DARK_GRAY + ""));

		//Round time
		if(Blockmania.getConfigManager().getRoundType() != GameType.RoundType.NO_ROUND) {
			Team roundCounter = scoreboard.registerNewTeam("BSHUF_Round");
			roundCounter.addEntry(ChatColor.BLACK + "");
			roundCounter.setPrefix(Language.SCOREBOARD_TIME.getMessage().replace("%s%", "00:00"));
			scorelist.add(obj.getScore(ChatColor.BLACK + ""));
		}

		scorelist.add(obj.getScore(ChatColor.MAGIC + "" + ChatColor.BLUE));
		scorelist.add(obj.getScore(Language.SCOREBOARD_OBJECTIVES.getMessage()));
		//Game Type
		String textObjective;
		switch (Blockmania.getConfigManager().getGameType()) {
			case OBJECTIVE_TIME -> {
				textObjective = Language.SCOREBOARD_OBJECTIVE_TIME.getMessage().replace("%s%", String.valueOf(Math.round((double) Blockmania.getConfigManager().getObjectiveTime() / 60)));
			}
			case OBJECTIVE_ROUNDS -> {
				textObjective = Language.SCOREBOARD_OBJECTIVE_ROUNDS.getMessage().replace("%s%",String.valueOf(Blockmania.getConfigManager().getObjectiveRound()));
			}
			case OBJECTIVE_POINTS -> {
				textObjective = Language.SCOREBOARD_OBJECTIVE_POINTS.getMessage().replace("%s%",String.valueOf(Blockmania.getConfigManager().getObjectivePoints()));
			}
			default -> textObjective = "ERROR";

		}
		scorelist.add(obj.getScore(textObjective));

		//Block
		scorelist.add(obj.getScore(Language.SCOREBOARD_BLOCK_TITLE.getMessage()));
		Team blockObjectif = scoreboard.registerNewTeam("BSHUF_Block");
		blockObjectif.addEntry(ChatColor.DARK_GREEN + "");
		blockObjectif.setPrefix(Language.SCOREBOARD_BLOCK.getMessage().replace("%s%",""));
		scorelist.add(obj.getScore(ChatColor.DARK_GREEN + ""));
		//Next round
		scorelist.add(obj.getScore(Language.SCOREBOARD_NEXT_ROUND_TITLE.getMessage()));
		Team nextRound = scoreboard.registerNewTeam("BSHUF_NextRound");
		nextRound.addEntry(ChatColor.DARK_PURPLE + "");
		nextRound.setPrefix(Language.SCOREBOARD_NEXT_ROUND.getMessage().replace("%s%",""));
		scorelist.add(obj.getScore(ChatColor.DARK_PURPLE + ""));

		scorelist.add(obj.getScore(ChatColor.DARK_AQUA + ""));

		Team minecraftfr = scoreboard.registerNewTeam("BSHUF_Minefr");
		minecraftfr.addEntry(ChatColor.UNDERLINE + "");
		minecraftfr.setPrefix("      \u00A7x\u00A74\u00A71\u00A7f\u00A7b\u00A73\u00A7dM\u00A7x\u00A76\u00A71\u00A7f\u00A7b\u00A73\u00A7fi\u00A7x\u00A78\u00A70\u00A7f\u00A7c\u00A74\u00A71n\u00A7x\u00A7a\u00A70\u00A7f\u00A7c\u00A74\u00A73e\u00A7x\u00A7c\u00A70\u00A7f\u00A7c\u00A74\u00A75c\u00A7x\u00A7d\u00A7f\u00A7f\u00A7d\u00A74\u00A77r\u00A7x\u00A7e\u00A7c\u00A7f\u00A78\u00A74\u00A7aa\u00A7x\u00A7e\u00A76\u00A7e\u00A7d\u00A74\u00A7df\u00A7x\u00A7e\u00A70\u00A7e\u00A72\u00A75\u00A70t\u00A7x\u00A7d\u00A7a\u00A7d\u00A77\u00A75\u00A73.\u00A7x\u00A7d\u00A74\u00A7c\u00A7d\u00A75\u00A76f\u00A7x\u00A7c\u00A7e\u00A7c\u00A72\u00A75\u00A79r");
		scorelist.add(obj.getScore(ChatColor.UNDERLINE + ""));

		int n = scorelist.size(); //Set the number of each line of the scoreboard
		for (Score score : scorelist) {
			score.setScore(n);
			n--;
		}

		player.setScoreboard(scoreboard);
	}

	public static void updateGlobalTimer(int time) {
		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			Team timeCounter = gameScoreboard.scoreboard.getTeam("BSHUF_Timer");
			if(timeCounter == null) {
				return;
			}
			timeCounter.setPrefix(Language.SCOREBOARD_TIME.getMessage().replace("%s%",  intToTime(time)));
		});
	}

	public static void updatRoundTimer(int time) {
		if(time < 0) return;
		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			Team roundCounter = gameScoreboard.scoreboard.getTeam("BSHUF_Round");
			if(roundCounter == null) {
				return;
			}
			roundCounter.setPrefix(Language.SCOREBOARD_TIME.getMessage().replace("%s%", intToTime(time)) );
		});
	}

	public void updateScore(int newScore) {
		Team scoreCounter = scoreboard.getTeam("BSHUF_Score");
		if(scoreCounter == null) return;
		scoreCounter.setPrefix(Language.SCOREBOARD_POINTS.getMessage().replace("%s%", String.valueOf(newScore)));
	}

	public void updateBloc(Material block, boolean completed) {
		Team scoreCounter = scoreboard.getTeam("BSHUF_Block");
		if(scoreCounter == null) return;
		String blockText = "";
		if(completed) blockText += ChatColor.STRIKETHROUGH;
		blockText += block.name();
		scoreCounter.setPrefix(Language.SCOREBOARD_BLOCK.getMessage().replace("%s%", blockText));
	}

	/**
	 * Update the tab score of this player for all the players
	 */
	public void updateTabScore() {
		Player player = Bukkit.getPlayer(playerUUID);
		GameTeam playerTeam = Blockmania.getGameManager().getGameData().getPlayersTeam().get(playerUUID);
		GamePlayer gamePlayer = Blockmania.getGameManager().getGameData().getPlayersData().get(playerUUID);
		if(player == null || gamePlayer == null) return;

		playersScoreboard.forEach((uuid, gameScoreboard) -> {
			gameScoreboard.getScoreboard().getObjective("BSHUF_tabscore").getScore(player.getName()).setScore(playerTeam == null ? gamePlayer.getScore() : playerTeam.getScore());
		});
	}

	public void updateNextRound(int actualRound) {
		String nextRound = Blockmania.getGameManager().getGameData().getCatoryName(actualRound + 1);
		Team bshufNextRound = scoreboard.getTeam("BSHUF_NextRound");
		if(bshufNextRound == null) return;
		bshufNextRound.setPrefix(Language.SCOREBOARD_NEXT_ROUND.getMessage().replace("%s%", nextRound));
	}

	
	private static String intToTime(int time) {
		String hour = "";
		String min = "00";
		String sec = "";
		if(time > 59) {
			int temp;
			if(time > 3599) {
				temp = (int) Math.floor(time / (float)3600);
				hour = String.valueOf(temp);
				hour += ":";
				time -= temp * 3600;
			}
			temp = (int) Math.floor(time / (float)60);
			min = String.valueOf(temp);
			time -= temp * 60;
		}
		if(time < 10) sec = "0";
		sec += String.valueOf(time);
		return hour+min+":"+sec;
	}

	public static HashMap<UUID, GameScoreboard> getPlayersScoreboard() { return playersScoreboard; }

	public Scoreboard getScoreboard() {
		return scoreboard;
	}
}
