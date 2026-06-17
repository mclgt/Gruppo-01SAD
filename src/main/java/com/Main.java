package com;

import com.Controller.core.AppState;
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
        AppState appState = new AppState(factory);
        try {
            appState.loadData();
        } catch (Exception e) {
            System.err.println("Attenzione: Errore durante il caricamento iniziale del database.");
            e.printStackTrace();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/MainView.fxml"));
        Parent p = loader.load();
        this.mainController = loader.getController();
        this.mainController.init(appState);
        pStage.setTitle("Riproduttore musicale");
        pStage.setScene(new Scene(p, 1000, 650));
        pStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Rilevata chiusura dell'applicazione. Avvio sincronizzazione database...");

        if (mainController != null && mainController.getAppState() != null) {
            mainController.getAppState().getTimerManager().stop();
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