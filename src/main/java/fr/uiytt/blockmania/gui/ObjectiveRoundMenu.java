package fr.uiytt.blockmania.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.uiytt.blockmania.Blockmania;
import fr.uiytt.blockmania.config.ConfigManager;
import fr.uiytt.blockmania.config.GameType;
import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.utils.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ObjectiveRoundMenu implements InventoryProvider {

	private final ConfigManager config = Blockmania.getConfigManager();
	public final SmartInventory inventory = SmartInventory.builder()
			.id("BM_ObjRound")
			.title(ChatColor.DARK_GRAY + "Objective round")
			.size(3, 9)
			.provider(this)
			.manager(Blockmania.getInvManager())
			.parent(new MainMenu().inventory)
			.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		ConfigManager config = Blockmania.getConfigManager();
		contents.fillBorders(ClickableItem.empty(ItemStackBuilder.build(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", new String[] {} )));
		contents.set(1,1, ClickableItem.of(ItemStackBuilder.build(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-10"), event -> {
			config.setObjectiveRound(Math.max(1, config.getObjectiveRound() - 10));
			updateItemValue(contents);
		}));
		contents.set(1,2, ClickableItem.of(ItemStackBuilder.build(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-5"), event -> {
			config.setObjectiveRound(Math.max(1, config.getObjectiveRound() - 5));
			updateItemValue(contents);
		}));
		contents.set(1,3, ClickableItem.of(ItemStackBuilder.build(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-1"), event -> {
			config.setObjectiveRound(Math.max(1, config.getObjectiveRound() - 1));
			updateItemValue(contents);
		}));

		contents.set(1,5, ClickableItem.of(ItemStackBuilder.build(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+1"), event -> {
			config.setObjectiveRound(config.getObjectiveRound() + 1);
			updateItemValue(contents);
		}));
		contents.set(1,6, ClickableItem.of(ItemStackBuilder.build(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+5"), event -> {
			config.setObjectiveRound(config.getObjectiveRound() + 5);
			updateItemValue(contents);
		}));
		contents.set(1,7, ClickableItem.of(ItemStackBuilder.build(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+10"), event -> {
			config.setObjectiveRound(config.getObjectiveRound() + 10);
			updateItemValue(contents);
		}));
		contents.set(0, 0, ClickableItem.of(ItemStackBuilder.build(Material.PAPER, ChatColor.GRAY + "<---"), event -> inventory.getParent().ifPresent(inventory -> inventory.open(player))));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
		updateItemValue(contents);
	}
	private void updateItemValue(InventoryContents contents) {
		contents.set(1, 4,ClickableItem.empty(
						ItemStackBuilder.build(Material.SOUL_TORCH, Language.GUI_MAIN_GAME_TYPE_TITLE.getMessage(),
										Language.splitLore(GameType.OBJECTIVE_POINTS.getGuiLore().getMessage()
														.replace("%s%", String.valueOf(config.getObjectiveTime() / 60))
														.replace("%s2%", String.valueOf(config.getObjectivePoints()))
														.replace("%s3%", String.valueOf(config.getObjectiveRound())))
						)
		));
	}

}
