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
    public String toString(){
        return "Airport: " +name+" ID: "+ id;
    }
    //Getters and setters END

    @Override
    public void draw() {
        try {
            DrawAPIAWT api = DrawAPIAWT.getInstance();
            Color fillColor = blink?UIConsts.BlinkColor:UIConsts.AirportColor;
            Color outlineColor = UIConsts.AirportOutlineColor;
            api.SetColor(fillColor);
            api.FillRectangle(x,y,DRAW_WIDTH,DRAW_HEIGHT);
            api.SetColor(outlineColor);
            api.DrawRectangle(x,y,DRAW_WIDTH,DRAW_HEIGHT);
            api.SetColor(UIConsts.TextColor);
            api.DrawText(x+DRAW_WIDTH+5,y,id+": "+name);
            //System.out.println("Drawing airport");

        }catch (SingletonNotInitialized e){
            System.out.println("Singleton not init");
            return;
        }
    }
}
