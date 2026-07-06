package GUI;

import Errors.SingletonNotInitialized;

import java.awt.*;

public class DrawAPIAWT {

    private Graphics myGraphics;
    private static DrawAPIAWT inst = null;

    private DrawAPIAWT(Graphics g){
        myGraphics = g;
    }
    public static void init(Graphics g) {
        //System.out.println(g.toString());
        if (inst == null) inst = new DrawAPIAWT(g);
        else inst.myGraphics = g;
        //System.out.println("aaaa");
    }

    public Graphics GetGraphics(){
        return myGraphics;
    }

    public static DrawAPIAWT getInstance() throws SingletonNotInitialized{
        if(inst==null)throw new SingletonNotInitialized("DrawAPIAWT not initialized before getInstance() is called!");
        return inst;
    }
    public void SetColor(Color color){
        myGraphics.setColor(color);
    }
    public void SetFont(Font font){
        myGraphics.setFont(font);
    }
    public void DrawRectangle(int x, int y, int w, int h){
        myGraphics.drawRect(x,y,w,h);
    }
    public void DrawCircle(int x, int y, int radius){
        myGraphics.drawOval(x-radius/2,y-radius/2,radius,radius);
    }
    public void FillRectangle(int x, int y, int w, int h){
        myGraphics.fillRect(x,y,w,h);
    }
    public void FillCircle(int x, int y, int radius){
        myGraphics.fillOval(x-radius/2,y-radius/2,radius,radius);
    }
    public void DrawText(int x, int y, String text){myGraphics.drawString(text,x,y);}
    public void TranslateRoot(int x,int y){myGraphics.translate(x,y);}
}
