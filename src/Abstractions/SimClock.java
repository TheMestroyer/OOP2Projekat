package Abstractions;

import javax.swing.Timer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimClock {
    private static SimClock inst = null;
    private final int tickPeriod = 200;

    public static void Init(){
        if(inst == null)inst = new SimClock();
    }

    public static SimClock GetInstance(){
        Init();
        return inst;
    }
    private final List<Tickable> tickables = new CopyOnWriteArrayList<>();
    private final Map<Tickable,Integer> counters = new ConcurrentHashMap<>();
    private Runnable onTick;

    private SimClock(){
        Timer clockTimer = new Timer(tickPeriod, e -> {
            for (Tickable t : tickables) {
                int count = counters.merge(t, 1, Integer::sum);
                if (count >= t.getTickRate()) {
                    t.onTick();
                    counters.put(t, 0);
                }
            }
            if (onTick != null) onTick.run();
        });
        clockTimer.start();
    }

    public void register(Tickable t){
        tickables.add(t);
        counters.put(t, 0);
    }
    public void unregister(Tickable t){
        tickables.remove(t);
        counters.remove(t);
    }


    public void SetOnTick(Runnable callback){
        onTick = callback;
    }

}
