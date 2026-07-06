package GUI;

import java.awt.*;

public class Drawable{
    public void draw(){}
    protected boolean visible=false;
    public void SetVisible(boolean val){
        visible = val;
    };
    public boolean GetVisible(){
        return visible;
    };
}
