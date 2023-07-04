package fr.uiytt.blockmania.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemStackBuilder {
  public static ItemStack build(Material material, String name, List<String> lore) {
    return build(material,name,lore.toArray(new String[0]),1);
  }

  public static ItemStack build(Material material,String name, String[] lore) {
    return build(material, name, lore, 1);

  }
  public static ItemStack build(Material material,String name) {
    return build(material, name, new String[] {}, 1);
  }

  @SuppressWarnings("ConstantConditions")
  public static ItemStack build(Material material,String name, String[] lore,int ammount) {
    ItemStack item = new ItemStack(material,ammount);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    if(lore.length > 0) {
      meta.setLore(Arrays.asList(lore));
    }
    item.setItemMeta(meta);
    return item;

  }

}
