package com.studentlife;

import com.studentlife.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new MainWindow(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
