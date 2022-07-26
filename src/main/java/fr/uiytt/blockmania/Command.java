package fr.uiytt.blockmania;

import fr.uiytt.blockmania.config.Language;
import fr.uiytt.blockmania.gui.MainMenu;
import fr.uiytt.blockmania.gui.TeamsMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String cmd, @NotNull String[] args) {
        if(args.length == 0) {
            sendHelp(commandSender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start" -> {
                if(commandSender.hasPermission("blockmania.admin.start")) {
                    if(!Blockmania.getGameManager().getGameData().isGameRunning()) {
                        Blockmania.getGameManager().start();
                    } else {
                        commandSender.sendMessage(Language.WARNING_GAME_ON.getMessage());
                    }
                } else {
                    commandSender.sendMessage(Language.WARNING_PERMISSION.getMessage());
                }

            }
            case "stop" -> {
                if(commandSender.hasPermission("blockmania.admin.stop")) {
                    if (Blockmania.getGameManager().getGameData().isGameRunning()) {
                        Blockmania.getGameManager().stop();
                        Bukkit.broadcastMessage(Language.COMMAND_STOP.getMessage());
                    } else {
                        commandSender.sendMessage(Language.WARNING_GAME_OFF.getMessage());
                    }
                } else {
                    commandSender.sendMessage(Language.WARNING_PERMISSION.getMessage());
                }
            }
            case "reload" -> {
                if(commandSender.hasPermission("blockmania.admin.reload")) {
                    if (!Blockmania.getGameManager().getGameData().isGameRunning()) {
                        Blockmania.getConfigManager().reload();
                        commandSender.sendMessage(Language.COMMAND_RELOAD.getMessage());
                    } else {
                        commandSender.sendMessage(Language.WARNING_GAME_ON.getMessage());
                    }
                } else {
                    commandSender.sendMessage(Language.WARNING_PERMISSION.getMessage());
                }
            }
            case "team", "teams" -> {
                if(!Blockmania.getGameManager().getGameData().isGameRunning()) {
                    if(commandSender instanceof Player player) {
                        if (Blockmania.getConfigManager().getTeamSize() != 1) {
                            new TeamsMenu().inventory.open(player);
                        } else {
                            commandSender.sendMessage(Language.WARNING_FFA.getMessage());
                        }
                    } else {
                        commandSender.sendMessage(Language.WARNING_CONSOL.getMessage());
                    }
                } else {
                    commandSender.sendMessage(Language.WARNING_GAME_ON.getMessage());
                }
            }
            case "config" -> {
                if(commandSender.hasPermission("blockmania.admin.config")) {
                    if(!Blockmania.getGameManager().getGameData().isGameRunning()) {
                        if(commandSender instanceof Player player) new MainMenu().inventory.open(player);

                        else commandSender.sendMessage(Language.WARNING_CONSOL.getMessage());
                    } else commandSender.sendMessage(Language.WARNING_GAME_ON.getMessage());
                } else commandSender.sendMessage(Language.WARNING_PERMISSION.getMessage());
            }
            default -> sendHelp(commandSender);
        }
        return true;
    }

    private static void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(Language.COMMAND_HELP_TITLE.getMessage());
        commandSender.sendMessage(Language.COMMAND_HELP_ALIASES.getMessage());
        if(commandSender.hasPermission("blockmania.admin.start")) {
            commandSender.sendMessage(Language.COMMAND_HELP_START.getMessage());
        }
        if(commandSender.hasPermission("blockmania.admin.stop")) {
            commandSender.sendMessage(Language.COMMAND_HELP_STOP.getMessage());
        }
        if(commandSender.hasPermission("blockmania.admin.config")) {
            commandSender.sendMessage(Language.COMMAND_HELP_CONFIG.getMessage());
        }
        if(commandSender.hasPermission("blockmania.admin.reload")) {
            commandSender.sendMessage(Language.COMMAND_HELP_RELOAD.getMessage());
        }
        commandSender.sendMessage(Language.COMMAND_HELP_TEAM.getMessage());
        commandSender.sendMessage(Language.COMMAND_HELP_HELP.getMessage());
    }

    private static final String[] ARGS0_COMMANDS = { "config", "start", "stop", "reload", "team","help"};
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], Arrays.asList(ARGS0_COMMANDS), completions);
            Collections.sort(completions);
            return completions;
        }
        return null;
    }
}
