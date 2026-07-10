package Abstractions;

import Helpers.TimeTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightManager implements Tickable {
    private static final int SLOT_MINUTES = 10;

    static private FlightManager instance;

    private final Map<Airport,Integer> nextAvailableSlot = new HashMap<>();

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
        if(!TimeTracker.GetInstance().IsActive()) return;

        int currentTime = TimeTracker.GetInstance().GetCurrentTime();
        List<Airport> airports = SimData.GetInstance().GetAirports();
        List<Flight> flights = SimData.GetInstance().GetFlights();

        for(Airport airport : airports){
            if(nextAvailableSlot.getOrDefault(airport,0) > currentTime) continue;

            Flight next = null;
            for(Flight f : flights){
                if(f.IsLaunched() || f.GetStartAirport()!=airport) continue;
                if(f.GetStartTime() > currentTime) continue;
                if(next==null || f.GetStartTime() < next.GetStartTime()) next = f;
            }

            if(next != null){
                next.StartFlight();
                nextAvailableSlot.put(airport, currentTime + SLOT_MINUTES);
            }
        }
    }

    @Override
    public int getTickRate() {
        return 5;
    }

    public void reset(){
        nextAvailableSlot.clear();
    }
}
