package Helpers;

import javax.swing.Timer;
import java.util.function.BooleanSupplier;

public class InactivityWatcher {
    public static final int WARNING_SECONDS = 55;
    public static final int TIMEOUT_SECONDS = 60;

    private static InactivityWatcher instance;

    public static InactivityWatcher GetInstance(){
        if(instance==null)instance = new InactivityWatcher();
        return instance;
    }

    private long lastActionMillis = System.currentTimeMillis();
    private boolean warned = false;
    private BooleanSupplier pausePredicate = () -> false;
    private Runnable onWarn;
    private Runnable onTimeout;

    private InactivityWatcher(){
        Timer timer = new Timer(1000, e -> checkIdle());
        timer.start();
    }

    public void SetPausePredicate(BooleanSupplier predicate){
        pausePredicate = predicate;
    }
    public void SetOnWarn(Runnable callback){
        onWarn = callback;
    }
    public void SetOnTimeout(Runnable callback){
        onTimeout = callback;
    }

    public void NotifyAction(){
        lastActionMillis = System.currentTimeMillis();
        warned = false;
    }

    private void checkIdle(){
        if(pausePredicate.getAsBoolean()){
            NotifyAction();
            return;
        }
        int idleSeconds = (int)((System.currentTimeMillis()-lastActionMillis)/1000);
        if(idleSeconds>=TIMEOUT_SECONDS){
            if(onTimeout!=null)onTimeout.run();
        } else if(idleSeconds>=WARNING_SECONDS && !warned){
            warned = true;
            if(onWarn!=null)onWarn.run();
        }
    }
}
