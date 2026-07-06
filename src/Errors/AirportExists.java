
package Errors;

public class AirportExists extends RuntimeException {
    public static final int SAME_NAME   = 0;
    public static final int SAME_ID     = 1;
    public static final int SAME_COORDS = 2;

    public AirportExists(int type) {
        super(messageFor(type));
    }
    public AirportExists(String airportId) {
        super("The airport with the id "+airportId+" would overlap this airport!");
    }

    private static String messageFor(int type) {
        return switch (type) {
            case SAME_ID -> "An airport with the same ID already exists!";
            default-> "An airport with the same name already exists!";
        };
    }
}