package fr.uiytt.blockmania.events;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.game.GamePlayer;
import fr.uiytt.blockmania.game.GameScoreboard;
import fr.uiytt.blockmania.game.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {

  @EventHandler
  public void onShift(PlayerToggleSneakEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning()) return;
    GamePlayer gamePlayer = Blockmania.getGameManager().getGameData().getPlayersData().get(event.getPlayer().getUniqueId());
    if(gamePlayer != null) {
      gamePlayer.walkedOnBlock(event.getPlayer().getLocation().add(0, -0.5, 0).getBlock().getType());
    }
  }

  @EventHandler
  public void onPlayerPvp(EntityDamageByEntityEvent event)  {
    if(!Blockmania.getGameManager().getGameData().isGameRunning() || Blockmania.getConfigManager().isPvpEnabled()) return;

    if(!(event.getEntity() instanceof Player)) return;

    if(event.getDamager().getType() == EntityType.ARROW) {
      Arrow arrow = (Arrow) event.getDamager();
      ProjectileSource damager = arrow.getShooter();
      if(!(damager instanceof Player)) return;
    } else if(!(event.getDamager() instanceof Player)) return;

    event.setCancelled(true);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if(Blockmania.getGameManager().getGameData().isGameRunning()) {
      //Slight delay so minecraft can properly load
      new BukkitRunnable() {
        @Override
        public void run() {
          new GameScoreboard(event.getPlayer().getUniqueId()).createScoreboard(event.getPlayer());
          if(!Blockmania.getGameManager().getGameData().getPlayersData().containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
          } else {
            GamePlayer gamePlayer = Blockmania.getGameManager().getGameData().getPlayersData().get(event.getPlayer().getUniqueId());
            gamePlayer.updateScoreboardData();

          }
        }
      }.runTaskLater(Blockmania.getInstance(), 5L);

    } else {
      GameTeam.reorganizeTeam();
    }
  }

  @EventHandler
  public void onPlayerLeft(PlayerQuitEvent event) {
    if(Blockmania.getGameManager().getGameData().isGameRunning()) {
      if (Blockmania.getGameManager().getGameData().getPlayersData().containsKey(event.getPlayer().getUniqueId())) {
        Bukkit.broadcastMessage(Language.WARNING_LEFT.getMessage().replace("%s", event.getPlayer().getDisplayName()));
      }

    }
  }
}
