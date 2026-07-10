package Helpers;

import Abstractions.SimClock;
import Abstractions.Tickable;

import javax.swing.Timer;
import java.awt.*;

public class TimeTracker implements Tickable {
    int currentTime=0;//Current time in minutes
    int timeIncrement = 10;//How much to increment every second
    private boolean active = false;

    static TimeTracker instance;
    //MUST BE REGISTERED FIRST FOR GOOD SYNCHRONIZATION
    static public void Init(){
        if(instance == null)instance = new TimeTracker();
    }
    static public TimeTracker GetInstance(){
        if(instance == null)instance = new TimeTracker();
        return instance;
    }

    public void Start(){
        active = true;
    }
    public void Stop(){
        active = false;
    }
    public boolean IsActive(){
        return active;
    }
    public void Reset(){
        active = false;
        currentTime = 0;
    }

    public int GetCurrentTime(){
        return currentTime;
    }
    public void SetCurrentTime(int time){
        currentTime = time;
    }

    private TimeTracker(){
        SimClock.GetInstance().register(this);
    }

    public void SetTimeIncrement(int timeIncrement){
        this.timeIncrement = timeIncrement;
    }

    @Override
    public void onTick() {
        //System.out.println("Trying tick on time tracker");
        if(active) {
            //System.out.println("Current time:"+currentTime);
            currentTime += timeIncrement;
        }
    }

    @Override
    public int getTickRate() {
        return 5;
    }


}
