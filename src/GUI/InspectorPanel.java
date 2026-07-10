package GUI;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimData;
import Errors.AirportExists;
import Errors.InvalidAirportData;
import Errors.InvalidFlightData;
import Helpers.TimeHelpers;

import java.awt.*;
import java.util.List;

public class InspectorPanel extends Panel {
    private static final String AIRPORTS_TAB = "airports";
    private static final String FLIGHTS_TAB = "flights";

    private final Runnable onDataChanged;

    private CardLayout cardLayout;
    private Panel cardsPanel;

    private TextField idField;
    private TextField nameField;
    private TextField xField;
    private TextField yField;
    private TextArea airportStatusLabel;

    private Choice startAirportChoice;
    private Choice destinationAirportChoice;
    private TextField startTimeField;
    private TextField durationField;
    private TextArea flightStatusLabel;
    private List<Airport> flightAirportOptions;

    public InspectorPanel(Runnable onDataChanged){
        this.onDataChanged = onDataChanged;
        setBackground(UIConsts.BackgroundColor1);
        setPreferredSize(new Dimension(200,0));
        setupLayout();
    }

    private void setupLayout(){
        setLayout(new BorderLayout());

        Panel tabBar = new Panel(new GridLayout(1,2,5,5));
        Button airportsTabButton = new Button("Airports");
        Button flightsTabButton = new Button("Flights");
        airportsTabButton.setForeground(UIConsts.TextColor);
        flightsTabButton.setForeground(UIConsts.TextColor);
        airportsTabButton.addActionListener(e -> showTab(AIRPORTS_TAB));
        flightsTabButton.addActionListener(e -> showTab(FLIGHTS_TAB));
        tabBar.add(airportsTabButton);
        tabBar.add(flightsTabButton);

        cardLayout = new CardLayout();
        cardsPanel = new Panel(cardLayout);
        cardsPanel.add(buildAirportForm(), AIRPORTS_TAB);
        cardsPanel.add(buildFlightForm(), FLIGHTS_TAB);

        add(tabBar, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
    }

    private Label textLabel(String text){
        Label label = new Label(text);
        label.setForeground(UIConsts.TextColor);
        return label;
    }

    private TextArea statusArea(){
        TextArea area = new TextArea("", 3, 20, TextArea.SCROLLBARS_NONE);
        area.setEditable(false);
        area.setBackground(UIConsts.BackgroundColor1);
        area.setForeground(UIConsts.TextColor);
        return area;
    }

    private void showTab(String tab){
        if(FLIGHTS_TAB.equals(tab)) refreshAirportChoices();
        cardLayout.show(cardsPanel, tab);
    }

    private Panel buildAirportForm(){
        Panel top = new Panel(new GridLayout(0,1,5,5));

        Panel form = new Panel(new GridLayout(4,2,5,5));
        idField = new TextField();
        nameField = new TextField();
        xField = new TextField();
        yField = new TextField();
        form.add(textLabel("ID:"));
        form.add(idField);
        form.add(textLabel("Name:"));
        form.add(nameField);
        form.add(textLabel("X:"));
        form.add(xField);
        form.add(textLabel("Y:"));
        form.add(yField);

        Button createButton = new Button("Add Airport");
        createButton.setForeground(UIConsts.TextColor);
        createButton.addActionListener(e -> handleCreateAirport());

        airportStatusLabel = statusArea();

        top.add(form);
        top.add(createButton);
        top.add(airportStatusLabel);

        return top;
    }

    private Panel buildFlightForm(){
        Panel top = new Panel(new GridLayout(0,1,5,5));

        Panel form = new Panel(new GridLayout(4,2,5,5));
        startAirportChoice = new Choice();
        destinationAirportChoice = new Choice();
        startTimeField = new TextField();
        durationField = new TextField();
        form.add(textLabel("From:"));
        form.add(startAirportChoice);
        form.add(textLabel("To:"));
        form.add(destinationAirportChoice);
        form.add(textLabel("Departure (HH:MM):"));
        form.add(startTimeField);
        form.add(textLabel("Duration (min):"));
        form.add(durationField);

        Button createButton = new Button("Add Flight");
        createButton.setForeground(UIConsts.TextColor);
        createButton.addActionListener(e -> handleCreateFlight());

        flightStatusLabel = statusArea();

        top.add(form);
        top.add(createButton);
        top.add(flightStatusLabel);

        refreshAirportChoices();

        return top;
    }

    private void refreshAirportChoices(){
        flightAirportOptions = SimData.GetInstance().GetAirports();

        String previousStart = startAirportChoice.getItemCount() > 0 ? startAirportChoice.getSelectedItem() : null;
        String previousDestination = destinationAirportChoice.getItemCount() > 0 ? destinationAirportChoice.getSelectedItem() : null;

        startAirportChoice.removeAll();
        destinationAirportChoice.removeAll();
        for(Airport a : flightAirportOptions){
            String label = a.getId() + " - " + a.getName();
            startAirportChoice.add(label);
            destinationAirportChoice.add(label);
        }

        selectIfPresent(startAirportChoice, previousStart);
        selectIfPresent(destinationAirportChoice, previousDestination);
    }

    private void selectIfPresent(Choice choice, String value){
        if(value == null) return;
        for(int i=0;i<choice.getItemCount();i++){
            if(choice.getItem(i).equals(value)){
                choice.select(i);
                return;
            }
        }
    }

    private void handleCreateAirport(){
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            Airport newAirport = new Airport(id, name, x, y);
            SimData.CheckOverlap(newAirport, SimData.GetInstance().GetAirports());
            SimData.GetInstance().addAirport(newAirport);

            idField.setText("");
            nameField.setText("");
            xField.setText("");
            yField.setText("");
            airportStatusLabel.setText("Added airport " + id);

            if (onDataChanged != null) onDataChanged.run();
        } catch (NumberFormatException ex) {
            airportStatusLabel.setText("X and Y must be whole numbers");
        } catch (AirportExists | InvalidAirportData ex) {
            airportStatusLabel.setText(ex.getMessage());
        }
    }

    private void handleCreateFlight(){
        if(flightAirportOptions == null || flightAirportOptions.size() < 2){
            flightStatusLabel.setText("Add at least two airports first");
            return;
        }
        int startIndex = startAirportChoice.getSelectedIndex();
        int destinationIndex = destinationAirportChoice.getSelectedIndex();
        try {
            int startTime = TimeHelpers.ParseTime(startTimeField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            Airport startAirport = flightAirportOptions.get(startIndex);
            Airport destinationAirport = flightAirportOptions.get(destinationIndex);

            SimData.GetInstance().addFlight(new Flight(startAirport, destinationAirport, startTime, duration));

            startTimeField.setText("");
            durationField.setText("");
            flightStatusLabel.setText("Added flight " + startAirport.getId() + " -> " + destinationAirport.getId());

            if (onDataChanged != null) onDataChanged.run();
        } catch (NumberFormatException ex) {
            flightStatusLabel.setText("Duration must be a whole number");
        } catch (InvalidFlightData ex) {
            flightStatusLabel.setText(ex.getMessage());
        }
    }
}
