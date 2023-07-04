package fr.uiytt.blockmania.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.ConfigManager;
import fr.uiytt.blockmania.config.GameType;
import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.game.GameTeam;
import fr.uiytt.blockmania.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MainMenu implements InventoryProvider {
  private final ConfigManager config = Blockmania.getConfigManager();
  public final SmartInventory inventory = SmartInventory.builder()
          .id("BM_MainMenu")
          .size(4, 9)
          .title(ChatColor.BOLD + "Config")
          .provider(this)
          .manager(Blockmania.getInvManager())
          .build();

  @Override
  public void init(Player player, InventoryContents contents) {
    contents.fillRow(0, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(1, 1, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(2, 2, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 3, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 4, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 5, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(2, 6, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(1, 7, ClickableItem.empty(ItemStackBuilder.build(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GRAY + "")));

    contents.set(1, 0, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(2, 1, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 2, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 6, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(2, 7, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(1, 8, ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));

    contents.set(2, 0, ClickableItem.empty(ItemStackBuilder.build(Material.WHITE_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 1, ClickableItem.empty(ItemStackBuilder.build(Material.WHITE_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 7, ClickableItem.empty(ItemStackBuilder.build(Material.WHITE_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(2, 8, ClickableItem.empty(ItemStackBuilder.build(Material.WHITE_STAINED_GLASS_PANE, ChatColor.GRAY + "")));

    contents.set(3, 0, ClickableItem.empty(ItemStackBuilder.build(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
    contents.set(3, 8, ClickableItem.empty(ItemStackBuilder.build(Material.LIGHT_GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "")));
  }

  @Override
  public void update(Player player, InventoryContents contents) {

    //GameType
    String[] lore = Language.splitLore(config.getGameType().getGuiLore().getMessage()
            .replace("%s%", String.valueOf(config.getObjectiveTime() / 60))
            .replace("%s2%", String.valueOf(config.getObjectivePoints()))
            .replace("%s3%", String.valueOf(config.getObjectiveRound())));
    GameType nextGameType;
    switch (config.getGameType()) {
      case OBJECTIVE_TIME -> nextGameType = GameType.OBJECTIVE_POINTS;
      case OBJECTIVE_POINTS -> nextGameType = GameType.OBJECTIVE_ROUNDS;
      default -> nextGameType = GameType.OBJECTIVE_TIME;
    }
    contents.set(1, 2, ClickableItem.of(ItemStackBuilder.build(Material.COMPASS, Language.GUI_MAIN_GAME_TYPE_TITLE.getMessage(), lore),
            inventoryClickEvent -> {
              if(inventoryClickEvent.isLeftClick()) config.setGameType(nextGameType);
              else if (inventoryClickEvent.isRightClick()) {
                switch (config.getGameType()) {
                  case OBJECTIVE_TIME -> new ObjectiveTimeMenu().inventory.open(player);
                  case OBJECTIVE_POINTS ->  new ObjectivePointsMenu().inventory.open(player);
                  default ->  new ObjectiveRoundMenu().inventory.open(player);
                }
              }
            }));

    //RoundType
    String[] lore2 = Language.splitLore(config.getRoundType().getGuiLore().getMessage());
    GameType.RoundType nextRoundType;
    switch (config.getRoundType()) {
      case NO_ROUND -> nextRoundType = GameType.RoundType.NORMAL;
      case NORMAL -> nextRoundType = GameType.RoundType.FULL_ROUND;
      case FULL_ROUND -> nextRoundType = GameType.RoundType.CYCLE;
      default -> nextRoundType = GameType.RoundType.NO_ROUND;
    }
    contents.set(1, 3, ClickableItem.of(ItemStackBuilder.build(Material.SOUL_TORCH, Language.GUI_MAIN_ROUND_TYPE_TITLE.getMessage(), lore2),
            inventoryClickEvent -> config.setRoundType(nextRoundType)));


    contents.set(1, 4, ClickableItem.of(ItemStackBuilder.build(Material.CLOCK, Language.GUI_MAIN_ROUND_TIME_TITLE.getMessage(),
                    Language.splitLore(Language.GUI_MAIN_ROUND_TIME_LORE.getMessage()
                            .replace("%s%", String.valueOf(config.getRoundLength()/60)))),
            inventoryClickEvent -> new RoundTimeMenu().inventory.open(player)));

    //TeamSize
    if (config.getTeamSize() <= 1) {
      contents.set(1, 5, ClickableItem.of(
              ItemStackBuilder.build(Material.WHITE_BANNER, Language.GUI_MAIN_TEAMS_SIZE_NAME.getMessage(),
                      Language.splitLore(Language.GUI_MAIN_TEAMS_FFA_LORE.getMessage())
              ), event -> {
                config.setTeamSize(2);
                GameTeam.reorganizeTeam();
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.getInventory().addItem(new ItemStack(Material.WHITE_BANNER)));
              }));
    } else {
      contents.set(1, 5, ClickableItem.of(
              ItemStackBuilder.build(Material.WHITE_BANNER, Language.GUI_MAIN_TEAMS_SIZE_NAME.getMessage(),
                      Language.splitLore(Language.GUI_MAIN_TEAMS_SIZE_LORE.getMessage().replace("%s%", String.valueOf(config.getTeamSize())))
              ), event -> {
                config.setTeamSize(config.getTeamSize() == 7 ? 1 : config.getTeamSize() + 1);

                for (Player player1 : Bukkit.getOnlinePlayers()) {
                  for (int i = 0; i < 41; i++) {
                    ItemStack item = player1.getInventory().getItem(i);
                    if (item == null || !item.getType().getKey().toString().contains("banner")) continue;
                    if (config.getTeamSize() == 1) {
                      player1.getInventory().setItem(i, new ItemStack(Material.AIR));
                    } else {
                      player1.getInventory().setItem(i, new ItemStack(Material.WHITE_BANNER));
                    }
                  }
                }
                GameTeam.reorganizeTeam();
              }));
    }
    String enabled = config.isSameBlockEnabled() ? Language.GUI_ENABLE.getMessage() : Language.GUI_DISABLE.getMessage();
    contents.set(1, 6, ClickableItem.of(ItemStackBuilder.build(Material.DIRT, Language.GUI_MAIN_ONLY_ONE_BLOCK_TITLE.getMessage(),
                    Language.splitLore(Language.GUI_MAIN_ONLY_ONE_BLOCK_LORE.getMessage()
                            .replace("%enable%", enabled))),
            inventoryClickEvent -> config.setSameBlock(!config.isSameBlockEnabled())));
    String enabled2 = config.isPvpEnabled() ? Language.GUI_ENABLE.getMessage() : Language.GUI_DISABLE.getMessage();
    contents.set(2, 3, ClickableItem.of(ItemStackBuilder.build(Material.DIAMOND_SWORD, Language.GUI_MAIN_PVP_TITLE.getMessage(),
                    Language.splitLore(Language.GUI_MAIN_PVP_LORE.getMessage()
                            .replace("%enable%", enabled2))),
            inventoryClickEvent -> config.setPvp(!config.isPvpEnabled())));
    String enabled3 = config.isStarterFood() ? Language.GUI_ENABLE.getMessage() : Language.GUI_DISABLE.getMessage();
    contents.set(2, 4, ClickableItem.of(ItemStackBuilder.build(Material.COOKED_BEEF, Language.GUI_MAIN_FOOD_TITLE.getMessage(),
                    Language.splitLore(Language.GUI_MAIN_FOOD_LORE.getMessage()
                            .replace("%enable%", enabled3))),
            inventoryClickEvent -> config.setStarterFood(!config.isStarterFood())));
    String enabled4 = config.isKeepInventory() ? Language.GUI_ENABLE.getMessage() : Language.GUI_DISABLE.getMessage();
    contents.set(2, 5, ClickableItem.of(ItemStackBuilder.build(Material.CHEST, Language.GUI_MAIN_KEEP_INVENTORY_TITLE.getMessage(),
                    Language.splitLore(Language.GUI_MAIN_KEEP_INVENTORY_LORE.getMessage()
                            .replace("%enable%", enabled4))),
            inventoryClickEvent -> config.setKeepInventory(!config.isKeepInventory())));
  }
}
