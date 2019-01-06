package sample;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        StageManager.addStage("gameStage","Pong","table.fxml",1000,600);
        StageManager.addStage("choosingStage","Initial setting", "start_interface.fxml", 450,300);

        StageManager.getStage("choosingStage").show();

    }


    public static void main(String[] args) {
        launch(args);
    }



}

