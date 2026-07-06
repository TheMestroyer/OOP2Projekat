package GUI;

import Abstractions.Airport;
import Abstractions.SimData;
import Errors.AirportExists;

import java.awt.*;

public class InspectorPanel extends Panel {
    private final Runnable onAirportCreated;

    private TextField idField;
    private TextField nameField;
    private TextField xField;
    private TextField yField;
    private Label statusLabel;

    public InspectorPanel(Runnable onAirportCreated){
        this.onAirportCreated = onAirportCreated;
        setBackground(UIConsts.BackgroundColor1);
        setPreferredSize(new Dimension(200,0));
        setupCreateAirportForm();
    }

    private void setupCreateAirportForm(){
        setLayout(new BorderLayout());

        Panel top = new Panel(new GridLayout(0,1,5,5));

        Panel form = new Panel(new GridLayout(4,2,5,5));
        idField = new TextField();
        nameField = new TextField();
        xField = new TextField();
        yField = new TextField();
        form.add(new Label("ID:"));
        form.add(idField);
        form.add(new Label("Name:"));
        form.add(nameField);
        form.add(new Label("X:"));
        form.add(xField);
        form.add(new Label("Y:"));
        form.add(yField);

        Button createButton = new Button("Add Airport");
        createButton.addActionListener(e -> handleCreateAirport());

        statusLabel = new Label(" ");
        statusLabel.setForeground(UIConsts.TextColor);

        top.add(form);
        top.add(createButton);
        top.add(statusLabel);

        add(top, BorderLayout.NORTH);
    }

    private void handleCreateAirport(){
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            SimData.GetInstance().addAirport(new Airport(id, name, x, y));

            idField.setText("");
            nameField.setText("");
            xField.setText("");
            yField.setText("");
            statusLabel.setText("Added airport " + id);

            if (onAirportCreated != null) onAirportCreated.run();
        } catch (NumberFormatException ex) {
            statusLabel.setText("X and Y must be whole numbers");
        } catch (AirportExists ex) {
            statusLabel.setText(ex.getMessage());
        }
    }
}
