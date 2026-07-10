
package Errors;

public class AirportExists extends RuntimeException {
    public static final int SAME_NAME     = 0;
    public static final int SAME_ID       = 1;
    public static final int SAME_COORDS   = 2;
    public static final int WOULD_OVERLAP = 3;

    public AirportExists(int type) {
        super(messageFor(type));
    }

    private static String messageFor(int type) {
        return switch (type) {
            case SAME_ID -> "An airport with the same ID already exists!";
            case SAME_COORDS -> "An airport already exists at these coordinates!";
            case WOULD_OVERLAP -> "This airport would overlap an existing airport on the map!";
            default-> "An airport with the same name already exists!";
        };
    }
}