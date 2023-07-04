package fr.uiytt.blockmania.config;

import de.leonhard.storage.Yaml;
import fr.uiytt.blockmania.Blockmania;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.nio.file.Files;

public enum Language {

    ERROR_HEADER("error_header"),
    HEADER("header"),

    GAME_VICTORY("game.victory"),
    GAME_VICTORY_PLURAL("game.victory_plural"),
    GAME_PVP_ON("game.pvp_on"),
    GAME_KEEP_INVENTORY_ON("game.keep_inventory_on"),
    GAME_TELEPORTING("game.teleporting"),
    GAME_TELEPORTING_PLAYER("game.teleporting_player"),
    GAME_TELEPORTING_TEAM("game.teleporting_team"),
    GAME_COMPLETED_BLOCK("game.completed_block"),

    ERROR_ADD_TEAM("warning.error.add_team"),
    WARNING_FFA("warning.ffa"),
    WARNING_LEFT("warning.left"),
    WARNING_PERMISSION("warning.permission"),
    WARNING_GAME_ON("warning.game_is_on"),
    WARNING_GAME_OFF("warning.game_is_off"),
    WARNING_CONSOL("consol"),

    COMMAND_STOP("command.stop"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_FINISH_ITEMS("command.finish_items"),
    COMMAND_HELP_TITLE("command.help.title"),
    COMMAND_HELP_ALIASES("command.help.aliases"),
    COMMAND_HELP_START("command.help.start"),
    COMMAND_HELP_STOP("command.help.stop"),
    COMMAND_HELP_CONFIG("command.help.config"),
    COMMAND_HELP_RELOAD("command.help.reload"),
    COMMAND_HELP_TEAM("command.help.team"),
    COMMAND_HELP_HELP("command.help.help"),

    SCOREBOARD_TIME("scoreboard.time"),
    SCOREBOARD_POINTS("scoreboard.points"),
    SCOREBOARD_ROUND("scoreboard.round"),
    SCOREBOARD_OBJECTIVES("scoreboard.objectives"),
    SCOREBOARD_OBJECTIVE_TIME("scoreboard.objective_time"),
    SCOREBOARD_OBJECTIVE_ROUNDS("scoreboard.objective_rounds"),
    SCOREBOARD_OBJECTIVE_POINTS("scoreboard.objective_points"),
    SCOREBOARD_BLOCK_TITLE("scoreboard.block_title"),
    SCOREBOARD_BLOCK("scoreboard.block"),
    SCOREBOARD_NEXT_ROUND_TITLE("scoreboard.next_round_title"),
    SCOREBOARD_NEXT_ROUND("scoreboard.next_round"),

    GUI_ENABLE("gui.enable"),
    GUI_DISABLE("gui.disable"),
    GUI_PREVIOUS("gui.previous"),
    GUI_NEXT("gui.next"),
    GUI_MAIN_GAME_TYPE_TITLE("gui.main.game_type.title"),
    GUI_MAIN_GAME_TYPE_TIME("gui.main.game_type.time"),
    GUI_MAIN_GAME_TYPE_POINTS("gui.main.game_type.points"),
    GUI_MAIN_GAME_TYPE_ROUNDS("gui.main.game_type.rounds"),
    GUI_MAIN_ROUND_TYPE_TITLE("gui.main.round_type.title"),
    GUI_MAIN_ROUND_TYPE_NO_ROUND("gui.main.round_type.no_round"),
    GUI_MAIN_ROUND_TYPE_NORMAL("gui.main.round_type.normal"),
    GUI_MAIN_ROUND_TYPE_FULL_ROUND("gui.main.round_type.full_round"),
    GUI_MAIN_ROUND_TYPE_CYCLE("gui.main.round_type.cycle"),
    GUI_MAIN_ROUND_TIME_TITLE("gui.main.round_time.title"),
    GUI_MAIN_ROUND_TIME_LORE("gui.main.round_time.lore"),
    GUI_MAIN_TEAMS_SIZE_NAME("gui.main.teams_size_name"),
    GUI_MAIN_TEAMS_FFA_LORE("gui.main.teams_ffa_lore"),
    GUI_MAIN_TEAMS_SIZE_LORE("gui.main.teams_size_lore"),
    GUI_MAIN_ONLY_ONE_BLOCK_TITLE("gui.main.only_one_block.title"),
    GUI_MAIN_ONLY_ONE_BLOCK_LORE("gui.main.only_one_block.lore"),
    GUI_MAIN_PVP_TITLE("gui.main.pvp.title"),
    GUI_MAIN_PVP_LORE("gui.main.pvp.lore"),
    GUI_MAIN_FOOD_TITLE("gui.main.food.title"),
    GUI_MAIN_FOOD_LORE("gui.main.food.lore"),
    GUI_MAIN_KEEP_INVENTORY_TITLE("gui.main.keep_inventory.title"),
    GUI_MAIN_KEEP_INVENTORY_LORE("gui.main.keep_inventory.lore"),

    GUI_TITLE_ADVANCED("gui.title.advanced_menu"),
    GUI_TITLE_APPLES_RATE("gui.title.apples_rate_menu"),
    GUI_TITLE_BORDER_END("gui.title.border_end_menu"),
    GUI_TITLE_BORDER_START("gui.title.border_start_menu"),
    GUI_TITLE_BORDER_SPEED("gui.title.border_speed_menu"),
    GUI_TITLE_BORDER_TIME("gui.title.border_time_menu"),
    GUI_TITLE_DEATH_ITEMS("gui.title.death_items_menu"),
    GUI_TITLE_DIAMOND_LIMIT("gui.title.diamond_limit_menu"),
    GUI_TITLE_EVENTS("gui.title.events_menu"),
    GUI_TITLE_FLITNTS_RATE("gui.title.flints_rate_menu"),
    GUI_TITLE_MAIN("gui.title.main_menu"),
    GUI_TITLE_PVP_TIME("gui.title.pvp_menu"),
    GUI_TITLE_RESPAWN("gui.title.respawn_menu"),
    GUI_TITLE_SPECTATOR_INVENTORY("gui.title.spectator_inventory_menu"),
    GUI_TITLE_START_ITEMS("gui.title.start_items_menu"),
    GUI_TITLE_TEAMS_MENU("gui.title.teams_menu"),
    GUI_TITLE_EVENTS_TIME("gui.title.time_events"),

    GAME_STOP("game.stop");

    public static void init(String langName) {
        try {
            File languageFile = new File(Blockmania.getInstance().getDataFolder().getAbsolutePath() + File.separator + "languages" + File.separator + langName + ".yml");
            if (!languageFile.exists()) {
                Blockmania.getPluginLogger().info(languageFile.getName() + " not found, extracting...");
                languageFile.getParentFile().mkdirs();
                Files.copy(Language.class.getResourceAsStream("/languages/"+ langName +".yml"), languageFile.toPath());
            }

            Yaml languageYaml = new Yaml(langName, "plugins/" + Blockmania.getInstance().getDataFolder().toString().split("/")[1] +"/languages");
            for (Language message : Language.values()) {
                String newMessage = languageYaml.getOrDefault(message.path, "&cThe text in file " + languageYaml.getName() + " with path " + message.path + " could not be found.");
                newMessage = newMessage.replace("%header%", HEADER.message);
                newMessage = newMessage.replace("%error_header%", ERROR_HEADER.message);

                message.message = ChatColor.translateAlternateColorCodes('&', newMessage);
            }
        } catch (Exception exception) {
            Blockmania.getPluginLogger().severe(exception.getMessage());
            Blockmania.getPluginLogger().severe("There was a probleme while loading the language file " + langName);
        }
    }


    private final String path;
    private String message;

    Language(String path) {
        this.path = path;
        this.message = "not loaded";
    }

    public String getMessage() {
        return message;
    }

    /**
     * Transform a string into a lore.
     * It splits the lore at ||
     *
     * @param lore A string which should be transformed into a lore
     * @return A String[] containing the lore.
     */
    public static String[] splitLore(String lore) {
        return lore.split("\n");
    }
}
