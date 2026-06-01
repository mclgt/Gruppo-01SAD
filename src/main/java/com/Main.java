package com;

import com.Model.Library;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;

public class Main extends Application {

    public void start(Stage pStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/MainView.fxml"));
        Parent p = loader.load();
        pStage.setTitle("Riproduttore musicale");
        pStage.setScene(new Scene(p, 1000, 650));
        pStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
