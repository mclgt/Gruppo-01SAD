package com;

import com.Controller.core.MainController;
import com.Model.LocalTrackFactory;
import com.Model.TrackFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage pStage) throws Exception {
        TrackFactory factory = new LocalTrackFactory();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/MainView.fxml"));
        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainController.class) {
                return new MainController(factory);
            }
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Parent p = loader.load();
        pStage.setTitle("Riproduttore musicale");
        pStage.setScene(new Scene(p, 1000, 650));
        pStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
