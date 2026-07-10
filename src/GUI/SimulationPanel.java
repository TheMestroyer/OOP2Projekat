package GUI;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimClock;
import Abstractions.SimData;
import Errors.SingletonNotInitialized;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimulationPanel extends JPanel {
    private SimData simData;
    public SimulationPanel(){
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
        simData = SimData.GetInstance();
        setBackground(UIConsts.BackgroundColor2);
        SimClock.GetInstance().SetOnTick(this::repaint);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        DrawAPIAWT.init(g);
        DrawAPIAWT api = DrawAPIAWT.getInstance();
        api.SetMapSize(getWidth(), getHeight());
        for(Airport a: simData.GetAirports()){
            a.draw();
        }
        for(Flight f: simData.GetFlights()){
            f.draw();
        }

    }

    private void handleClick(int screenX, int screenY) {
        try {
            DrawAPIAWT api = DrawAPIAWT.getInstance();
            api.SetMapSize(getWidth(), getHeight());

            Airport hit = null;
            for (Airport a : simData.GetAirports()) {
                int[] pos = api.MapPoint(a.getX(), a.getY(), Airport.DRAW_WIDTH, Airport.DRAW_HEIGHT);
                if(pos[0]<=screenX && pos[0]+Airport.DRAW_WIDTH>=screenX
                && pos[1]<=screenY && pos[1]+Airport.DRAW_HEIGHT>=screenY){
                    hit = a;
                    break;
                }
            }

            boolean deselecting = hit!=null && hit.getSelected();
            for (Airport a : simData.GetAirports()) {
                if(a!=hit) a.deselect();
            }
            if(hit!=null){
                if(deselecting) hit.deselect();
                else hit.select();
            }
        } catch (SingletonNotInitialized e) {
            System.out.println("Singleton not init");
        }
    }
}
