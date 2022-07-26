package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.BlockCategory;
import fr.uiytt.blockmania.Blockmania;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GameData {
  private final HashMap<UUID, GamePlayer> playersData = new HashMap<>();
  private final HashMap<UUID, GameTeam> playersTeam = new HashMap<>();
  private final List<GameTeam> gameTeams = new ArrayList<>();
  private final LinkedList<BlockCategory> orderedCategories = new LinkedList<>();
  private boolean gameRunning = false;
  private boolean needToStop = false;
  private World world;

  public void addPlayer(UUID playerUUID) {
    playersData.put(playerUUID, new GamePlayer(this, playerUUID));
  }

  public void initCategories(List<BlockCategory> blockCategories) {
    orderedCategories.addAll(blockCategories);
    orderedCategories.sort(Comparator.comparingInt(BlockCategory::getDifficulty));
    if(Blockmania.getConfigManager().isSameBlockEnabled()) {
      orderedCategories.forEach(BlockCategory::reduceToOne);
    }
  }

  public List<Player> getOnlinePlayers() {
    List<Player> players = new ArrayList<>();
    for(UUID playerUUID : playersData.keySet()) {
      Player player = Bukkit.getPlayer(playerUUID);
      if(player != null) {
        players.add(player);
      }
    }
    return players;
  }

  public Material getBlockInCategory(int categoryIndex) {
    if(categoryIndex >= orderedCategories.size()) {
      categoryIndex = orderedCategories.size() - 1;
    }
    return orderedCategories.get(categoryIndex).getRandomBlock();
  }

  public void finishRound() {
    playersData.forEach((uuid, gamePlayer) -> gamePlayer.updateToNextRound());
  }
  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }

  public String getCatoryName(int index) {
    if(index >= orderedCategories.size()) return "Aucun";
    return orderedCategories.get(index).getName();
  }

  public void setGameRunning(boolean gameRunning) {
    this.gameRunning = gameRunning;
  }

  public boolean isGameRunning() {
    return gameRunning;
  }

  public HashMap<UUID, GamePlayer> getPlayersData() {
    return new HashMap<>(playersData);
  }

  public void setNeedToStop(boolean needToStop) {
    this.needToStop = needToStop;
  }

  public boolean isNeedToStop() {
    return needToStop;
  }

  public HashMap<UUID, GameTeam> getPlayersTeam() {
    return playersTeam;
  }

  public List<GameTeam> getGameTeams() {
    return gameTeams;
  }

  public int getNumberOfCategories() { return orderedCategories.size(); }
}
