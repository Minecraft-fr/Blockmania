package fr.uiytt.blockmania.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

  /**
   * Return a random {@link Location} in the border
   * @param world The {@link World} where the location is
   * @return {@link Location} Location
   */
  public static Location randomLocation(World world) {
    Location loc = null;
    for (int i =0;i<25;i++) {
      int random_x = ThreadLocalRandom.current().nextInt(-1000, 1001);
      int random_z = ThreadLocalRandom.current().nextInt(-1000, 1001);
      loc = new Location(world, random_x, 300, random_z);
      Location highestBlock = highestBlock(loc,true);
      if(highestBlock.getBlock().getType() != Material.WATER && highestBlock.getBlock().getType() != Material.LAVA) {
        break;
      }
    }
    return loc;
  }

  /**
   * Return a {@link Location} where the highest block of this location is.
   * @param originalLoc The location where it must search.
   * @param acceptTransparentBlock Check if the block is transparent.
   * @return Highest location where there is a block.
   */
  public static Location highestBlock(Location originalLoc, boolean acceptTransparentBlock) {
    Location loc = originalLoc.clone();
    loc.setY(originalLoc.getWorld().getMaxHeight());
    while(true) {
      loc.setY(loc.getBlockY() - 1);
      if(loc.getBlockY() == -1) {
        loc.setY(300d);
        break;
      }
      if(loc.getBlockY() >= originalLoc.getWorld().getMaxHeight()) {continue;}

      //break if block is occluding or if this accept transparent block
      //and of course if the material is not air
      if(loc.getBlock().getType().isOccluding() || acceptTransparentBlock) {
        if(loc.getBlock().getType() != Material.AIR) {
          break;
        }
      }
    }
    return loc;

  }
  /**
   * Return a {@link Location} where the highest block of this location is.
   * It include all transparent blocks.
   * @param loc The location where it must search.
   * @return Highest location where there is a block.
   */
  public static Location highestBlock(Location loc) {
    return highestBlock(loc, true);
  }
}
