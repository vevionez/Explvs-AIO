package activities.quests;

import activities.activity.Activity;
import activities.banking.DepositAllBanking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Tab;

import java.util.stream.Stream;

public class DoricsQuest extends QuestActivity {
    /*    Goblin Diplomacy
           [62:0]
        Buy 6 clay, 4 copper ore and 2 iron ore
        Talk-to -> Doric -> "No, he doesn't look fat", "Do you want me to pick an armour colour for you?" "What about a different colour?"
        DONE!*/
    private static final Area DORICS_HOUSE = new Area(2953, 3449, 2950, 3454);
    private static final int INVENTORY_SLOTS_REQUIRED = 12;

    private static final String[] ITEMS_NEEDED = {
            "Clay",
            "Copper ore",
            "Iron ore"
    };
    private final DepositAllBanking depositAllBanking = new DepositAllBanking(ITEMS_NEEDED);
    private final DialogueCompleter DoricDialogueCompleter = new DialogueCompleter(
            "Doric",
            DORICS_HOUSE,
            "I wanted to use your anvils.",
            "Yes, I will get you the materials."
    );

    public DoricsQuest() {
        super(Quest.DORICS_QUEST);
    }

    @Override
    public void onStart() {
        depositAllBanking.exchangeContext(getBot());
        DoricDialogueCompleter.exchangeContext(getBot());
    }

    @Override
    public void runActivity() throws InterruptedException {
        if (!getInventory().contains(ITEMS_NEEDED) && getInventory().getEmptySlotCount() < INVENTORY_SLOTS_REQUIRED) {
            depositAllBanking.run();
        } else if (getTabs().getOpen() != Tab.INVENTORY) {
            getTabs().open(Tab.INVENTORY);
        } else if (!DORICS_HOUSE.contains(myPosition())) {
            setStatus("Walking to Location : Dorics House");
            getWalking().webWalk(DORICS_HOUSE);
        } else {
            switch (getProgress()) {
                case 0:
                    DoricDialogueCompleter.run();
                case 10:
                    if (hasRequiredItems()) {
                        DoricDialogueCompleter.run();
                    } else {
                        getItemsNeeded();
                    }
                    break;
                case 100:
                    log("Quest is complete");
                    isComplete = true;
                    break;
                default:
                    log("Unknown progress config value: " + getProgress());
                    setFailed();
                    break;
            }
        }
    }

    private void getItemsNeeded() throws InterruptedException {
    }

    private boolean hasRequiredItems() {
        return Stream.of(ITEMS_NEEDED).allMatch(item -> getInventory().contains(item));
    }

    @Override
    public Activity copy() {
        return new DoricsQuest();
    }

}
