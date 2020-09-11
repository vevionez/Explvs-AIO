package activities.skills.combat;

import org.osbot.rs07.api.map.Area;

public enum Npc {

    Guards("Guard",
            new util.Location("Varrock Upstairs Castle", new Area(3206, 3490, 3200, 3500).setPlane(1)),
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468)),
            new util.Location("Falador", new Area(2960, 3394, 2971, 3375))
            // new util.Location("Varrock Castle", new Area(3206, 3490, 3200, 3500)),
            // new util.Location("Varrock Castle", new Area(3206, 3490, 3200, 3500))
    ),

    Chickens("Chicken",
            new util.Location("Lumbridge East", new Area(3236, 3286, 3225, 3301)),
            new util.Location("Lumbridge West", new Area(3192, 3275, 3184, 3279)),
            new util.Location("Falador South", new Area(3037, 3281, 3026, 3290))
    ),
    Cows("Cow",
            new util.Location("Lumbridge East", new Area(
                    new int[][]{
                            {3241, 3297},
                            {3264, 3297},
                            {3264, 3255},
                            {3245, 3278}
                    }
            )),
            new util.Location("Lumbridge West", new Area(3202, 3459, 3223, 3468))
    ),
    Frogs("Frog",
            new util.Location("Lumbridge Swamp", new Area(3202, 3459, 3223, 3468))
    ),
    Goblins("Goblin",
            new util.Location("Lumbridge East", new Area(3202, 3459, 3223, 3468)),
            new util.Location("Goblin Camp (Falador north)", new Area(3202, 3459, 3223, 3468))
    ),
    Black_Knights("Black knight",
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468)),
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468))
    ),
    Giants("Hill giant",
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468)),
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468))
    ),
    Wizards("Wizard",
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468)),
            new util.Location("Varrock Castle", new Area(3202, 3459, 3223, 3468))
    );

    public String name;
    public util.Location[] locations;

    Npc(final String name, final util.Location... locations) {
        this.name = name;
        this.locations = locations;
    }

    @Override
    public String toString() {
        return name;
    }
}
