package Abstractions;

import Errors.AirportExists;
import Errors.InvalidAirportData;
import Errors.InvalidFlightData;
import Errors.SingletonNotInitialized;
import GUI.DrawAPIAWT;
import Helpers.CoordHelpers;

import java.util.ArrayList;
import java.util.List;

public class SimData {
    private final List<Airport> airports = new ArrayList<>();
    private final List<Flight> flights = new ArrayList<>();
    private static SimData inst=null;
    public static SimData GetInstance(){
        if(inst==null)inst = new SimData();
        return inst;
    };
    private SimData(){

    }

    public void addAirport(Airport a) throws AirportExists {
        ValidateAirport(a, airports);
        airports.add(a);
    }
    public void addFlight(Flight f) {
        ValidateFlight(f);
        flights.add(f);
    }

    public static void ValidateAirport(Airport a, List<Airport> against) throws AirportExists {
        if(!a.getId().matches("[A-Z]{3}"))
            throw new InvalidAirportData(InvalidAirportData.BAD_CODE);
        if(a.getX()<CoordHelpers.MIN_X || a.getX()>CoordHelpers.MAX_X)
            throw new InvalidAirportData(InvalidAirportData.BAD_X);
        if(a.getY()<CoordHelpers.MIN_Y || a.getY()>CoordHelpers.MAX_Y)
            throw new InvalidAirportData(InvalidAirportData.BAD_Y);

        for( Airport x:against){
            if(a.getName().compareTo(x.getName())==0)
                throw new AirportExists(AirportExists.SAME_NAME);
            if(a.getId().compareTo(x.getId())==0)
                throw new AirportExists(AirportExists.SAME_ID);
            if(a.getX()==x.getX() && a.getY()==x.getY())
                throw new AirportExists(AirportExists.SAME_COORDS);
        }
    }

    public static void CheckOverlap(Airport a, List<Airport> against) throws AirportExists {
        try {
            DrawAPIAWT api = DrawAPIAWT.getInstance();
            int[] newPos = api.MapPoint(a.getX(), a.getY(), Airport.DRAW_WIDTH, Airport.DRAW_HEIGHT);
            for(Airport x : against){
                int[] pos = api.MapPoint(x.getX(), x.getY(), Airport.DRAW_WIDTH, Airport.DRAW_HEIGHT);
                if(CoordHelpers.RectanglesOverlap(newPos[0], newPos[1], Airport.DRAW_WIDTH, Airport.DRAW_HEIGHT,
                        pos[0], pos[1], Airport.DRAW_WIDTH, Airport.DRAW_HEIGHT))
                    throw new AirportExists(AirportExists.WOULD_OVERLAP);
            }
        } catch (SingletonNotInitialized e) {
            System.out.println("Singleton not init");
        }
    }
    public static void ValidateFlight(Flight f) {
        if(f.GetStartAirport()==f.GetDestinationAirport())
            throw new InvalidFlightData(InvalidFlightData.SAME_AIRPORT);
        if(f.GetDuration()<=0)
            throw new InvalidFlightData(InvalidFlightData.BAD_DURATION);
        if(f.GetStartTime()<0 || f.GetStartTime()>=24*60)
            throw new InvalidFlightData(InvalidFlightData.BAD_TIME);
    }
    
    public void ReplaceAll(List<Airport> newAirports, List<Flight> newFlights){
        Clear();
        airports.addAll(newAirports);
        flights.addAll(newFlights);
    }
    public List<Airport> GetAirports(){
        return airports;
    }
    public List<Flight> GetFlights() {
        return flights;
    }

    public boolean hasSelectedAirport(){
        for(Airport a : airports){
            if(a.getSelected()) return true;
        }
        return false;
    }

    public void Clear(){
        for(Flight f : flights){
            f.resetFlight();
        }
        airports.clear();
        flights.clear();
    }

}
