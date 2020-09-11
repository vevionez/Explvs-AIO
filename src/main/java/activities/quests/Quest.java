package activities.quests;

import org.osbot.rs07.api.Configs;

public enum Quest {


    SHEEP_SHEARER("Sheep Shearer", 179, 21, null, null),
    THE_RESTLESS_GHOST("The Restless Ghost", 107, 5, null, null),
    RUNE_MYSTERIES("Rune Mysteries", 63, 6, null, null),
    GOBLIN_DIPLOMACY("Goblin Diplomacy", 62, 6, null, new String[]{"1x Goblin mail", "1x Orange goblin mail", "1x Blue goblin mail"}),
    WITCH_POTION("Witches Potion", 67, 3, new String[]{"Combat lvl high enough to kill giant rat."}, new String[]{"1x Rat's tail", "1x Burnt meat", "1x Eye of Newt", "1x Onion"}),
    COOKS_ASSISTANT("Cooks Assistant", 29, 2, null, new String[]{"1x Pot of flour", "1x Bucket of milk", "1x Egg"}),
    ROMEO_AND_JULIET("Romeo & Juliet", 144, 100, null, null);
    //ABYSS ("Abyss Mini Quest", 492, 4);

    final String name;
    final int configID;
    final int completedConfigVal;
    final String[] Requirements;
    final String[] optionalRequirements;
    Quest(final String name, final int configID, final int completedConfigVal, final String[] Requirements, String[] optionalRequirements) {
        this.name = name;
        this.configID = configID;
        this.completedConfigVal = completedConfigVal;
        this.Requirements = Requirements;
        this.optionalRequirements = optionalRequirements;
    }

    public boolean isComplete(final Configs configs) {
        return configs.get(configID) == completedConfigVal;
    }

    @Override
    public String toString() {
        return name;
    }

    public String[] getRequirements() {
        return Requirements;
    }

    public String[] getOptionalRequirements() {
        return optionalRequirements;
    }
}
