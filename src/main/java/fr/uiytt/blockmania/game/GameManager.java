package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.utils.PlayerFromUUIDNotFoundException;
import fr.uiytt.blockmania.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {
  private final GameData gameData;
  private final GameThread gameThread;

  public GameManager() {
    gameData = new GameData();
    gameThread = new GameThread(gameData);
  }

  @SuppressWarnings("ConstantConditions")
  public void start() {
    gameData.initCategories(Blockmania.getConfigManager().getBlockCategories());
    gameData.setGameRunning(true);

    for(Player player : Bukkit.getOnlinePlayers()) {
      if(player.getGameMode() == GameMode.SPECTATOR) continue;
      GameScoreboard playerScoreboard = new GameScoreboard(player.getUniqueId());
      playerScoreboard.createScoreboard(player);
      gameData.addPlayer(player.getUniqueId());
      gameData.setWorld(player.getWorld());

      playerScoreboard.updateTabScore();
      GameTeam gameTeam = gameData.getPlayersTeam().get(player.getUniqueId());
      if(Blockmania.getConfigManager().getTeamSize() != 1 && gameTeam == null) {
        try {
          teamsWithLessPlayers().get(0).addPlayer(player.getUniqueId());
        } catch (PlayerFromUUIDNotFoundException e) {
          Blockmania.getPluginLogger().severe(e.getMessage());
        }
      }

      player.setGameMode(GameMode.SURVIVAL);
      player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0d);
      player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
      player.setExp(0);
      player.getInventory().clear();
      player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));
    }

    for (World world : Bukkit.getWorlds()) {
      world.setGameRule(GameRule.KEEP_INVENTORY, true);
      world.setTime(8000);
    }
    teleportPlayers(gameData.getOnlinePlayers());
    gameThread.runTaskTimer(Blockmania.getInstance(), 100L, 20L);

    Block zerozero = gameData.getWorld().getBlockAt(Utils.highestBlock(new Location(gameData.getWorld(), 0, 256, 0)));
    for(int x=-2; x<=2; x++) {
      for(int z=-2; z<=2; z++) { {
        zerozero.getRelative(x, 0, z).setType(x > -2 && x < 2 && z > -2 && z < 2 ? Material.END_PORTAL : Material.END_PORTAL_FRAME);
      }}
    }
  }

  public void stop() {
    gameData.setGameRunning(false);
    for(Player player : Bukkit.getOnlinePlayers()) {
      player.setPlayerListName(player.getDisplayName());
    }
    Set<Player> winners = findWinner();
    if(winners.size() == 1) {
      Bukkit.broadcastMessage(Language.GAME_VICTORY.getMessage().replace("%s%", ((Player)winners.toArray()[0]).getDisplayName()));
    } else {
      StringBuilder playersText = new StringBuilder();
      for(Player player : winners) {
        GameTeam playerTeam = gameData.getPlayersTeam().get(player.getUniqueId());
        playersText.append(playerTeam == null ? "" : playerTeam.getColor().getChat()).append(player.getDisplayName()).append(", ");
      }
      playersText.delete(playersText.length()-2, playersText.length());
      Bukkit.broadcastMessage(Language.GAME_VICTORY_PLURAL.getMessage().replace("%s%", playersText));

    }
    new BukkitRunnable() {
      private int i = 0;
      @Override
      public void run() {
        winners.forEach(winner -> {
          winner.getWorld().spawn(winner.getLocation(), Firework.class);
          i += 1;
          if(i == 10) {
            this.cancel();
          }
        });
      }
    }.runTaskTimer(Blockmania.getInstance(), 1, 5);

    Blockmania.registerNewGameManager();

  }

  private Set<Player> findWinner() {
    int highestScore = -1;
    Set<Player> winners = new HashSet<>();

    for(Map.Entry<UUID, GamePlayer> gamePlayerEntry : gameData.getPlayersData().entrySet()) {
      if(gamePlayerEntry.getValue().getScore() > highestScore) {
        winners.clear();
        highestScore = gamePlayerEntry.getValue().getScore();
      }
      if(gamePlayerEntry.getValue().getScore() >= highestScore) {
        Player player = Bukkit.getPlayer(gamePlayerEntry.getKey());
        if(player != null) {
          winners.add(player);
        }
      }
    }
    return winners;
  }

  private void teleportPlayers(List<Player> players) {
    Bukkit.broadcastMessage(Language.GAME_TELEPORTING.getMessage());
    if(Blockmania.getConfigManager().getTeamSize() == 1 || !Blockmania.getConfigManager().isSpawnTogetherEnabled()) {
      new BukkitRunnable() {
        @Override
        public void run() {
          for(int i=0;i<players.size();i++) {
            Player player = players.get(i);
            Location loc = Utils.randomLocation(gameData.getWorld());
            Bukkit.broadcastMessage(Language.GAME_TELEPORTING_PLAYER.getMessage().replace("%s%", String.valueOf(i + 1)).replace("%s2%", String.valueOf(players.size())));
            new BukkitRunnable() {

              @Override
              public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 4, true));
                player.teleport(loc);
              }
            }.runTask(Blockmania.getInstance());
            try {
              Thread.sleep(250);
            } catch (InterruptedException e) {
              Blockmania.getPluginLogger().severe(e.getMessage());
            }
          }
          if(Blockmania.getConfigManager().isPvpEnabled()) {
            Bukkit.broadcastMessage(Language.GAME_PVP_ON.getMessage());
          }
          if (Blockmania.getConfigManager().isKeepInventory()) {
            Bukkit.broadcastMessage(Language.GAME_KEEP_INVENTORY_ON.getMessage());
          }
        }
      }.runTaskAsynchronously(Blockmania.getInstance());
    } else {
      List<GameTeam> teams = gameData.getGameTeams();
      new BukkitRunnable() {
        @Override
        public void run() {
          for (int i = 0; i < teams.size(); i++) {
            Location loc = Utils.randomLocation(gameData.getWorld());
            GameTeam team = teams.get(i);
            Bukkit.broadcastMessage(Language.GAME_TELEPORTING_TEAM.getMessage().replace("%s%", String.valueOf(i + 1)).replace("%s2%", String.valueOf(teams.size())));
            new BukkitRunnable() {

              @Override
              public void run() {
                for (UUID playerUUID : team.getPlayersUUIDs()) {
                  Player player = Bukkit.getPlayer(playerUUID);
                  try {
                    if (player == null) {
                      throw new PlayerFromUUIDNotFoundException(playerUUID);
                    }
                  } catch (PlayerFromUUIDNotFoundException e) {
                    Blockmania.getPluginLogger().severe(e.getMessage());
                    team.removePlayer(playerUUID);
                    continue;
                  }
                  player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 4, true, false));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 30 * 20, 4, true, false));
                  player.teleport(loc);
                }
              }
            }.runTask(Blockmania.getInstance());
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              Blockmania.getPluginLogger().severe(e.getMessage());
            }
          }
        }
      }.runTaskAsynchronously(Blockmania.getInstance());
    }

  }

  private List<GameTeam> teamsWithLessPlayers() {
    int size = Integer.MAX_VALUE;
    List<GameTeam> result = new ArrayList<>();
    for (GameTeam team : gameData.getGameTeams()) {
      if(team.getPlayersUUIDs().size() <= size) {
        if(team.getPlayersUUIDs().size() < size) {
          result.clear();
          size = team.getPlayersUUIDs().size();
        }
        result.add(team);
      }
    }
    Collections.shuffle(result);
    return result;
  }

  public GameData getGameData() {
    return gameData;
  }
}
