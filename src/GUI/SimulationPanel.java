package GUI;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimClock;
import Abstractions.SimData;
import Helpers.CoordHelpers;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimulationPanel extends Panel {
    private SimData simData;
    public SimulationPanel(){
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int[] newCoords = CoordHelpers.SimPanelShift(e.getX(), e.getY());
                handleClick(newCoords[0],newCoords[1]);
            }
        });
        simData = SimData.GetInstance();
        setSize(new Dimension(360,180));
        SimClock.GetInstance().SetOnTick(this::repaint);
        for(Flight f: simData.GetFlights()){
            f.StartFlight();
        }
    }
    @Override
    public void paint(Graphics g) {
        DrawAPIAWT api = DrawAPIAWT.getInstance();
        DrawAPIAWT.init(this.getGraphics());
        api.TranslateRoot(180,90);
        for(Airport a: simData.GetAirports()){
            a.draw();
        }
        for(Flight f: simData.GetFlights()){
            f.draw();
        }

    }
    private void handleClick(int px, int py) {
        System.out.println("Clickerd on:"+px+"m"+py);
        System.out.println("Panel offset:"+this.getX()+"m"+this.getY());
        for (Airport a : simData.GetAirports()) {
            if(a.getX()<=px&&a.getX()+Airport.DRAW_WIDTH>=px
            && a.getY()<=py&&a.getY()+Airport.DRAW_HEIGHT>=py){
                a.select();
            }
            else{
                System.out.println("Clicked off airport: "+a.toString());
                a.deselect();
            }
        }
        repaint();
    }
}
