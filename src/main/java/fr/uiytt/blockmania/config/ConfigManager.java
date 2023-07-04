package fr.uiytt.blockmania.config;

import de.leonhard.storage.Json;
import de.leonhard.storage.Yaml;
import fr.uiytt.blockmania.BlockCategory;
import fr.uiytt.blockmania.Blockmania;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Yaml config = new Yaml("config.yml", "plugins" + File.separatorChar + "Blockmania");
    private final Json blocsFile = new Json("blocs.json", "plugins" + File.separatorChar + "Blockmania");

    private final List<BlockCategory>  blockCategories = new ArrayList<>();
    private GameType gameType;
    private GameType.RoundType roundType;
    private int roundLength;
    private int teamSize;
    private boolean spawnTogether;
    private boolean sameBlock;
    private boolean pvp;
    private boolean starterFood;
    private boolean keepInventory;
    private int pointsPerRound;
    private int objectivePoints;
    private int objectiveTime;
    private int objectiveRound;

    public ConfigManager() {
        reload();
    }


    public void reload() {
        loadCategories();

        config.forceReload();

        roundType = GameType.RoundType.getRoundTypeFromName(config.getOrSetDefault("round.round_type", "NORMAL"));
        roundLength = config.getOrSetDefault("round.time", 600);

        teamSize = config.getOrSetDefault("team.size", 1);
        spawnTogether = config.getOrSetDefault("team.spawn_together", true);

        sameBlock = config.getOrSetDefault("same_block_all_players", false);
        pvp = config.getOrSetDefault("pvp", false);
        starterFood = config.getOrSetDefault("starter_food", true);
        keepInventory = config.getOrSetDefault("keep_inventory", true);
        pointsPerRound = config.getOrSetDefault("points_per_round", 1);

        gameType = GameType.getTypeFromName(config.getOrSetDefault("objective.type", "OBJECTIVE_POINTS"));
        objectivePoints = config.getOrSetDefault("objective.points", 10);
        objectiveTime = config.getOrSetDefault("objective.time", 40) * 60;
        objectiveRound = config.getOrSetDefault("objective.round", 10);

        String languageName = config.getOrSetDefault("serverOwner.language", "fr");

        Language.init(languageName);
    }

    @SuppressWarnings("unchecked")
    private void loadCategories() {
        blockCategories.clear();
        try {
            HashMap<String, Object> categories = (HashMap<String, Object>) blocsFile.getMap(("categories"));
            for(Map.Entry<String, Object> entry : categories.entrySet()) {
                int difficulty = blocsFile.getInt("categories." + entry.getKey() + ".difficulty");
                List<String> materials = (List<String>) blocsFile.getList("categories." + entry.getKey() + ".blocs");
                if(materials.isEmpty()) {
                    Blockmania.getPluginLogger().warning(entry.getKey() + ".blocs could not be properly loaded");
                    continue;
                }
                blockCategories.add(new BlockCategory(entry.getKey(), materials, difficulty));
            }
        } catch (ClassCastException e) {
            Blockmania.getPluginLogger().severe(e.getMessage());
        } finally {
            blockCategories.removeIf(blockCategory -> blockCategory.getBlocs().isEmpty());
            if(blockCategories.isEmpty() ) {
                blockCategories.add(new BlockCategory("default", Collections.singletonList("STONE"), 0));
            }
        }
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public boolean isSpawnTogetherEnabled() {
        return spawnTogether;
    }

    public List<BlockCategory> getBlockCategories() {
        return blockCategories;
    }

    public int getRoundLength() {
        return roundLength;
    }

    public void setRoundLength(int roundLength) {
        this.roundLength = roundLength;
    }

    public GameType.RoundType getRoundType() {
        return roundType;
    }

    public void setRoundType(GameType.RoundType roundType) {
        this.roundType = roundType;
    }

    public boolean isSameBlockEnabled() {
        return sameBlock;
    }

    public void setSameBlock(boolean sameBlock) {
        this.sameBlock = sameBlock;
    }

    public int getPointsPerRound() {
        return pointsPerRound;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public int getObjectivePoints() {
        return objectivePoints;
    }

    public void setObjectivePoints(int objectivePoints) {
        this.objectivePoints = objectivePoints;
    }

    public int getObjectiveTime() {
        return objectiveTime;
    }

    public void setObjectiveTime(int objectiveTime) {
        this.objectiveTime = objectiveTime;
    }

    public int getObjectiveRound() {
        return objectiveRound;
    }

    public void setObjectiveRound(int objectiveRound) {
        this.objectiveRound = objectiveRound;
    }

    public boolean isPvpEnabled() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isStarterFood() {
        return starterFood;
    }

    public void setStarterFood(boolean starterFood) {
        this.starterFood = starterFood;
    }

    public boolean isKeepInventory() {
        return keepInventory;
    }

    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }
}
