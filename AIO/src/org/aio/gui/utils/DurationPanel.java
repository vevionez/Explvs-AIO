package org.aio.gui.utils;

import org.aio.gui.JSONSerializable;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DurationPanel extends JPanel implements JSONSerializable {

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private JComboBox<TimeType> timeTypeSelector;
    private DateTimePanel dateTimePanel;
    private JTextField durationField;

    public DurationPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        timeTypeSelector = new JComboBox<>(TimeType.values());
        add(timeTypeSelector);

        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        durationPanel.add(new JLabel("Duration (minutes):"));
        durationField = new JTextField();
        durationField.setColumns(6);
        ((AbstractDocument) durationField.getDocument()).setDocumentFilter(new NumberDocumentFilter());
        durationPanel.add(durationField);
        add(durationPanel);

        dateTimePanel = new DateTimePanel();
        dateTimePanel.setVisible(false);
        add(dateTimePanel);

        timeTypeSelector.addActionListener(e -> {
            TimeType selectedTimeType = (TimeType) timeTypeSelector.getSelectedItem();
            if (selectedTimeType == TimeType.DURATION) {
                dateTimePanel.setVisible(false);
                durationPanel.setVisible(true);
            } else {
                durationPanel.setVisible(false);
                dateTimePanel.setVisible(true);
            }
        });
    }

    public TimeType getSelectedTimeType() {
        return (TimeType) timeTypeSelector.getSelectedItem();
    }

    public int getDuration() {
        return Integer.parseInt(durationField.getText());
    }

    public LocalDateTime getSelectedDateTime() {
        return dateTimePanel.getDateTime();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        if (timeTypeSelector.getSelectedItem() == TimeType.DURATION) {
            jsonObject.put("duration", durationField.getText());
        } else {
            jsonObject.put(
                    "datetime",
                    dateTimePanel.getDateTime().format(dtFormatter)
            );
        }

        return jsonObject;
    }

    @Override
    public void fromJSON(final JSONObject jsonObject) {
        if (jsonObject.containsKey("duration")) {
            durationField.setText((String) jsonObject.get("duration"));
            timeTypeSelector.setSelectedItem(TimeType.DURATION);
        }

        if (jsonObject.containsKey("datetime")) {
            String datetimeStr = (String) jsonObject.get("datetime");
            LocalDateTime datetime = LocalDateTime.parse(
                    datetimeStr,
                    dtFormatter
            );
            dateTimePanel.setDateTime(datetime);
            timeTypeSelector.setSelectedItem(TimeType.DATE_TIME);
        }
    }

    public enum TimeType {
        DURATION("Duration"),
        DATE_TIME("Date / Time");

        private String name;

        TimeType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}