package fr.uiytt.blockmania.config;

public enum GameType {
  OBJECTIVE_TIME(Language.GUI_MAIN_GAME_TYPE_TIME),
  OBJECTIVE_POINTS(Language.GUI_MAIN_GAME_TYPE_POINTS),
  OBJECTIVE_ROUNDS(Language.GUI_MAIN_GAME_TYPE_ROUNDS);

  private final Language guiLore;
  GameType(Language guiLore) {
    this.guiLore = guiLore;
  }

  public Language getGuiLore() {
    return guiLore;
  }

  public static GameType getTypeFromName(String name) {
    for (GameType gameType : values()) {
      if(gameType.name().equalsIgnoreCase(name)) {
        return gameType;
      }
    }
    return null;
  }

  public enum RoundType {
    NO_ROUND(Language.GUI_MAIN_ROUND_TYPE_NO_ROUND),
    NORMAL(Language.GUI_MAIN_ROUND_TYPE_NORMAL),
    FULL_ROUND(Language.GUI_MAIN_ROUND_TYPE_FULL_ROUND),
    CYCLE(Language.GUI_MAIN_ROUND_TYPE_CYCLE);

    private final Language guiLore;
    RoundType(Language guiLore) {
      this.guiLore = guiLore;
    }

    public Language getGuiLore() {
      return guiLore;
    }

    public static RoundType getRoundTypeFromName(String name) {
      for (RoundType roundType : values()) {
        if(roundType.name().equalsIgnoreCase(name)) {
          return roundType;
        }
      }
      return null;
    }
  }
}
