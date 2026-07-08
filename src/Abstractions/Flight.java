package Abstractions;

import Errors.SingletonNotInitialized;
import GUI.DrawAPIAWT;
import GUI.Drawable;
import GUI.UIConsts;
import Helpers.CoordHelpers;

import java.awt.*;

public class Flight extends Drawable implements Tickable {
    private final Airport startAirport,destinationAirport;
    private final int duration;
    private final int startTime;
    private boolean active;
    private Airplane airplane;
    private int currentX, currentY;
    private int directionX, directionY;//-1 left/up, 0 neutral, +1 right/down
    public Flight(Airport startAirport, Airport destinationAirport, int startTime, int duration){
        this.startAirport = startAirport;
        this.destinationAirport = destinationAirport;
        this. startTime = startTime;
        this.duration = duration;

        directionX = startAirport.getX()>=destinationAirport.getX()?-1:1;
        directionY = startAirport.getY()>=destinationAirport.getY()?-1:1;
        if(startAirport.getX()==destinationAirport.getX())directionX = 0;
        if(startAirport.getY()==destinationAirport.getY())directionY = 0;

        EndFlight();
    }

    public int GetStartTime(){
        return startTime;
    }

    public void StartFlight(){
        active = true;
        SimClock.GetInstance().register(this);
        System.out.println("registered tick on flight");
    }
    private void EndFlight(){
        active = false;
        currentX = startAirport.getX()+Airport.DRAW_WIDTH/2;
        currentY = startAirport.getY()+Airport.DRAW_HEIGHT/2;
        SimClock.GetInstance().unregister(this);
        System.out.println("unregistered tick on flight");
    }

    private boolean HasReachedEnd(){
        if(directionX!=0&& (directionX*currentX<directionX *(destinationAirport.getX()+Airport.DRAW_WIDTH/2))){
            return false;
        }
        if(directionY!=0&& (directionY*currentY<directionY *(destinationAirport.getY()+Airport.DRAW_HEIGHT/2))){
            return false;
        }
        return true;
    }

    private int[]  NextCoords(){
        int distanceX = directionX*CoordHelpers.CalculateStraightDistance(startAirport.getX(), destinationAirport.getX());
        int distanceY = directionY*CoordHelpers.CalculateStraightDistance(startAirport.getY(), destinationAirport.getY());
        int stepX = distanceX/duration;
        int stepY = distanceY/duration;
        int nextX = currentX+stepX;
        int nextY = currentY+stepY;
        return new int[]{nextX,nextY};
    }

    private void UpdatePos(){
        int[] newCoords = NextCoords();
        currentX = newCoords[0];
        currentY = newCoords[1];
        if(HasReachedEnd()){
            EndFlight();
        }
    }
    @Override
    public void onTick() {
        System.out.println("Called tick on flight");
        if(active){
            UpdatePos();
        }
    }

    @Override
    public int getTickRate() {
        return 5;
    }

    @Override
    public void draw(){
        if(airplane==null){
            airplane = new Airplane();
        }
        airplane.DrawAirplane(currentX,currentY,active);
    }
}
