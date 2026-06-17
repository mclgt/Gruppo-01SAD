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

/**
 * @class Main
 * @brief Punto di ingresso dell'applicazione JavaFX.
 *
 *        Inizializza l'AppState (libreria, DAO, factory) e carica la vista
 *        principale tramite FXMLLoader. Al momento della chiusura,
 *        sincronizza il database e rilascia tutte le risorse.
 *
 * @see AppState
 * @see MainController
 */
public class Main extends Application {
    private MainController mainController;

    /**
     * @brief Avvia l'applicazione: inizializza AppState, carica il file FXML
     *        della vista principale e mostra la finestra.
     * @param pStage Lo Stage primario fornito dal runtime JavaFX.
     * @throws Exception In caso di errori nel caricamento dell'FXML.
     */
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

    /**
     * @brief Eseguito automaticamente dal runtime JavaFX alla chiusura della
     *        finestra.
     *        Ferma il timer di riproduzione, salva tutti i dati nel database
     *        tramite {@link MainController#saveDB()} e chiude la connessione
     *        SQLite.
     * @throws Exception In caso di errori durante la sincronizzazione.
     */
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

    /**
     * @brief Metodo main: delega il lancio al runtime JavaFX tramite
     *        {@link Application#launch}.
     * @param args Argomenti della riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
