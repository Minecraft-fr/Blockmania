package fr.uiytt.blockmania;

import de.leonhard.storage.util.FileUtils;
import fr.minuskube.inv.InventoryManager;
import fr.uiytt.blockmania.config.ConfigManager;
import fr.uiytt.blockmania.events.BlockListener;
import fr.uiytt.blockmania.events.PlayerListener;
import fr.uiytt.blockmania.game.GameManager;
import fr.uiytt.blockmania.game.GameTeam;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

public class Blockmania extends JavaPlugin {
  private static Blockmania instance;
  private static Logger logger;
  private static ConfigManager configManager;
  private static InventoryManager inventoryManager;
  private static GameManager gameManager;

  @Override
  public void onEnable() {
    super.onEnable();
    logger = this.getLogger();
    instance = this;

    //Hook in the SmartInv API for GUI
    inventoryManager = new InventoryManager(this);
    inventoryManager.init();

    getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    getServer().getPluginManager().registerEvents(new BlockListener(), this);

    Objects.requireNonNull(getCommand("blockmania")).setExecutor(new Command());
    Objects.requireNonNull(getCommand("blockmania")).setTabCompleter(new Command());

    configManager = new ConfigManager();

    Blockmania.registerNewGameManager();
  }
  @NotNull
  public static Logger getPluginLogger() {
    return logger;
  }

  public static Blockmania getInstance() {
    return instance;
  }

  public static ConfigManager getConfigManager() {
    return configManager;
  }

  public static GameManager getGameManager() {
    return gameManager;
  }

  public static void registerNewGameManager() {
    gameManager = new GameManager();
    GameTeam.reorganizeTeam();
  }

  public static InventoryManager getInvManager() {
    return inventoryManager;
  }
}

