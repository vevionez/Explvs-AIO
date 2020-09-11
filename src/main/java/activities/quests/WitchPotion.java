package activities.quests;

import activities.activity.Activity;
import activities.banking.DepositAllBanking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import util.Sleep;

import java.util.stream.Stream;

public class WitchPotion extends QuestActivity {

/*[67:0] Talk to Hetty
[67:1] Rat tail drop available (collect all items) -> talk to Hetty
[67:2] Drink from potion
[67:3] Quest complete*/


    private static final Area ArcheryShop = new Area(2953, 3205, 2960, 3202);
    private static final Area HettyHouse = new Area(2970, 3203, 2965, 3208);
    private static final Area GiantRat = new Area(2991, 3193, 3003, 3182);
    private static final Area MagicShop = new Area(3011, 3261, 3016, 3256);
    private static final Area OnionField = new Area(2946, 3260, 2955, 3248);
    private static final int INVENTORY_SLOTS_REQUIRED = 5;

    private static final String[] ITEMS_NEEDED = {
            "Rat's tail",
            "Burnt meat",
            "Onion",
            "Eye of newt"
    };
    private final DepositAllBanking depositAllBanking = new DepositAllBanking(ITEMS_NEEDED);
    private final DialogueCompleter HettyDialogueCompleter = new DialogueCompleter(
            "Hetty",
            HettyHouse
    );

    public WitchPotion() {
        super(Quest.WITCH_POTION);
    }

    @Override
    public void runActivity() throws InterruptedException {
        if (!getInventory().contains(ITEMS_NEEDED) && getInventory().getEmptySlotCount() < INVENTORY_SLOTS_REQUIRED) {
            depositAllBanking.run();
        } else if (getTabs().getOpen() != Tab.INVENTORY) {
            getTabs().open(Tab.INVENTORY);
        } else {
            switch (getProgress()) {
                case 0:
                    HettyDialogueCompleter.run();
                    break;
                case 1:
                    if (hasRequiredItems()) {
                        HettyDialogueCompleter.run();
                    } else {
                        getItemsNeeded();
                    }
                    break;
                case 2:
                    // Drink the potion

                    break;
                case 3:
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
        if (!getInventory().contains("Onion")) {
            getOnion();
        }
        if (!getInventory().contains("Rat's tail")) {
            getRatTail();
        }
        if (!getInventory().contains("Burnt meat")) {
            getBurntMeat();
        }
        if (!getInventory().contains("Eye of newt")) {
            getEyeOfNewt();
        }
    }

    private void getRatTail() {
        if (!ArcheryShop.contains(myPosition())) {
            setStatus("Walking to Location : Archery Shop");
            getWalking().webWalk(ArcheryShop);
        }
        // Kill rat
        NPC Rat = getNpcs().closest(npc -> npc.getName().equals("Rat") && npc.isAttackable());
        if (Rat != null && Rat.interact("Attack")) {
            setStatus("Attacking NPC: Rat");
            Sleep.sleepUntil(() -> myPlayer().getInteracting() != null, 5000, 600);
        }
        // loot Tail
        GroundItem tail = groundItems.closest("Rat's tail");
        if(tail.exists()){
            setStatus("Looting: Tail");
            tail.interact("Take");
        }
    }

    private void getBurntMeat() {
        if (!getInventory().contains("Raw rat meat") || !getInventory().contains("Cooked meat")) {
            if (!GiantRat.contains(myPosition())) {
                setStatus("Walking to Location : Giant Rat Location");
                getWalking().webWalk(GiantRat);
            }
            // Kill a Giant rat
            NPC GiantRat = getNpcs().closest(npc -> npc.getName().equals("Giant rat") && npc.isAttackable());
            if (GiantRat != null && GiantRat.interact("Attack")) {
                setStatus("Attacking NPC: Giant rat");
                Sleep.sleepUntil(() -> myPlayer().getInteracting() != null, 5000, 600);
            }
            GroundItem meat = groundItems.closest("Raw rat meat");
            // loot meat
            if(meat.exists()){
                setStatus("Looting: Raw rat meat");
                meat.interact("Take");
            }
        }
        if (getInventory().contains("Raw rat meat")) {
            // Cook raw Meat
        }
        if (getInventory().contains("Cooked meat")) {
            // Cook Cooked Meat
        }
    }

    private void getEyeOfNewt() {
        if (!MagicShop.contains(myPosition())) {
            setStatus("Walking to Location : Magic shop");
            getWalking().webWalk(MagicShop);
        }
        // Buy an Eye of Newt
    }

    private void getOnion() {
        if (!OnionField.contains(myPosition())) {
            setStatus("Walking to Location : Onion Field ");
            getWalking().webWalk(OnionField);
        }
    }

    private boolean hasRequiredItems() {
        return Stream.of(ITEMS_NEEDED).allMatch(item -> getInventory().contains(item));
    }

    @Override
    public Activity copy() {
        return new WitchPotion();
    }

}
