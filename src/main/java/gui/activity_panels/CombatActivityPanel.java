package gui.activity_panels;

import activities.eating.Food;
import activities.skills.combat.CombatActivity;
import activities.skills.combat.Npc;
import gui.fields.IntegerField;
import gui.styled_components.StyledJCheckBox;
import gui.styled_components.StyledJComboBox;
import gui.styled_components.StyledJLabel;
import gui.styled_components.StyledJPanel;
import org.json.simple.JSONObject;
import util.Location;

import javax.swing.*;
import java.awt.*;

public class CombatActivityPanel implements ActivityPanel{

    private JPanel mainPanel;
    private Box npcRow;
    private Box foodRow;
    private Box lootRow;
    private JComboBox<Npc> npcSelector;
    private JComboBox<Location> locationSelector;
    private JComboBox<Food> foodSelector;
    private JTextField hpPercentField;
    private StyledJCheckBox lootCheckBox;

    public CombatActivityPanel() {
        mainPanel = new StyledJPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        npcRow = new Box(BoxLayout.X_AXIS);
        foodRow = new Box(BoxLayout.X_AXIS);
        lootRow = new Box(BoxLayout.X_AXIS);

        final JLabel npcLabel = new StyledJLabel("NPC:");
        npcRow.add(npcLabel);

        npcSelector = new StyledJComboBox<>();
        npcRow.add(npcSelector);

        final JLabel locationLabel = new StyledJLabel("Location:");
        npcRow.add(locationLabel);

        locationSelector = new StyledJComboBox<>();
        npcRow.add(locationSelector);

        final JLabel foodLabel = new StyledJLabel("Food:");
        foodRow.add(foodLabel);

        foodSelector = new StyledJComboBox<>();
        foodRow.add(foodSelector);

        JLabel hpPercentLabel = new StyledJLabel();
        hpPercentLabel.setText("HP % To Eat:");
        foodRow.add(hpPercentLabel);

        hpPercentField = new IntegerField();
        hpPercentField.setColumns(2);
        hpPercentField.setEditable(true);
        hpPercentField.setEnabled(true);
        foodRow.add(hpPercentField);

        lootCheckBox = new StyledJCheckBox("Loot Items");
        lootCheckBox.setSelected(false);
        lootRow.add(lootCheckBox);

        mainPanel.add(npcRow);
        mainPanel.add(foodRow);
        mainPanel.add(lootRow);


        npcSelector.setModel(new DefaultComboBoxModel<>(Npc.values()));
        locationSelector.setModel(new DefaultComboBoxModel<>(Npc.values()[0].locations));
        foodSelector.setModel(new DefaultComboBoxModel<>(Food.values()));
        hpPercentField.setVisible(false);
        hpPercentLabel.setVisible(false);
        foodSelector.addActionListener(e -> {
            Food selectedFood = (Food) foodSelector.getSelectedItem();
            if (selectedFood == Food.NONE) {
                hpPercentLabel.setVisible(false);
                hpPercentField.setVisible(false);
            } else {
                hpPercentLabel.setVisible(true);
                hpPercentField.setVisible(true);
            }
        });
        npcSelector.addActionListener(e -> {
            Npc selectedNpc = (Npc) npcSelector.getSelectedItem();
            locationSelector.setModel(new DefaultComboBoxModel<>(selectedNpc.locations));
        });
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public CombatActivity toActivity() {
        return new CombatActivity(
                (Npc) npcSelector.getSelectedItem(),
                (Location) locationSelector.getSelectedItem(),
                (Food) foodSelector.getSelectedItem(),
                Integer.parseInt(hpPercentField.getText())
        );

    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Location", ((Npc) locationSelector.getSelectedItem()).name());
        return jsonObject;
    }

    @Override
    public void fromJSON(JSONObject jsonObject) {
        locationSelector.setSelectedItem(Npc.valueOf((String) jsonObject.get("Location")));
    }
}
