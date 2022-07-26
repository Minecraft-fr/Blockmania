package fr.uiytt.blockmania.events;

import fr.uiytt.blockmania.Blockmania;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener {

  @EventHandler
  public void onPistoMove(BlockPistonExtendEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning()) return;

    for(Block block : event.getBlocks()) {
      if(block.getType() == Material.DRAGON_EGG) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler
  public void onPistonRectract(BlockPistonRetractEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning()) return;
    for(Block block : event.getBlocks()) {
      if (block.getType() == Material.DRAGON_EGG) {
        event.setCancelled(true);
        break;
      }
    }
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning()) return;

    if(event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG) {
      event.setCancelled(true);
    }
  }


}
