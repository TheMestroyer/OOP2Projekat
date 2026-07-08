package Abstractions;

import Helpers.TimeTracker;

import java.util.List;

public class FlightManager implements Tickable {
    static private FlightManager instance;


    //MUST BE REGISTERED AFTER TIME TRACKER FOR GOOD SYNCHRONIZATION
    static public FlightManager GetInstance(){
        if(instance == null)instance = new FlightManager();
        return instance;
    }
    private FlightManager(){
        SimClock.GetInstance().register(this);
    }



    @Override
    public void onTick() {
        List<Flight> flights = SimData.GetInstance().GetFlights();
        for(Flight f :flights){
            if(f.GetStartTime() == TimeTracker.GetInstance().GetCurrentTime()){
                f.StartFlight();
            }
        }
    }

    @Override
    public int getTickRate() {
        return 5;
    }
}
