package Abstractions;

import GUI.Drawable;
import Helpers.TimeTracker;

public class Flight extends Drawable implements Tickable {
    private final Airport startAirport,destinationAirport;
    private final int duration;
    private final int startTime;
    private boolean active;
    private boolean launched;
    private Airplane airplane;
    private int currentX, currentY;

    public Flight(Airport startAirport, Airport destinationAirport, int startTime, int duration){
        this.startAirport = startAirport;
        this.destinationAirport = destinationAirport;
        this.startTime = startTime;
        this.duration = duration;

        resetFlight();
    }

    public int GetStartTime(){
        return startTime;
    }
    public int GetDuration(){
        return duration;
    }
    public Airport GetStartAirport(){
        return startAirport;
    }
    public Airport GetDestinationAirport(){
        return destinationAirport;
    }
    public boolean IsLaunched(){
        return launched;
    }

    public void StartFlight(){
        active = true;
        launched = true;
        SimClock.GetInstance().register(this);
    }
    private void EndFlight(){
        active = false;
        SimClock.GetInstance().unregister(this);
    }

    public void resetFlight(){
        active = false;
        launched = false;
        currentX = startAirport.getX();
        currentY = startAirport.getY();
        SimClock.GetInstance().unregister(this);
    }

    private double Progress(){
        int elapsed = TimeTracker.GetInstance().GetCurrentTime()-startTime;
        double fraction = (double)elapsed/duration;
        if(fraction<0)return 0;
        if(fraction>1)return 1;
        return fraction;
    }

    private void UpdatePos(){
        double fraction = Progress();
        currentX = startAirport.getX()+(int)Math.round((destinationAirport.getX()-startAirport.getX())*fraction);
        currentY = startAirport.getY()+(int)Math.round((destinationAirport.getY()-startAirport.getY())*fraction);
        if(fraction>=1){
            EndFlight();
        }
    }

    @Override
    public void onTick() {
        if(active){
            UpdatePos();
        }
    }

    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public void draw(){
        if(airplane==null){
            airplane = new Airplane();
        }
        airplane.DrawAirplane(currentX,currentY,active);
    }
}
