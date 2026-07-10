package Abstractions;

import Errors.SingletonNotInitialized;
import GUI.DrawAPIAWT;
import GUI.Drawable;
import GUI.UIConsts;

import java.awt.*;

public class Airport extends Drawable implements Tickable {
    public static final int DRAW_WIDTH=20,DRAW_HEIGHT=20;
    private static final int TEXT_PADDING = 5;
    private final int x,y;
    private final String name;
    private final String id;

    //GUI stuff
    private volatile boolean selected = false;
    private volatile boolean blink=false;

    public Airport(String id,String name,int x,int y){
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        selected = false;
        SetVisible(true);
    }

    //Getters and setters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getName() {return name;}
    public String getId(){return id;}

    public boolean getSelected(){return selected;}
    public void select(){
        if(!selected) {
            selected = true;
            SimClock.GetInstance().register(this);
        }
    }
    public void deselect(){
        selected=false;
        blink = false;
        SimClock.GetInstance().unregister(this);
    }
    public void toggleBlink(){
        blink = !blink;
    }

    @Override
    public void onTick(){
        toggleBlink();
    }

    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public String toString(){
        return "Airport: " +name+" ID: "+ id;
    }
    //Getters and setters END

    @Override
    public void draw() {
        try {
            if(!GetVisible())return;
            DrawAPIAWT api = DrawAPIAWT.getInstance();
            int[] screen = api.MapPoint(x,y,DRAW_WIDTH,DRAW_HEIGHT);
            int sx = screen[0], sy = screen[1];
            Color fillColor = blink?UIConsts.BlinkColor:UIConsts.AirportColor;
            Color outlineColor = UIConsts.AirportOutlineColor;
            api.SetColor(fillColor);
            api.FillRectangle(sx,sy,DRAW_WIDTH,DRAW_HEIGHT);
            api.SetColor(outlineColor);
            api.DrawRectangle(sx,sy,DRAW_WIDTH,DRAW_HEIGHT);
            api.SetColor(UIConsts.TextColor);

            String label = id+": "+name;
            int textX = sx+DRAW_WIDTH+TEXT_PADDING;
            int textWidth = api.TextWidth(label);
            if(textX+textWidth > api.GetMapWidth())
                textX = api.GetMapWidth()-textWidth;
            api.DrawText(textX,sy,label);
            //System.out.println("Drawing airport");

        }catch (SingletonNotInitialized e){
            System.out.println("Singleton not init");
            return;
        }
    }
}
