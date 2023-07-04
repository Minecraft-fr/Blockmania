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
      completedBlock = true;
      GameTeam playerTeam = Blockmania.getGameManager().getGameData().getPlayersTeam().get(playerUUID);
      GameScoreboard playerScoreboard = GameScoreboard.getPlayersScoreboard().get(playerUUID);
      if(playerTeam == null) {
        score += Blockmania.getConfigManager().getPointsPerRound();
        playerScoreboard.updateTabScore();
      } else {
        playerTeam.setScore(playerTeam.getScore() + Blockmania.getConfigManager().getPointsPerRound());
        score = playerTeam.getScore();
      }
      playerScoreboard.updateScore(score);
      playerScoreboard.updateBloc(targetMaterial, true);
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
    for(Player anyPlayer : Bukkit.getOnlinePlayers()) {
      GameScoreboard anyPlayerScoreboard = GameScoreboard.getPlayersScoreboard().get(anyPlayer.getUniqueId());
      if(anyPlayerScoreboard != null) anyPlayerScoreboard.updateTabScore();
    }
  }

  public boolean hasCompletedBlock() {
    return completedBlock;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getRound() {
    return round;
  }

  public Material getTargetMaterial() {
    return targetMaterial;
  }
}
