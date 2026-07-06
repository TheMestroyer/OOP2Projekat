package Helpers;

import javax.swing.Timer;
import java.awt.*;

public class TimeSetter {
    private int targetFramerate = 24;
    private final Frame frameToRefresh;
    private final Timer timer;

    public TimeSetter(Frame frameToRefresh){
        this.frameToRefresh = frameToRefresh;
        timer = new Timer(1000/targetFramerate, e -> frameToRefresh.repaint());
    }

    public void SetTargetFramerate(int val){
        targetFramerate = val;
        timer.setDelay(1000/targetFramerate);
    }

    public void start(){
        timer.start();
    }

    public void stop(){
        timer.stop();
    }
}
