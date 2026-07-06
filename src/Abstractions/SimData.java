package Abstractions;

import Errors.AirportExists;

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
        for( Airport x:airports){
            if(a.getName().compareTo(x.getName())==0) {
                System.out.println(a+"   "+x+"    "+a.getX()+"   "+x.getX());
                throw new AirportExists(AirportExists.SAME_NAME);
            }
            if(a.getId().compareTo(x.getId())==0)
                throw new AirportExists(AirportExists.SAME_ID);
            if((a.getX()>=x.getX()-Airport.DRAW_WIDTH&& a.getX()<=x.getX()+Airport.DRAW_WIDTH) &&
                    (a.getY()>=x.getY()-Airport.DRAW_HEIGHT&& a.getY()<=x.getY()+Airport.DRAW_HEIGHT))
                throw new AirportExists(x.getName());
        }
        airports.add(a);
    }
    public void addFlight(Flight f)   {
        flights.add(f);
    }
    public List<Airport> GetAirports(){
        return airports;
    }
    public List<Flight> GetFlights() {
        return flights;
    }

}
