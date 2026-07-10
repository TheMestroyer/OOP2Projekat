package GUI;

import Abstractions.FlightManager;
import Abstractions.SimClock;
import Abstractions.SimData;
import Helpers.TimeHelpers;
import Helpers.TimeTracker;

import java.awt.*;

public class SimControlPanel extends Panel {
    private Label timeLabel;

    public SimControlPanel(Runnable onReset){
        setBackground(UIConsts.BackgroundColor1);
        setLayout(new FlowLayout(FlowLayout.LEFT,10,5));

        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button resetButton = new Button("Reset");
        startButton.setForeground(UIConsts.TextColor);
        pauseButton.setForeground(UIConsts.TextColor);
        resetButton.setForeground(UIConsts.TextColor);
        timeLabel = new Label();
        timeLabel.setForeground(UIConsts.TextColor);
        updateTimeLabel();

        startButton.addActionListener(e -> TimeTracker.GetInstance().Start());
        pauseButton.addActionListener(e -> TimeTracker.GetInstance().Stop());
        resetButton.addActionListener(e -> {
            TimeTracker.GetInstance().Reset();
            FlightManager.GetInstance().reset();
            for(var f : SimData.GetInstance().GetFlights()) f.resetFlight();
            updateTimeLabel();
            if(onReset != null) onReset.run();
        });

        add(startButton);
        add(pauseButton);
        add(resetButton);
        add(timeLabel);

        SimClock.GetInstance().SetOnTick(this::updateTimeLabel);
    }

    private void updateTimeLabel(){
        timeLabel.setText("Simulated time: "+TimeHelpers.FormatTime(TimeTracker.GetInstance().GetCurrentTime()));
    }
}
