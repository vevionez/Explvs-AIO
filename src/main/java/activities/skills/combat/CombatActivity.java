package activities.skills.combat;

import activities.activity.Activity;
import activities.activity.ActivityType;
import activities.banking.ItemReqBanking;
import activities.eating.Eating;
import activities.eating.Food;
import activities.grand_exchange.GEMode;
import activities.grand_exchange.item_guide.ItemGuide;
import activities.grand_exchange.price_guide.OSRSPriceGuide;
import activities.grand_exchange.price_guide.RSBuddyPriceGuide;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import util.Executable;
import util.Location;
import util.RSUnits;
import util.Sleep;
import util.item_requirement.ItemReq;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CombatActivity extends Activity {
    private final util.Location location;
    private final Npc npc;
    private Food food;
    private Eating eatNode;
    private int hpPercentToEatAt;
    private Executable bankNode;
    private Item[] lootList;
    private final ItemReq[] itemReqs;
    private Quests.Quest cook = Quests.Quest.COOKS_ASSISTANT;
    public CombatActivity(Npc npc, Location location, Food food, int hpPercentToEatAt) {
        super(ActivityType.COMBAT);
        this.location = location;
        this.npc = npc;
        this.food = food;
        this.hpPercentToEatAt = hpPercentToEatAt;
        //TODO: Allow setting of Loot List through GUI

        itemReqs = new ItemReq[]{
                new ItemReq(this.food.toString(), 5, 24)
        };
        String[] itemsToLoot = {"Death rune", "Blood rune", "Law rune", "Mithril ore", "Pure essence", "Adamant arrow",
                "Ranarr seed", "Torstol seed", "Snapdragon seed", "Bones"};


    }

    @Override
    public void onStart() {
        if (food != null) {
            eatNode = new Eating(food);
            eatNode.exchangeContext(getBot());
        }
        bankNode = new ItemReqBanking(itemReqs);
        bankNode.exchangeContext(getBot());
    }

    @Override
    public void runActivity() throws InterruptedException {
        if (isOutOfFood()) {
            if (getBank() != null && getBank().isOpen()) {
                getBank().close();
            } else {
                setStatus("Getting more food from Bank");
                bankNode.run();
                if (bankNode.hasFailed()) {
                    setFailed();
                }
            }
        } else if (!location.getArea().contains(myPosition())) {
            setStatus("Walking to Location : " + location.getName());
            getWalking().webWalk(location.getArea());
        } else if (food != null && eatNode.getHpPercent() < hpPercentToEatAt) {
            setStatus("Eating Food : " + food.toString());
            eatNode.run();
        } else if (getBank() != null && getBank().isOpen()) {
            getBank().close();
        } /* else if (isNearGroundItemWithValue() && !inventory.isFull()) {
            setStatus("Picking up a valuable item!");
            pickUpValuableItems();
        }*/ else {
            if (!isAttacking(npc)) {
                setStatus("Looking for NPC to Attack");
                attackNpc(npc);
            }
        }
    }

    @Override
    public CombatActivity copy() {
        return new CombatActivity(npc, location, food, hpPercentToEatAt);
    }

    private void attackNpc(Npc attackNpc) {
        //noinspection unchecked
        NPC attackMe = getNpcs().closest(npc -> npc.getName().equals(attackNpc.name) && npc.isAttackable());
        if (attackMe != null && attackMe.interact("Attack")) {
            setStatus("Attacking NPC: " + attackNpc.name);
            Sleep.sleepUntil(() -> myPlayer().getInteracting() != null, 5000, 600);
        }
    }

    private boolean isAttacking(Npc attackNpc) {
        return myPlayer().getInteracting() != null && myPlayer().getInteracting().getName().equals(attackNpc.name);
    }

    private boolean isOutOfFood() {
        return !getInventory().contains(food.toString());
    }

    private boolean isNearGroundItemWithValue() throws InterruptedException {
        List<GroundItem> items = groundItems.getAll();
        for (GroundItem item : items) {
            int amount = item.getAmount();
            final Optional<Integer> price = getPrice(item.getName());
            if (price.isPresent()) {
                log("Looked up item: " + item.getName()+ " - Price: " + RSUnits.valueToFormatted(price.get()) + "- ID: " + item.getId());
            } else {
                sleep(1);
            }
/*            int value = amount * price;
            if (value > 50) {
                return true;
            }*/
        }

        return false;
    }

    private void pickUpValuableItems() {
        List<GroundItem> items = groundItems.getAll();
        for (GroundItem item : items) {
            int amount = item.getAmount();
            int price = getPrice(item.getName()).orElse(0);
            log("Looked up item while Looting: " + item.getName()+ "-" + price);
            int value = amount * price;
            if (value > 50) {
                if (!inventory.isFull() && getGroundItems().closest(item.getName()) != null) {

                    if(getGroundItems().closest(item.getName()).interact("Take")){
                        Sleep.sleepUntil(() -> {
                            return getGroundItems().closest(item.getName()) == null;
                        }, 5000);

                    }
                }
            }
        }
    }

    private Optional<Integer> getPrice(final String itemName) {
        Map<String, Integer> allGEItems = ItemGuide.getAllGEItems();

        if (!allGEItems.containsKey(itemName)) {
            return Optional.empty();
        }

        final int itemID = allGEItems.get(itemName);

        Optional<Integer> price;
        price = RSBuddyPriceGuide.getSellPrice(itemID);

        if (!price.isPresent()) {
            price = OSRSPriceGuide.getPrice(itemID);
        }

        return price;
    }
}
