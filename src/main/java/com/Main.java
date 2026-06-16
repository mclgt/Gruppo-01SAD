package com;

import com.Controller.core.MainController;
import com.DataLayer.DAO.DatabaseManager;
import com.Model.LocalTrackFactory;
import com.Model.TrackFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private MainController mainController;

    @Override
    public void start(Stage pStage) throws Exception {
        TrackFactory factory = new LocalTrackFactory();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/MainView.fxml"));

        loader.setControllerFactory(controllerClass -> {
            if (controllerClass == MainController.class) {
                this.mainController = new MainController(factory);
                return this.mainController;
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

    @Override
    public void stop() throws Exception {
        System.out.println("Rilevata chiusura dell'applicazione. Avvio sincronizzazione database...");

        if (mainController != null) {
            mainController.getTimerManager().stop();
            mainController.saveDB();
        }

        // Rilascio definitivo e chiusura sicura delle risorse
        DatabaseManager.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}