package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private ServerSocket svs;
    private Socket s;
    private ReceiveMessageS r;
    private Thread receiveThread;

    private OutputStream out;

    private Timer timer;

    private String sendMessage = "";

    private Label serverStatus = (Label) SaveReference.getReference("serverStatus");
    private Button BIG_Server = (Button) SaveReference.getReference("BIG_Server");

    private String boardStyleServer = "-fx-background-color: #ff6a6a;";
    private String boardStyleClient = "-fx-background-color: #6788ff;";
    private String pongStyle = "-fx-background-color: white;";

    private Pong pong = new Pong(pongStyle, 485, 285, 30, 30);
    private Board board1 = new Board(boardStyleServer, 100, 200, 20, 120);
    private Board board2 = new Board(boardStyleClient, 900, 200, 20, 120);

    private AnchorPane anchorPane = (AnchorPane) SaveReference.getReference("anchorPane");

    private Button setServer = (Button) SaveReference.getReference("setServer");
    private Button connectToServer = (Button) SaveReference.getReference("connectToServer");

    private Label serverScore = (Label) SaveReference.getReference("serverScore");
    private Label clientScore = (Label) SaveReference.getReference("clientScore");

    private  int count = 0;
    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                serverStatus.setText("server:連線中...");
            }
        });

        try {
            svs = new ServerSocket(8080);
            s = svs.accept();
            svs.close();

            out = s.getOutputStream();
        } catch (Exception e) {
            exit();
            System.out.println("Exception");
            e.printStackTrace();
        }


        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                serverStatus.setText("成功連線!");

//                BIG_Server.setText("Start!");
//                BIG_Server.setDisable(false);

                StageManager.getStage("gameStage").show();

                StageManager.getStage("choosingStage").close();
                StageManager.getStage("gameStage").getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {

                        switch (event.getCode()) {
                            case UP:
                                board1.setDir(1);
                                break;
                            case DOWN:
                                board1.setDir(2);
                                break;
                        }
                    }
                });

                anchorPane.getChildren().add(pong);
                anchorPane.getChildren().add(board1);
                anchorPane.getChildren().add(board2);

            }
        });

        board1.start();
        board2.start();
        pong.start();

        r = new ReceiveMessageS( s, board1, board2, pong);
        receiveThread = new Thread(r);
        receiveThread.start();



        timer = new Timer(SaveReference.getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                /***********************資料傳送說明:**************************/
                /*                                                          */
                /*                                                          */
                /*     傳輸的字串為"分數+分數+球x座標+球y座標+板子y座標"的形式     */
                /*     會傳13個數，舉個例子，sendMessage="0304650302500"       */
                /*    0   3   0   4   6   5   0   3   0   2   5   0   0    */
                /*    ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓   ↓    */
                /*   分數 分數 分數 分數 px  px  px  py  py  py  by  by  by   */
                /*                                                         */
                /*xy座標均為三位數，若不足三位數則補0補到三位數，分數則不滿十位數補零 */
                /*                         舉個例子                         */
                /*                 10 -> 010 ， 3 -> 003                   */
                /*                  分數同理，未滿十位數補0                   */
                /*                                                         */
                /***********************************************************/


                if (pong.getScoreClient() == 5) {
                    timer.stop();
                    exit();
                } else if (pong.getScoreServer() == 5) {
                    timer.stop();
                    exit();
                } else {

                    if (pong.getScoreServer() >= 10) {
                        sendMessage += Integer.toString(pong.getScoreServer());
                    } else {
                        sendMessage += "0" + Integer.toString(pong.getScoreServer());
                    }

                    if (pong.getScoreClient() >= 10) {
                        sendMessage += Integer.toString(pong.getScoreClient());
                    } else {
                        sendMessage += "0" + Integer.toString(pong.getScoreClient());
                    }

                    if (pong.getLayoutY() >= 0 && pong.getLayoutX() >= 0) {

                        if (pong.getLayoutX() < 10) {
                            sendMessage += "00" + Integer.toString((int) pong.getLayoutX());
                        } else if (pong.getLayoutX() < 100 && pong.getLayoutX() >= 10) {
                            sendMessage += "0" + Integer.toString((int) pong.getLayoutX());
                        } else {
                            sendMessage += Integer.toString((int) pong.getLayoutX());
                        }

                        if (pong.getLayoutY() < 10) {
                            sendMessage += "00" + Integer.toString((int) pong.getLayoutY());
                        } else if (pong.getLayoutY() < 100 && pong.getLayoutY() >= 10) {
                            sendMessage += "0" + Integer.toString((int) pong.getLayoutY());
                        } else {
                            sendMessage += Integer.toString((int) pong.getLayoutY());
                        }

                    }

                    if (board1.getLayoutY() >= 0) {

                        if (board1.getLayoutY() < 10) {
                            sendMessage += "00" + Integer.toString((int) board1.getLayoutY());
                        } else if (board1.getLayoutY() < 100 && board1.getLayoutY() >= 10) {
                            sendMessage += "0" + Integer.toString((int) board1.getLayoutY());
                        } else {
                            sendMessage += Integer.toString((int) board1.getLayoutY());
                        }

                    }


                    try {
                        if (sendMessage.length() == 13){
                            out.write(sendMessage.getBytes());
                        }
                    } catch (Exception e1) {
                        timer.stop();
                        exit();
                        System.out.println("Exception(exit() in timer)");
                        e1.printStackTrace();
                    }

                    System.out.println("---"+count+"---");
                    System.out.println(sendMessage);
                    System.out.println("------------------------------");
                    count++;


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
                    serverStatus.setText("status of setting");
                    StageManager.getStage("gameStage").close();
                    StageManager.getStage("choosingStage").show();
                    setServer.setDisable(false);
                    connectToServer.setDisable(false);

                    pong.stop();
                    board1.stop();
                    board2.stop();

                    clientScore.setText("0");
                    serverScore.setText("0");

                    anchorPane.getChildren().removeAll(pong,board1,board2);

                    Element.clearEleArr();

                }
            });


            s.close();


        } catch (Exception e) {
            System.out.println("Exception(exit() in Server)");
            e.printStackTrace();
        }
    }


}

class ReceiveMessageS implements Runnable {

    private byte mesin[] = new byte[3];
    private InputStream in;
    private Socket s;
    private Board board1, board2;
    private Pong pong;
    private Timer timer;
    private Button setServer = (Button) SaveReference.getReference("setServer");
    private Button connectToServer = (Button) SaveReference.getReference("connectToServer");
    private AnchorPane anchorPane = (AnchorPane) SaveReference.getReference("anchorPane");
    private Label serverScore = (Label) SaveReference.getReference("serverScore");
    private Label clientScore = (Label) SaveReference.getReference("clientScore");

    public ReceiveMessageS( Socket socket, Board b1, Board b2, Pong p) {
        s = socket;
        board1 = b1;
        board2 = b2;
        pong = p;
    }

    @Override
    public void run() {

        try {
            in = s.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer = new Timer(SaveReference.getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    int n = in.read(mesin);
                    String str = new String(mesin, 0, n);
                    String board2Y="";

                    board2Y += str.charAt(0);
                    board2Y += str.charAt(1);
                    board2Y += str.charAt(2);

                    board2.setLayoutY(Integer.parseInt(board2Y));

                } catch (Exception e1) {
                    exit();
                    System.out.println("Exception(in ReceiveMessageS)");
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

                    pong.stop();
                    board1.stop();
                    board2.stop();

                    clientScore.setText("0");
                    serverScore.setText("0");

                    anchorPane.getChildren().removeAll(pong,board1,board2);

                    Element.clearEleArr();

                }
            });

            s.close();


        } catch (Exception e) {
            System.out.println("Exception(exit() in ReceiveMessageS)");
            e.printStackTrace();
        }
    }


}
