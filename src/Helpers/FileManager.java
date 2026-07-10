package Helpers;

import Abstractions.Airport;
import Abstractions.Flight;
import Abstractions.SimData;
import Errors.FileFormatException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FileManager {
    private static final String AIRPORT_HEADER = "CODE,NAME,X,Y";
    private static final String FLIGHT_HEADER  = "FROM,TO,DEPARTURE,DURATION";

    private FileManager(){}

    public static void SaveCsv(File file){
        List<Airport> airports = SimData.GetInstance().GetAirports();
        List<Flight> flights = SimData.GetInstance().GetFlights();

        StringBuilder sb = new StringBuilder();
        sb.append("# AIRPORTS\n").append(AIRPORT_HEADER).append("\n");
        for(Airport a: airports){
            sb.append(a.getId()).append(',').append(a.getName()).append(',')
              .append(a.getX()).append(',').append(a.getY()).append('\n');
        }
        sb.append("\n# FLIGHTS\n").append(FLIGHT_HEADER).append("\n");
        for(Flight f: flights){
            sb.append(f.GetStartAirport().getId()).append(',').append(f.GetDestinationAirport().getId()).append(',')
              .append(TimeHelpers.FormatTime(f.GetStartTime())).append(',').append(f.GetDuration()).append('\n');
        }

        writeFile(file, sb.toString());
    }

    public static void LoadCsv(File file){
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException ex){
            throw new FileFormatException("Could not open file '"+file.getName()+"'. Check that it exists and is readable.");
        }

        List<Airport> stagedAirports = new ArrayList<>();
        List<Flight> stagedFlights = new ArrayList<>();
        Map<String,Airport> byCode = new HashMap<>();

        String section = null;
        boolean headerExpected = false;

        for(int i=0;i<lines.size();i++){
            int lineNum = i+1;
            String line = lines.get(i).trim();
            if(line.isEmpty()) continue;

            if(line.startsWith("#")){
                String tag = line.substring(1).trim().toUpperCase();
                if(tag.equals("AIRPORTS")){ section = "AIRPORTS"; headerExpected = true; }
                else if(tag.equals("FLIGHTS")){ section = "FLIGHTS"; headerExpected = true; }
                else throw new FileFormatException("Row "+lineNum+": unknown section '"+line+"'. Expected '# AIRPORTS' or '# FLIGHTS'.");
                continue;
            }

            if(section==null)
                throw new FileFormatException("Row "+lineNum+": expected a '# AIRPORTS' or '# FLIGHTS' section header before any data.");

            if(headerExpected){
                String expected = section.equals("AIRPORTS") ? AIRPORT_HEADER : FLIGHT_HEADER;
                if(!line.equalsIgnoreCase(expected))
                    throw new FileFormatException("Row "+lineNum+": file does not contain the expected columns ("+expected+"). Check the format or provide a new file.");
                headerExpected = false;
                continue;
            }

            String[] parts = line.split(",", -1);
            if(section.equals("AIRPORTS")){
                if(parts.length!=4)
                    throw new FileFormatException("Row "+lineNum+": expected 4 columns ("+AIRPORT_HEADER+") but found "+parts.length+".");
                String code = parts[0].trim();
                String name = parts[1].trim();
                int x,y;
                try {
                    x = Integer.parseInt(parts[2].trim());
                    y = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException ex){
                    throw new FileFormatException("Row "+lineNum+": X and Y must be whole numbers.");
                }

                Airport a = new Airport(code,name,x,y);
                validateAirport(a, stagedAirports, "Row "+lineNum);
                stagedAirports.add(a);
                byCode.put(code,a);
            } else {
                if(parts.length!=4)
                    throw new FileFormatException("Row "+lineNum+": expected 4 columns ("+FLIGHT_HEADER+") but found "+parts.length+".");
                Flight f = resolveFlight(byCode, parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), "Row "+lineNum);
                stagedFlights.add(f);
            }
        }

        SimData.GetInstance().ReplaceAll(stagedAirports, stagedFlights);
    }

    public static void SaveJson(File file){
        List<Airport> airports = SimData.GetInstance().GetAirports();
        List<Flight> flights = SimData.GetInstance().GetFlights();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n\"airports\":[\n");
        for(int i=0;i<airports.size();i++){
            Airport a = airports.get(i);
            sb.append("{\"code\":\"").append(MiniJson.EscapeString(a.getId())).append("\",")
              .append("\"name\":\"").append(MiniJson.EscapeString(a.getName())).append("\",")
              .append("\"x\":").append(a.getX()).append(",\"y\":").append(a.getY()).append("}");
            sb.append(i<airports.size()-1 ? ",\n" : "\n");
        }
        sb.append("],\n\"flights\":[\n");
        for(int i=0;i<flights.size();i++){
            Flight f = flights.get(i);
            sb.append("{\"from\":\"").append(f.GetStartAirport().getId()).append("\",")
              .append("\"to\":\"").append(f.GetDestinationAirport().getId()).append("\",")
              .append("\"departure\":\"").append(TimeHelpers.FormatTime(f.GetStartTime())).append("\",")
              .append("\"duration\":").append(f.GetDuration()).append("}");
            sb.append(i<flights.size()-1 ? ",\n" : "\n");
        }
        sb.append("]\n}\n");

        writeFile(file, sb.toString());
    }

    @SuppressWarnings("unchecked")
    public static void LoadJson(File file){
        String text;
        try {
            text = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException ex){
            throw new FileFormatException("Could not open file '"+file.getName()+"'. Check that it exists and is readable.");
        }

        Object root = MiniJson.Parse(text);
        if(!(root instanceof Map))
            throw new FileFormatException("The JSON file must contain an object with 'airports' and 'flights' arrays.");
        Map<String,Object> rootMap = (Map<String,Object>) root;

        Object airportsRaw = rootMap.get("airports");
        Object flightsRaw = rootMap.get("flights");
        if(!(airportsRaw instanceof List) || !(flightsRaw instanceof List))
            throw new FileFormatException("The JSON file does not contain the expected 'airports' and 'flights' arrays. Check the format or provide a new file.");

        List<Airport> stagedAirports = new ArrayList<>();
        List<Flight> stagedFlights = new ArrayList<>();
        Map<String,Airport> byCode = new HashMap<>();

        List<Object> airportsList = (List<Object>) airportsRaw;
        for(int i=0;i<airportsList.size();i++){
            Map<String,Object> obj = asObject(airportsList.get(i), "airports["+i+"]");
            String code = asString(obj.get("code"), "airports["+i+"].code");
            String name = asString(obj.get("name"), "airports["+i+"].name");
            int x = asInt(obj.get("x"), "airports["+i+"].x");
            int y = asInt(obj.get("y"), "airports["+i+"].y");

            Airport a = new Airport(code,name,x,y);
            validateAirport(a, stagedAirports, "airports["+i+"]");
            stagedAirports.add(a);
            byCode.put(code,a);
        }

        List<Object> flightsList = (List<Object>) flightsRaw;
        for(int i=0;i<flightsList.size();i++){
            Map<String,Object> obj = asObject(flightsList.get(i), "flights["+i+"]");
            String fromCode = asString(obj.get("from"), "flights["+i+"].from");
            String toCode = asString(obj.get("to"), "flights["+i+"].to");
            String departure = asString(obj.get("departure"), "flights["+i+"].departure");
            int duration = asInt(obj.get("duration"), "flights["+i+"].duration");

            Flight f = resolveFlight(byCode, fromCode, toCode, departure, String.valueOf(duration), "flights["+i+"]");
            stagedFlights.add(f);
        }

        SimData.GetInstance().ReplaceAll(stagedAirports, stagedFlights);
    }

    private static void validateAirport(Airport a, List<Airport> against, String where){
        try {
            SimData.ValidateAirport(a, against);
        } catch (RuntimeException ex){
            throw new FileFormatException(where+": "+ex.getMessage());
        }
    }

    private static Flight resolveFlight(Map<String,Airport> byCode, String fromCode, String toCode,
                                         String departure, String durationText, String where){
        Airport from = byCode.get(fromCode);
        Airport to = byCode.get(toCode);
        if(from==null) throw new FileFormatException(where+": unknown airport code '"+fromCode+"'.");
        if(to==null) throw new FileFormatException(where+": unknown airport code '"+toCode+"'.");

        int startTime;
        try {
            startTime = TimeHelpers.ParseTime(departure);
        } catch (RuntimeException ex){
            throw new FileFormatException(where+": "+ex.getMessage());
        }

        int duration;
        try {
            duration = Integer.parseInt(durationText);
        } catch (NumberFormatException ex){
            throw new FileFormatException(where+": duration must be a whole number.");
        }

        Flight f = new Flight(from,to,startTime,duration);
        try {
            SimData.ValidateFlight(f);
        } catch (RuntimeException ex){
            throw new FileFormatException(where+": "+ex.getMessage());
        }
        return f;
    }

    @SuppressWarnings("unchecked")
    private static Map<String,Object> asObject(Object value, String path){
        if(!(value instanceof Map))
            throw new FileFormatException(path+" is not a valid object.");
        return (Map<String,Object>) value;
    }
    private static String asString(Object value, String path){
        if(!(value instanceof String))
            throw new FileFormatException(path+" is missing or is not a string.");
        return (String) value;
    }
    private static int asInt(Object value, String path){
        if(!(value instanceof Double))
            throw new FileFormatException(path+" is missing or is not a number.");
        return ((Double) value).intValue();
    }

    private static void writeFile(File file, String content){
        try(Writer out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)){
            out.write(content);
        } catch (IOException ex){
            throw new FileFormatException("Could not write to file '"+file.getName()+"': "+ex.getMessage());
        }
    }
}
