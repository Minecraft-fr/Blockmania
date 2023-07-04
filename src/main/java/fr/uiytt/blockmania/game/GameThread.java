package fr.uiytt.blockmania.game;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.ConfigManager;
import fr.uiytt.blockmania.config.GameType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class GameThread extends BukkitRunnable {
  private final GameData gameData;
  private final ConfigManager config;

  private int seconds;
  private int roundTimeLeft;
  public GameThread(GameData gameData) {
    this.gameData = gameData;
    seconds = 0;
    config = Blockmania.getConfigManager();
    roundTimeLeft = config.getRoundType() == GameType.RoundType.NO_ROUND ? -1 : config.getRoundLength();
  }

  @Override
  public void run() {
    if(!gameData.isGameRunning()) {
      this.cancel();
      return;
    }

    seconds++;
    roundTimeLeft--;

    if(roundTimeLeft == 0) {
      roundTimeLeft = config.getRoundLength();
      gameData.finishRound();
    }

    GameScoreboard.updateGlobalTimer(seconds);
    GameScoreboard.updatRoundTimer(roundTimeLeft);

    //Check if all players finished their block
    if(config.getRoundType() == GameType.RoundType.NORMAL || config.getRoundType() == GameType.RoundType.CYCLE) {
      HashMap<UUID, GamePlayer> playersData = gameData.getPlayersData();
      boolean allFinished = true;
      for(Player player : gameData.getOnlinePlayers()) {
        if(!playersData.get(player.getUniqueId()).hasCompletedBlock()) {
          allFinished = false;
        }
      }
      if(allFinished && config.getRoundType() != GameType.RoundType.NO_ROUND) roundTimeLeft = 1;
    }

    if(config.getGameType() == GameType.OBJECTIVE_TIME && seconds >= config.getObjectiveTime()) {
      gameData.setNeedToStop(true);
    }
    if(gameData.isNeedToStop()) {
      Blockmania.getGameManager().stop();
    }
  }
}
