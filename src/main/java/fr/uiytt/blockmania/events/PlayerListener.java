package fr.uiytt.blockmania.events;

import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.game.GamePlayer;
import fr.uiytt.blockmania.game.GameScoreboard;
import fr.uiytt.blockmania.game.GameTeam;
import fr.uiytt.blockmania.gui.TeamsMenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
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
            GameTeam playerTeam = Blockmania.getGameManager().getGameData().getPlayersTeam().get(event.getPlayer().getUniqueId());
            if(playerTeam != null) {
              playerTeam.addTeamToTab(event.getPlayer());
            }

          }
        }
      }.runTaskLater(Blockmania.getInstance(), 5L);

    } else {
      event.getPlayer().setGameMode(GameMode.ADVENTURE);
      event.getPlayer().getInventory().clear();
      GameTeam.reorganizeTeam();
      if(Blockmania.getConfigManager().getTeamSize() != 1) {
        event.getPlayer().getInventory().addItem(new ItemStack(Material.WHITE_BANNER));
      }
    }
  }

  @EventHandler
  public void onPlayerLeft(PlayerQuitEvent event) {
    if(Blockmania.getGameManager().getGameData().isGameRunning()) {
      if (Blockmania.getGameManager().getGameData().getPlayersData().containsKey(event.getPlayer().getUniqueId())) {
        Bukkit.broadcastMessage(Language.WARNING_LEFT.getMessage().replace("%s", event.getPlayer().getDisplayName()));
      }
    } else {
      GameTeam.removePlayerFromAllTeams(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler
  public void onPlayerRightClick(PlayerInteractEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning()) {
      if (Blockmania.getConfigManager().getTeamSize() != 1 &&
              (
                      (event.getItem() != null && event.getItem().getType().toString().contains("BANNER"))
                || (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().contains("BANNER")))){
        event.setCancelled(true);
        new TeamsMenu().inventory.open(event.getPlayer());
      }
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if(Blockmania.getGameManager().getGameData().isGameRunning()) return;
    event.getDrops().clear();
    event.getEntity().getInventory().clear();
  }
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if(Blockmania.getGameManager().getGameData().isGameRunning()) return;
    if(Blockmania.getConfigManager().getTeamSize() != 1) {
      event.getPlayer().getInventory().addItem(new ItemStack(Material.WHITE_BANNER));
    }
  }
}
