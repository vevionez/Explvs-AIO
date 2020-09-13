package gui.task_panels;

import activities.quests.*;
import gui.styled_components.StyledJComboBox;
import gui.styled_components.StyledJLabel;
import gui.styled_components.StyledJPanel;
import org.json.simple.JSONObject;
import tasks.QuestTask;
import tasks.Task;
import tasks.TaskType;

import javax.swing.*;
import java.awt.*;

public class QuestTaskPanel extends TaskPanel {

    private JComboBox<Quest> questSelector;

    QuestTaskPanel() {
        super(TaskType.QUEST);

        JPanel contentPanel = new StyledJPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        Box Requirements = new Box(BoxLayout.X_AXIS);
        Box OptionalRequirements = new Box(BoxLayout.X_AXIS);

        Box controls = new Box(BoxLayout.X_AXIS);

        final JLabel label1 = new StyledJLabel();
        label1.setText("Quest:");
        controls.add(label1);

        questSelector = new StyledJComboBox<>();
        controls.add(questSelector);

        JLabel questRequirements = new JLabel("Requirements:");
        JLabel QuestRequirement = new JLabel("None");
        Requirements.add(questRequirements);
        Requirements.add(QuestRequirement);
        JLabel questOptionalRequirements = new JLabel("Optional:");
        JLabel OptionalQuestRequirement = new JLabel("None");
        OptionalRequirements.add(questOptionalRequirements);
        OptionalRequirements.add(OptionalQuestRequirement);

        questSelector.setModel(new DefaultComboBoxModel<>(Quest.values()));
        questSelector.addActionListener(e -> {
            Requirements.removeAll();
            OptionalRequirements.removeAll();
            Quest selectedQuest = (Quest) questSelector.getSelectedItem();
            if(selectedQuest.getRequirements() != null){
                JLabel requirementLabel = new JLabel("Requirement: ");
                Requirements.add(requirementLabel);
                for (String requirement: selectedQuest.getRequirements()) {
                    JLabel label = new JLabel(requirement + " ");
                    Requirements.add(label);
                }
                Requirements.revalidate();
                Requirements.repaint();
            }else{
                JLabel label = new JLabel("Requirement: None");
                Requirements.add(label);
                Requirements.revalidate();
                Requirements.repaint();
            }
            if(selectedQuest.getOptionalRequirements() != null){
                JLabel optionalLabel = new JLabel("Optional: ");
                OptionalRequirements.add(optionalLabel);
                for (String optionalRequirement: selectedQuest.getOptionalRequirements()) {
                    JLabel label = new JLabel(optionalRequirement + " ");
                    OptionalRequirements.add(label);
                }
                OptionalRequirements.revalidate();
                OptionalRequirements.repaint();
            }else{
                JLabel label = new JLabel("Requirement: None");
                OptionalRequirements.add(label);
                OptionalRequirements.revalidate();
                OptionalRequirements.repaint();
            }
        });

        contentPanel.add(controls, BorderLayout.NORTH);
        contentPanel.add(Requirements, BorderLayout.CENTER);
        contentPanel.add(OptionalRequirements, BorderLayout.SOUTH);

        setContentPanel(contentPanel);
    }

    @Override
    public Task toTask() {
        switch ((Quest) questSelector.getSelectedItem()) {
            case SHEEP_SHEARER:
                return new QuestTask(new SheepShearer(), (Quest) questSelector.getSelectedItem());
            case RUNE_MYSTERIES:
                return new QuestTask(new RuneMysteries(), (Quest) questSelector.getSelectedItem());
            case COOKS_ASSISTANT:
                return new QuestTask(new CooksAssistant(), (Quest) questSelector.getSelectedItem());
            case ROMEO_AND_JULIET:
                return new QuestTask(new RomeoAndJuliet(), (Quest) questSelector.getSelectedItem());
            case THE_RESTLESS_GHOST:
                return new QuestTask(new TheRestlessGhost(), (Quest) questSelector.getSelectedItem());
            case WITCH_POTION:
                return new QuestTask(new WitchPotion(), (Quest) questSelector.getSelectedItem());
            case GOBLIN_DIPLOMACY:
                return new QuestTask(new GoblinDiplomacy(), (Quest) questSelector.getSelectedItem());
            case DORICS_QUEST:
                return new QuestTask(new DoricsQuest(), (Quest) questSelector.getSelectedItem());
        }
        return null;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", TaskType.QUEST.name());
        jsonObject.put("quest", ((Quest) questSelector.getSelectedItem()).name());
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject) {
        questSelector.setSelectedItem(Quest.valueOf((String) jsonObject.get("quest")));
    }
}
