package Helpers;

import Errors.InvalidFlightData;

public final class TimeHelpers {
    private TimeHelpers(){}

    public static int ParseTime(String text){
        if(text==null || !text.matches("([01]\\d|2[0-3]):[0-5]\\d"))
            throw new InvalidFlightData(InvalidFlightData.BAD_TIME);
        String[] parts = text.split(":");
        return Integer.parseInt(parts[0])*60 + Integer.parseInt(parts[1]);
    }

    public static String FormatTime(int minutesSinceMidnight){
        int m = ((minutesSinceMidnight % 1440) + 1440) % 1440;
        return String.format("%02d:%02d", m/60, m%60);
    }
}
