package GUI;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimData;
import Helpers.TimeSetter;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Frame {
    private InspectorPanel inspectorPanel;
    private SimulationPanel simPanel;

    private void SetupPanels(){
        this.setVisible(true);

        SimData sd = SimData.GetInstance();
        System.out.println("a");

        sd.addAirport(new Airport("aaa","dds",59,50));
        sd.addAirport(new Airport("aba","dda",100,100));
        sd.addAirport(new Airport("abad","ddah",180,-80));
        sd.addAirport(new Airport("abaa","ddaf",180,80));


        sd.addFlight(new Flight(sd.GetAirports().get(0),sd.GetAirports().get(1),10,10));
        sd.addFlight(new Flight(sd.GetAirports().get(2),sd.GetAirports().get(3),10,5));

        simPanel = new SimulationPanel();
        simPanel.setVisible(true);
        inspectorPanel = new InspectorPanel(simPanel::repaint);
        inspectorPanel.setVisible(true);
    }
    @Override
    public void paint(Graphics g) {
        DrawAPIAWT.init(g);
        simPanel.repaint();
    }

    private void SetupLayout(){
        SetupPanels();

        setName("BASEM (Best airplane sim ever made)");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setSize(new Dimension(1280,720));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        c.weightx = 1.0;
        c.gridx = 0;
        add(simPanel,c);
        c.weightx = 0.0;
        c.gridx = 1;
        add(inspectorPanel,c);
    }

    public App(){
        super();
        SetupLayout();
        System.out.println("bbbb");
        setVisible(true);

//        TimeSetter ts = new TimeSetter(this);
//        ts.start();
    }
}
