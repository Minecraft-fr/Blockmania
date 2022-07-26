package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.GameType;
import fr.uiytt.blockmania.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class GamePlayer {
  private final GameData gameData;
  private final UUID playerUUID;
  private int score;
  private int round;
  private boolean completedBlock;
  private Material targetMaterial;
  public GamePlayer(GameData gameData, UUID playerUUID) {
    this.gameData = gameData;
    this.playerUUID = playerUUID;
    this.score = 0;
    this.round = 0;
    this.completedBlock = false;
    targetMaterial = gameData.getBlockInCategory(round);
    GameScoreboard.getPlayersScoreboard().get(playerUUID).updateBloc(targetMaterial, false);
    GameScoreboard.getPlayersScoreboard().get(playerUUID).updateNextRound(0);
  }

  public void completedRound() {
    if(!completedBlock) {
      score += Blockmania.getConfigManager().getPointsPerRound();
      completedBlock = true;
      GameScoreboard.updateTabScore(Bukkit.getPlayer(playerUUID), score);
      GameScoreboard.getPlayersScoreboard().get(playerUUID).updateScore(score);
      GameScoreboard.getPlayersScoreboard().get(playerUUID).updateBloc(targetMaterial, true);
      Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).sendMessage(Language.GAME_COMPLETED_BLOCK.getMessage());

      if(Blockmania.getConfigManager().getRoundType() == GameType.RoundType.NO_ROUND) {
        new BukkitRunnable() {
          @Override
          public void run() {
            updateToNextRound();
          }
        }.runTaskLater(Blockmania.getInstance(), 40L);
      }
    }
  }
  public void updateToNextRound() {
    if(Blockmania.getConfigManager().getRoundType() != GameType.RoundType.CYCLE  || completedBlock) {
      round = (round + 1) % gameData.getNumberOfCategories();
      completedBlock = false;
      targetMaterial = gameData.getBlockInCategory(round);
      GameScoreboard.getPlayersScoreboard().get(playerUUID).updateNextRound(round != gameData.getNumberOfCategories() - 1 ? round : -1);
      GameScoreboard.getPlayersScoreboard().get(playerUUID).updateBloc(targetMaterial, false);
    } else {
      targetMaterial = gameData.getBlockInCategory(round);
      GameScoreboard.getPlayersScoreboard().get(playerUUID).updateBloc(targetMaterial, false);
    }
    if((score >= Blockmania.getConfigManager().getObjectivePoints() && Blockmania.getConfigManager().getGameType() == GameType.OBJECTIVE_POINTS)
            || (round >= Blockmania.getConfigManager().getObjectiveRound() && Blockmania.getConfigManager().getGameType() == GameType.OBJECTIVE_ROUNDS)) {
      Blockmania.getGameManager().getGameData().setNeedToStop(true);
    }

  }

  public void walkedOnBlock(Material material) {
    if(material == targetMaterial) {
      completedRound();
    }
  }

  public void updateScoreboardData() {
    GameScoreboard playerScoreboard = GameScoreboard.getPlayersScoreboard().get(playerUUID);
    playerScoreboard.updateBloc(targetMaterial, completedBlock);
    playerScoreboard.updateNextRound(round);
    playerScoreboard.updateScore(score);
    for(Player player : Bukkit.getOnlinePlayers()) {
      GameScoreboard.updateTabScore(player, score);
      Blockmania.getGameManager().getGameData().getPlayersTeam().get(player.getUniqueId()).addTeamToTab(player);
    }
  }

  public boolean hasCompletedBlock() {
    return completedBlock;
  }

  public int getScore() {
    return score;
  }

  public int getRound() {
    return round;
  }

  public Material getTargetMaterial() {
    return targetMaterial;
  }
}
