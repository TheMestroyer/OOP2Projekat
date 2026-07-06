package Helpers;

public final class CoordHelpers {
    public static int[] SimPanelShift(int x, int y){
        return new int[]{x-180,y-90};
    }
    public static int CalculateDistance(int x1,int y1, int x2, int y2){
        return (int)Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
    public static int CalculateStraightDistance(int a, int b){
        return Math.abs(a-b);
    }
}
