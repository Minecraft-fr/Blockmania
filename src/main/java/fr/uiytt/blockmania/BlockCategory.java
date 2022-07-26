package fr.uiytt.blockmania;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockCategory {
  private final String name;
  private final int difficulty;
  private final List<Material> blocs = new ArrayList<>();
  public BlockCategory(String name, List<String> materialsList, int difficulty) {
    this.name = name;
    this.difficulty = difficulty;
    blocs.addAll(sanitizeBlock(materialsList));
  }

  public void reduceToOne() {
    int i = ThreadLocalRandom.current().nextInt(0, blocs.size());
    Material material = blocs.remove(i);
    blocs.clear();
    blocs.add(material);
  }

  public Material getRandomBlock() {
    int i = ThreadLocalRandom.current().nextInt(0, blocs.size());
    return blocs.get(i);
  }

  private static List<Material> sanitizeBlock(List<String> materialsString) {
    List<Material> materials = new ArrayList<>();
    for(String materialString : materialsString) {
      Material material = Material.getMaterial(materialString);
      if(material == null) {
        Blockmania.getPluginLogger().info("Material " + materialString + " could not be found");
        continue;
      }
      if(!material.isBlock()) {
        Blockmania.getPluginLogger().info("Material " + materialString + " is not a block");
        continue;
      }
      materials.add(material);
    }
    return materials;
  }
  public List<Material> getBlocs() {
    return new ArrayList<>(blocs);
  }

  public String getName() {
    return name;
  }

  public int getDifficulty() {
    return difficulty;
  }
}
