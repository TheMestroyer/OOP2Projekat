package GUI;

import Abstractions.Airport;
import Abstractions.SimData;

import java.awt.*;
import java.util.List;

public class FilterPanel extends Panel {
    private Panel listPanel;

    public FilterPanel(){
        setBackground(UIConsts.BackgroundColor1);
        setPreferredSize(new Dimension(200,0));
        setLayout(new BorderLayout());

        listPanel = new Panel(new GridLayout(0,1));
        listPanel.setBackground(UIConsts.BackgroundColor1);

        ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
        scrollPane.setBackground(UIConsts.BackgroundColor1);
        scrollPane.add(listPanel);
        add(scrollPane, BorderLayout.CENTER);

        refresh();
    }

    public void refresh(){
        listPanel.removeAll();

        List<Airport> airports = SimData.GetInstance().GetAirports();
        for(Airport a : airports){
            Checkbox cb = new Checkbox(a.getId()+" - "+a.getName(), a.GetVisible());
            cb.setForeground(UIConsts.TextColor);
            cb.addItemListener(e -> a.SetVisible(cb.getState()));
            listPanel.add(cb);
        }

        listPanel.setSize(listPanel.getPreferredSize());
        validate();
        repaint();
    }
}
