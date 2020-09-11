package activities.skills.combat;

import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.ui.Skill;
import util.Tool;

public enum Weapon implements Tool {

    BRONZE("Bronze Scimitar", 1),
    IRON("Iron Scimitar", 1),
    STEEL("Steel Scimitar", 5),
    BLACK("Black Scimitar", 10),
    MITHRIL("Mithril Scimitar", 20),
    ADAMANT("Adamant Scimitar", 30),
    RUNE("Rune Scimitar", 40),
    DRAGON("Dragon Scimitar", 60);

    String name;
    int attLevelRequired;

    Weapon(final String name, final int attLevelRequired) {
        this.name = name;
        this.attLevelRequired = attLevelRequired;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLevelRequired() {
        return attLevelRequired;
    }

    @Override
    public boolean canUse(Skills skills) {
        return skills.getStatic(Skill.ATTACK) >= attLevelRequired;
    }

    @Override
    public boolean canEquip(Skills skills) {
        return skills.getStatic(Skill.ATTACK) >= attLevelRequired;
    }

    @Override
    public String toString() {
        return name;
    }
}
