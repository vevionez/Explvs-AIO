package activities.quests;

import activities.activity.Activity;
import activities.banking.DepositAllBanking;
import activities.skills.combat.Npc;
import org.osbot.rs07.api.Store;
import org.osbot.rs07.api.Trade;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import util.MakeAllInterface;
import util.Sleep;
import util.item_requirement.ItemReq;

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
            HettyHouse,
            "I am in search of a quest.",
            "Yes, help me become one with my darker side."
    );
    private Entity Onion;
    private MakeAllInterface makeAllInterface;

    public WitchPotion() {
        super(Quest.WITCH_POTION);
    }

    @Override
    public void onStart() {
        depositAllBanking.exchangeContext(getBot());
        HettyDialogueCompleter.exchangeContext(getBot());

        makeAllInterface = new MakeAllInterface(1);
        makeAllInterface.exchangeContext(getBot());
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
                    if (!HettyHouse.contains(myPosition())) {
                        setStatus("Walking to Location : Hettys House");
                        getWalking().webWalk(HettyHouse);
                    }
                    drinkPotion();
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

    private void drinkPotion() {
        getObjects().closest("Cauldron").interact("Drink from");
        Sleep.sleepUntil(() -> getDialogues().isPendingContinuation(), 5000, 600);
        getDialogues().clickContinue();
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
        if (!isAttacking("Rat")) {
            // Kill rat
            NPC Rat = getNpcs().closest(npc -> npc.getName().equals("Rat") && npc.isAttackable());
            if (Rat != null && Rat.interact("Attack")) {
                setStatus("Attacking NPC: Rat");
                Sleep.sleepUntil(() -> myPlayer().getInteracting() != null && myPlayer().getInteracting().getName().equals("Rat"), 5000, 600);
            }
        }
        Sleep.sleepUntil(() -> groundItems.closest("Rat's tail") != null && groundItems.closest("Rat's tail").exists(), 25000, 600);
        // loot Tail
        GroundItem tail = groundItems.closest("Rat's tail");
        if (tail != null && tail.exists()) {
            setStatus("Looting: Tail");
            tail.interact("Take");
            Sleep.sleepUntil(() -> getInventory().contains("Rat's tail"), 5000, 600);
        }
    }

    private void getBurntMeat() {
        if (!getInventory().contains("Raw rat meat") && !getInventory().contains("Cooked meat")) {
            if (!GiantRat.contains(myPosition())) {
                setStatus("Walking to Location : Giant Rat Location");
                getWalking().webWalk(GiantRat);
            }
            // Kill a Giant rat
            NPC GiantRat = getNpcs().closest(npc -> npc.getName().equals("Giant rat") && npc.isAttackable());
            if (GiantRat != null && GiantRat.interact("Attack")) {
                setStatus("Attacking NPC: Giant rat");
                Sleep.sleepUntil(() -> myPlayer().getInteracting() == null && myPlayer().getInteracting().getName().equals("Giant rat"), 5000, 600);
            }
            Sleep.sleepUntil(() -> groundItems.closest("Raw rat meat") != null && groundItems.closest("Raw rat meat").exists(), 80000, 600);
            GroundItem meat = groundItems.closest("Raw rat meat");
            // loot meat
            if (meat != null && meat.exists()) {
                setStatus("Looting: Raw rat meat");
                meat.interact("Take");
                Sleep.sleepUntil(() -> getInventory().contains("Raw rat meat"), 5000, 600);
            }
        }
        Area CookingSpot = new Area(2967, 3213, 2970, 3209);
        if (getInventory().contains("Raw rat meat")) {
            // go to cooking spot
            if (!CookingSpot.contains(myPosition())) {
                setStatus("Walking to Location : Giant Rat Location");
                getWalking().webWalk(CookingSpot);
            }
            cook("Raw rat meat");
            Sleep.sleepUntil(() -> getInventory().contains("Cooked meat"), 5000, 600);
        }
        if (getInventory().contains("Cooked meat")) {
            if (!CookingSpot.contains(myPosition())) {
                setStatus("Walking to Location : Giant Rat Location");
                getWalking().webWalk(CookingSpot);
            }
            cook("Cooked meat");
            Sleep.sleepUntil(() -> getInventory().contains("Burnt meat"), 5000, 600);
        }
    }

    private void getEyeOfNewt() {
        if (!MagicShop.contains(myPosition())) {
            setStatus("Walking to Location : Magic shop");
            getWalking().webWalk(MagicShop);
        }
        if (!getInventory().contains("Coins")) {
            //Get some money
        }
        NPC Betty = getNpcs().closest("Betty");
        if (Betty != null && Betty.isVisible()) {

            Betty.interact("Trade");
            Sleep.sleepUntil(() -> getStore().isOpen(), 5000, 600);
            getStore().buy("Eye of newt", 1);
            Sleep.sleepUntil(() -> getInventory().contains("Eye of newt"), 5000, 600);
        }
        ;
        // Buy an Eye of Newt
    }

    private void getOnion() {
        if (!OnionField.contains(myPosition())) {
            setStatus("Walking to Location : Onion Field ");
            getWalking().webWalk(OnionField);
        }
        if (Onion == null || !Onion.exists()) Onion = getObjects().closest(true, "Onion");
        if (Onion != null) {
            Onion.interact("Pick");
            Sleep.sleepUntil(() -> getInventory().contains("Onion"), 5000, 600);
        }
    }

    private boolean hasRequiredItems() {
        return Stream.of(ITEMS_NEEDED).allMatch(item -> getInventory().contains(item));
    }

    private boolean isAttacking(String NPC) {
        return myPlayer().getInteracting() != null && myPlayer().getInteracting().getName().equals(NPC);
    }

    private void cook(String Item) {

        if (!Item.equals(getInventory().getSelectedItemName())) {
            getInventory().getItem(Item).interact("Use");
        }
        if (getObjects().closest("Range").interact("Use")) {
            Sleep.sleepUntil(() -> makeAllInterface.isMakeAllScreenOpen(), 2000);
        }
        if (makeAllInterface.isMakeAllScreenOpen()) {
            if (makeAllInterface.makeAll()) {
                Sleep.sleepUntil(() -> getDialogues().isPendingContinuation(), 90_000);
            }
        }
    }

    @Override
    public Activity copy() {
        return new WitchPotion();
    }

}
