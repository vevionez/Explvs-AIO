package activities.quests;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.utility.Condition;
import util.Executable;
import util.Sleep;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class DialogueCompleter extends Executable {

    private final String npcName;
    private final String[] dialogueOptions;
    private Area area;

    public DialogueCompleter(final String npcName, final Area area, final String... dialogueOptions) {
        this.npcName = npcName;
        this.area = area;
        this.dialogueOptions = dialogueOptions;
    }

    public DialogueCompleter(final String npcName, final String... dialogueOptions) {
        this.npcName = npcName;
        this.dialogueOptions = dialogueOptions;
    }

    @Override
    public void run() throws InterruptedException {
        NPC npc = getNpcs().closest(npcName);

        if (npc == null || !npc.isOnScreen()) {
            if (area != null && !area.contains(myPosition())) {
                WebWalkEvent webWalkEvent = new WebWalkEvent(area);
                webWalkEvent.setBreakCondition(new Condition() {
                    @Override
                    public boolean evaluate() {
                        NPC npc = getNpcs().closest(npcName);
                        return npc != null && npc.isOnScreen() && getMap().canReach(npc);
                    }
                });
                execute(webWalkEvent);
                return;
            } else {
                log(String.format("Could not find NPC with name '%s'", npcName));
                setFailed();
                return;
            }
        }

        if (!getMap().canReach(npc)) {
            getDoorHandler().handleNextObstacle(npc);
        } else if (!isChatting()) {
            log("Talking to NPC");
            if (npc.interact("Talk-to")) {
                Sleep.sleepUntil(() -> getDialogues().inDialogue() && myPlayer().isInteracting(npc), 5000);
            }
        } else {
            if (pendingContinue()) {
                sleep(random(200, 1500));
                log("Clicking Continue");
                selectContinue();
            } else if (pendingOption()) {
                sleep(random(200, 1500));
                log("Selecting Option");
                getDialogues().selectOption(dialogueOptions);
            }
        }
    }

    private boolean isChatting() {
        return getOptionWidget() != null && getOptionWidget().isVisible() ||
                getContinueWidget() != null && getContinueWidget().isVisible();
    }

    protected boolean pendingOption() {
        RS2Widget optionWidget = getOptionWidget();
        return optionWidget != null && optionWidget.isVisible();
    }

    protected boolean pendingContinue() {
        RS2Widget continueWidget = getContinueWidget();
        return continueWidget != null && continueWidget.isVisible();
    }

    protected boolean selectContinue() {
        RS2Widget continueWidget = getContinueWidget();
        if (continueWidget == null) {
            return false;
        }
        if (continueWidget.getMessage().contains("Click here to continue")) {
            getKeyboard().pressKey(KeyEvent.VK_SPACE);
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000, 600);
            return true;
        } else if (continueWidget.interact()) {
            Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000, 600);
            return true;
        }
        return false;
    }

    private RS2Widget getOptionWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(),
                widget -> widget.isVisible()
                        && (widget.getMessage().contains("Select an Option"))
        );
    }

    private RS2Widget getContinueWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(),
                widget -> widget.isVisible()
                        && (widget.getMessage().contains("Click here to continue")
                        || widget.getMessage().contains("Click to continue"))
        );
    }
}
