package sample;

import javafx.scene.control.Label;

import javax.swing.*;
import java.util.ArrayList;

public class Element extends Label{

    /*==========資料成員=========*/
    protected static ArrayList<Element> eleArr = new ArrayList<Element>();//存取物件用的
    protected Timer timer;//移動用的
    /*==========================*/

    /*===========建構元==========*/
    public Element(String style,double layoutX,double layoutY, int width, int height){
        this.setStyle(style);
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setPrefSize(width,height);

        eleArr.add(this); //將物件加入eleArr
    }
    /*==========================*/

    /*============方法============*/

    public static void clearEleArr(){
        eleArr.clear();
    }
    /*==========================*/


}
