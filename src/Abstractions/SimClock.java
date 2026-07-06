package Abstractions;

import javax.swing.Timer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimClock {
    private static SimClock inst = null;
    public static SimClock GetInstance(){
        if(inst==null) inst = new SimClock();
        return inst;
    }

    private final List<Tickable> tickables = new CopyOnWriteArrayList<>();
    private Runnable onTick;
    private final int tickPeriod = 500;

    private SimClock(){
        Timer clockTimer = new Timer(tickPeriod, e -> {
            for (Tickable t : tickables) t.onTick();
            if (onTick != null) onTick.run();
        });
        clockTimer.start();
    }

    public void SetOnTick(Runnable callback){
        onTick = callback;
    }
    public void register(Tickable t){
        tickables.add(t);
    }
    public void unregister(Tickable t){
        tickables.remove(t);
    }
}
