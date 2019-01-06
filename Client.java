package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends Thread {

    //建立 Socket 以及傳輸資訊時所需的所有媒介
    private Socket s;
    private ReceiveMessageC r;
    private Thread receiveThread;

    private String sendMessage = "";

    private Timer timer;

    private OutputStream out;

    //連線狀況
    private Label clientStatus = (Label) SaveReference.getReference("clientStatus");
    private Button BIG_Client = (Button) SaveReference.getReference("BIG_Client");
    private TextField clientIP = (TextField) SaveReference.getReference("clientIP");

    private String boardStyleServer = "-fx-background-color: #ff6a6a;";
    private String boardStyleClient = "-fx-background-color: #6788ff;";
    private String pongStyle = "-fx-background-color: white;";

    private Board board1 = new Board(boardStyleServer, 100, 200, 20, 120);
    private Board board2 = new Board(boardStyleClient, 900, 200, 20, 120);
    private SimplePong pong = new SimplePong(pongStyle, 485, 285, 30, 30);


    private AnchorPane anchorPane = (AnchorPane) SaveReference.getReference("anchorPane");

    private Label serverScore = (Label)SaveReference.getReference("serverScore");
    private Label clientScore = (Label)SaveReference.getReference("clientScore");

    private Button setServer = (Button)SaveReference.getReference("setServer");
    private Button connectToServer = (Button)SaveReference.getReference("connectToServer");

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientStatus.setText("client:連線中...");
                try {
                    s = new Socket(clientIP.getText(), 8080);

                    out = s.getOutputStream();
                    r = new ReceiveMessageC(s,board1,board2,pong);

                    receiveThread = new Thread(r);
                    receiveThread.start();
                } catch (Exception e) {
                    exit();
                    System.out.println("Exception");
                    e.printStackTrace();
                }
            }
        });


        System.out.println("成功連線");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientStatus.setText("成功連線!");
//                BIG_Client.setText("Start!");
//                BIG_Client.setDisable(false);
                StageManager.getStage("gameStage").show();
                StageManager.getStage("choosingStage").close();

                StageManager.getStage("gameStage").getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {

                        switch (event.getCode()) {
                            case UP:
                                board2.setDir(1);
                                break;
                            case DOWN:
                                board2.setDir(2);
                                break;
                        }
                    }
                });

                anchorPane.getChildren().add(pong);
                anchorPane.getChildren().add(board1);
                anchorPane.getChildren().add(board2);

            }
        });

        board2.start();

        timer = new Timer(SaveReference.getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (serverScore.getText().equals("5")){
                    timer.stop();
                    exit();
                }else if (clientScore.getText().equals("5")){
                    timer.stop();
                    exit();
                }else {
//                    sendMessage = Integer.toString(board2.getDir());
                    if (board2.getLayoutY() >= 0) {

                        if (board2.getLayoutY() < 10) {
                            sendMessage += "00" + Integer.toString((int) board2.getLayoutY());
                        } else if (board2.getLayoutY() < 100 && board2.getLayoutY() >= 10) {
                            sendMessage += "0" + Integer.toString((int) board2.getLayoutY());
                        } else {
                            sendMessage += Integer.toString((int) board2.getLayoutY());
                        }

                    }

                    try {
                        out.write(sendMessage.getBytes());
                    }catch (Exception e1) {
                        timer.stop();
                        exit();
                        System.out.println("Exception(exit() in timer)");
                        e1.printStackTrace();
                    }

                    sendMessage = "";
                    
                }

            }
        });
        timer.start();
    }

    private void exit() {

        try {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    clientStatus.setText("status of setting");
                    StageManager.getStage("gameStage").close();
                    StageManager.getStage("choosingStage").show();
                    setServer.setDisable(false);
                    connectToServer.setDisable(false);

                    board2.stop();

                    clientScore.setText("0");
                    serverScore.setText("0");

                    anchorPane.getChildren().removeAll(pong,board1,board2);

                    Element.clearEleArr();

                }
            });

            s.close();

        }catch (Exception e) {
            System.out.println("Exception(exit() in Client)");
            e.printStackTrace();
        }
    }


}

class ReceiveMessageC implements Runnable {

    private byte mesin[] = new byte[13];
    private InputStream in;
    private Socket s;
    private SimplePong pong;
    private Board board1,board2;
    private Timer timer;

    private Label serverScore = (Label)SaveReference.getReference("serverScore");
    private Label clientScore = (Label)SaveReference.getReference("clientScore");

    private AnchorPane anchorPane = (AnchorPane) SaveReference.getReference("anchorPane");

    private int scoreS;
    private int scoreC;
    private int count = 0;

    private Button setServer = (Button)SaveReference.getReference("setServer");
    private Button connectToServer = (Button)SaveReference.getReference("connectToServer");

    public ReceiveMessageC( Socket socket,  Board b1, Board b2,SimplePong p) {
        s = socket;
        pong = p;
        board1 = b1;
        board2 = b2;
    }

    @Override
    public void run() {



        timer = new Timer(SaveReference.getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    in = s.getInputStream();

                    int n = in.read(mesin);
                    String str = new String(mesin, 0, n);
                    String x = "", y = "",scoreServer = "", scoreClient = "",board1Y="";

                    if (str.length() == 13) {

                        System.out.println("---"+count+"---");
                        System.out.println(str);
                        System.out.println("------------------------------");
                        count++;


                        scoreServer += str.charAt(0);
                        scoreServer += str.charAt(1);

                        scoreClient += str.charAt(2);
                        scoreClient += str.charAt(3);

                        x += str.charAt(4);
                        x += str.charAt(5);
                        x += str.charAt(6);

                        y += str.charAt(7);
                        y += str.charAt(8);
                        y += str.charAt(9);

                        board1Y += str.charAt(10);
                        board1Y += str.charAt(11);
                        board1Y += str.charAt(12);


                        scoreS = Integer.parseInt(scoreServer);
                        scoreC = Integer.parseInt(scoreClient);


                        pong.setLayoutX(Integer.parseInt(x));
                        pong.setLayoutY(Integer.parseInt(y));

                        board1.setLayoutY(Integer.parseInt(board1Y));


                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                serverScore.setText(Integer.toString(scoreS));
                                clientScore.setText(Integer.toString(scoreC));
                            }
                        });



                    }


//                    System.out.println(str);
                } catch (Exception e1) {
                    exit();
                    System.out.println("Exception(in ReceiveMessageC)");
                    e1.printStackTrace();
                }


            }
        });
        timer.start();

    }

    private void exit() {
        timer.stop();
        try {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    StageManager.getStage("gameStage").close();
                    StageManager.getStage("choosingStage").show();
                    setServer.setDisable(false);
                    connectToServer.setDisable(false);

                    board2.stop();

                    clientScore.setText("0");
                    serverScore.setText("0");

                    anchorPane.getChildren().removeAll(pong,board1,board2);

                    Element.clearEleArr();

                }
            });


            s.close();


        } catch (Exception e) {
            System.out.println("Exception(exit() in ReceiveMessageC)");
            e.printStackTrace();
        }
    }


}