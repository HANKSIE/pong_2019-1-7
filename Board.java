package sample;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends Element{

    private Board thisRef = this;

    public Board(String style, double layoutX, double layoutY, int width, int height) {
        super(style, layoutX, layoutY, width, height);
        move();
    }
    private int Dir = 0;

    public void setDir(int a){
        Dir = a;
    }

    public void move(){

        timer = new Timer(13, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (Dir){
                    case 0:
                        //do nothing
                        break;
                    case 1:
                        if(thisRef.getLayoutY() <= 30){
                            Dir = 2;
                        }
                        thisRef.setLayoutY(thisRef.getLayoutY() - 3);
                        break;
                    case 2:
                        if(thisRef.getLayoutY()+120 >= 600){
                            Dir = 1;
                        }
                        thisRef.setLayoutY(thisRef.getLayoutY() + 3);
                        break;
                }
            }
        });

    }

    public void start(){
        timer.start();
    }

    public void stop(){
        timer.stop();
    }

}
