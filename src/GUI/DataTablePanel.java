package GUI;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimData;
import Helpers.TimeHelpers;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DataTablePanel extends Panel {
    private static final String AIRPORTS_TAB = "airports";
    private static final String FLIGHTS_TAB = "flights";

    private CardLayout cardLayout;
    private Panel cardsPanel;

    private DefaultTableModel airportTableModel;
    private DefaultTableModel flightTableModel;

    public DataTablePanel(){
        setBackground(UIConsts.BackgroundColor1);
        setPreferredSize(new Dimension(0,180));
        setupLayout();
        refresh();
    }

    private void setupLayout(){
        setLayout(new BorderLayout());

        Panel tabBar = new Panel(new GridLayout(1,2,5,5));
        Button airportsTabButton = new Button("Airports Table");
        Button flightsTabButton = new Button("Flights Table");
        airportsTabButton.setForeground(UIConsts.TextColor);
        flightsTabButton.setForeground(UIConsts.TextColor);
        airportsTabButton.addActionListener(e -> cardLayout.show(cardsPanel, AIRPORTS_TAB));
        flightsTabButton.addActionListener(e -> cardLayout.show(cardsPanel, FLIGHTS_TAB));
        tabBar.add(airportsTabButton);
        tabBar.add(flightsTabButton);

        airportTableModel = new DefaultTableModel(new Object[]{"Code","Name","X","Y"},0){
            @Override public boolean isCellEditable(int row, int column){ return false; }
        };
        flightTableModel = new DefaultTableModel(new Object[]{"From","To","Departure","Duration (min)"},0){
            @Override public boolean isCellEditable(int row, int column){ return false; }
        };

        JTable airportTable = new JTable(airportTableModel);
        JTable flightTable = new JTable(flightTableModel);

        cardLayout = new CardLayout();
        cardsPanel = new Panel(cardLayout);
        cardsPanel.add(new JScrollPane(airportTable), AIRPORTS_TAB);
        cardsPanel.add(new JScrollPane(flightTable), FLIGHTS_TAB);

        add(tabBar, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
    }

    public void refresh(){
        airportTableModel.setRowCount(0);
        for(Airport a : SimData.GetInstance().GetAirports()){
            airportTableModel.addRow(new Object[]{a.getId(), a.getName(), a.getX(), a.getY()});
        }

        flightTableModel.setRowCount(0);
        for(Flight f : SimData.GetInstance().GetFlights()){
            flightTableModel.addRow(new Object[]{
                    f.GetStartAirport().getId(),
                    f.GetDestinationAirport().getId(),
                    TimeHelpers.FormatTime(f.GetStartTime()),
                    f.GetDuration()
            });
        }
    }
}
