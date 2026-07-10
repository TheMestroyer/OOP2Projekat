package Helpers;

public final class CoordHelpers {
    public static final int MIN_X = -180, MAX_X = 180;
    public static final int MIN_Y = -90, MAX_Y = 90;
    public static final int MAP_PADDING = 20;

    public static int[] DomainToScreen(int domainX, int domainY, int panelWidth, int panelHeight, int iconWidth, int iconHeight){
        double scaleX = (panelWidth - 2.0*MAP_PADDING - iconWidth) / (MAX_X - MIN_X);
        double scaleY = (panelHeight - 2.0*MAP_PADDING - iconHeight) / (MAX_Y - MIN_Y);
        int screenX = MAP_PADDING + (int)Math.round((domainX - MIN_X) * scaleX);
        int screenY = MAP_PADDING + (int)Math.round((domainY - MIN_Y) * scaleY);
        return new int[]{screenX, screenY};
    }

    public static boolean RectanglesOverlap(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2){
        return x1 < x2+w2 && x1+w1 > x2 && y1 < y2+h2 && y1+h1 > y2;
    }
}
