package Abstractions;

import Errors.SingletonNotInitialized;
import GUI.DrawAPIAWT;
import GUI.Drawable;
import GUI.UIConsts;

import java.awt.*;

public class Airplane extends Drawable {
    private static final int AIRPLANE_RADIUS = 10;
    private int x,y;
    private boolean active;

    public void DrawAirplane(int x, int y,boolean active){
        this.x = x;
        this.y = y;
        this.active = active;
        draw();
    }
    @Override
    public void draw(){
        try {
            if(!active)return;
            DrawAPIAWT api = DrawAPIAWT.getInstance();
            int[] screen = api.MapPoint(x,y,AIRPLANE_RADIUS,AIRPLANE_RADIUS);
            Color fillColor = UIConsts.AirplaneColor;
            Color outlineColor = UIConsts.AirplaneOutlineColor;
            api.SetColor(fillColor);
            api.FillCircle(screen[0],screen[1],AIRPLANE_RADIUS);
            api.SetColor(outlineColor);
            api.DrawCircle(screen[0],screen[1],AIRPLANE_RADIUS);
            api.SetColor(UIConsts.TextColor);
            //System.out.println("Drawing airplane");

        }catch (SingletonNotInitialized e){
            System.out.println("Singleton not init");
            return;
        }
    }


}
