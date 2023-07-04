package fr.uiytt.blockmania.events;

import fr.uiytt.blockmania.Blockmania;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {

  @EventHandler
  public void onDragonSpawn(EntitySpawnEvent event) {
    if(!Blockmania.getGameManager().getGameData().isGameRunning() || event.getEntityType() != EntityType.ENDER_DRAGON) return;
    EnderDragon enderDragon = (EnderDragon) event.getEntity();
    AttributeInstance maxHealthAttribtue = enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    assert maxHealthAttribtue != null;
    maxHealthAttribtue.setBaseValue(maxHealthAttribtue.getBaseValue() / 2d);
  }
}
