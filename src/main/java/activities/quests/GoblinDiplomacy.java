package activities.quests;

import activities.activity.Activity;
import activities.banking.DepositAllBanking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import util.Sleep;

import java.util.stream.Stream;

public class GoblinDiplomacy extends QuestActivity {
    /*    Goblin Diplomacy
           [62:0]
        Buy 3 goblin armour, 1 orange dye, and 1 blue dye
        Talk-to -> General Wartface -> "No, he doesn't look fat", "Do you want me to pick an armour colour for you?" "What about a different colour?"

                [62:3]
        Use -> Orange dye -> Goblin mail -> Orange goblin mail
        Talk-to -> General Wartface -> "I have some orange armour here"
        NOTES there is a cut scene after this

                [62:4]
        Use -> Blue dye -> Goblin mail -> Blue goblin mail
        Talk-to -> General Wartface -> "I have some blue armour here"
        NOTES there is a cut scene after this

                [62:5]
        Talk-to -> General Wartface -> "I have some brown armour here"
        NOTES there is a cut scene after this

                [62:6]
        DONE!*/
    private static final Area GOBLIN_VILLAGE = new Area(2961, 3493, 2949, 3516);
    private static final Area WARTFACE_HUT = new Area(2956, 3511, 2958, 3513);
    private static final int INVENTORY_SLOTS_REQUIRED = 5;

    private static final String[] ITEMS_NEEDED = {
            "Goblin mail",
            "Orange goblin mail",
            "Blue goblin mail"
    };
    private final DepositAllBanking depositAllBanking = new DepositAllBanking(ITEMS_NEEDED);
    private final DialogueCompleter WartFaceDialogueCompleter = new DialogueCompleter(
            "General Wartface",
            WARTFACE_HUT,
            "No, he doesn't look fat",
            "Do you want me to pick an armour colour for you?",
            "What about a different colour?",
            "I have some orange armour here",
            "I have some blue armour here",
            "I have some brown armour here"
    );

    public GoblinDiplomacy() {
        super(Quest.GOBLIN_DIPLOMACY);
    }

    @Override
    public void onStart() {
        depositAllBanking.exchangeContext(getBot());
        WartFaceDialogueCompleter.exchangeContext(getBot());
    }

    @Override
    public void runActivity() throws InterruptedException {
        if (!getInventory().contains(ITEMS_NEEDED) && getInventory().getEmptySlotCount() < INVENTORY_SLOTS_REQUIRED) {
            depositAllBanking.run();
        } else if (getTabs().getOpen() != Tab.INVENTORY) {
            getTabs().open(Tab.INVENTORY);
        } else if (!GOBLIN_VILLAGE.contains(myPosition())) {
            setStatus("Walking to Location : Goblin Village");
            getWalking().webWalk(GOBLIN_VILLAGE);
        } else {
            switch (getProgress()) {
                case 0:
                    WartFaceDialogueCompleter.run();
                    break;
                case 3:
                    if (hasRequiredItems()) {
                        WartFaceDialogueCompleter.run();
                    } else {
                        getItemsNeeded();
                    }
                    break;
                case 4:
                case 5:
                    WartFaceDialogueCompleter.run();
                    // TODO: Deal with cutscene
                    break;
                case 6:
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
        return new GoblinDiplomacy();
    }

}
