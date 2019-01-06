package sample;

import javafx.application.Platform;
import javafx.scene.control.Label;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Pong extends Element {

    /*==========資料成員=========*/
    private Pong thisRef = this;
    private float m; //斜率
    private Random rand = new Random();
    private int v = 2, nowX, nowY, nextX, nextY, preY; /*移動速率、目前的x位置、目前的y位置、下一個x位置、下一個y位置
                                                        、移動前的y位置*/
    private boolean xFlag; //控制x方向的flag

    private Label serverScore = (Label) SaveReference.getReference("serverScore");
    private Label clientScore = (Label) SaveReference.getReference("clientScore");

    private int scoreServer = 0;
    private int scoreClient = 0;

    /*==========================*/

    /*===========建構元==========*/
    public Pong(String style, int layoutX, int layoutY, int width, int height) {

        super(style, layoutX, layoutY, width, height);

        nowX = layoutX;
        nowY = layoutY;

        init();
        update();

    }
    /*===========================*/

    /*============方法============*/

    public void init() {
        /*-----決定斜率為正or負&&xFlag-----*/
        if (rand.nextInt(11) % 2 == 0) {
            m = rand.nextInt(2) + 1;
        } else {
            m = -(rand.nextInt(2) + 1);
        }

        if (rand.nextInt(4) % 2 == 0) {
            xFlag = true;
        } else {
            xFlag = false;
        }
        /*--------------------------------*/
    }

    public void update() {

        timer = new Timer(SaveReference.getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (int i = 1; i < eleArr.size(); i++) {


                    Element temp = eleArr.get(i);

                    if (nextX < temp.getLayoutX() + temp.getWidth()+10 && //撞右邊
                            nextX + thisRef.getWidth() > temp.getLayoutX()-10 &&
                            nextY < temp.getLayoutY() + temp.getHeight()+10 && //撞下邊
                            nextY + thisRef.getHeight() > temp.getLayoutY()-10) {



                        if (preY > temp.getLayoutY() + temp.getHeight() ||//上一個y高於這個物件的y+height
                                preY + thisRef.getHeight() < temp.getLayoutY()) {//上一個y低於這個物件的y
                            /*撞到上下邊↑*/

                            init();
                            m = -m;
                        } else { //撞到左右邊

                            init();
                            xFlag = !xFlag;
                        }


                    }


                }

                preY = nowY;

                if ((nextX < 10) || (nextX > 960) || (nextY < 10) || (nextY > 560)) {//碰到scene的其中一邊


                    if ((nextY <= 10) || (nextY >= 560)) {//碰到上邊or下邊
                        if ((nextY <= 10)) {
                            nowY = 10;
                        } else {
                            nowY = 560;
                        }
                        m = -m;
                    } else {//碰到左邊or右邊

                        if (nextX <= 10) { //碰左邊
                            nowX = 10;
                            scoreClient++;
                        } else {  //碰右邊
                            nowX = 960;
                            scoreServer++;
                        }
                        xFlag = !xFlag;

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                serverScore.setText(Integer.toString(scoreServer));
                                clientScore.setText(Integer.toString(scoreClient));
                            }
                        });


                    }

                }


                if (xFlag) {
                    nowX += v;
                    nextX = nowX + v;
                } else {
                    nowX -= v;
                    nextX = nowX - v;
                }


                nowY = (int) m * v + nowY;

                nextY = (int) m * v + nowY;

                thisRef.setLayoutX(nowX);
                thisRef.setLayoutY(nowY);

//                System.out.println("x:"+nowX+",y:"+nowY+",m:"+m+",xFlag:"+xFlag);
//                System.out.println("nextX:"+nextX+",nextY:"+nextY);
//                System.out.println("--------------------------------");
//                System.out.println("m:"+m);
            }
        });


    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public int getScoreServer() {
        return scoreServer;
    }

    public int getScoreClient() {
        return scoreClient;
    }

    /*===========================*/

}