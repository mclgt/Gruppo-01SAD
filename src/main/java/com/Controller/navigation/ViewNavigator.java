package com.Controller.navigation;

import com.Controller.core.MainController;
import com.Controller.playlist.PlaylistController;
import com.Model.Playlist;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * @brief Classe responsabile della navigazione tra le viste principali
 *        nell'area centrale dell'applicazione.
 */
public class ViewNavigator {
    private final StackPane centerContentArea;
    private final MainController mainController;

    public ViewNavigator(StackPane centerContentArea, MainController mainController) {
        this.centerContentArea = centerContentArea;
        this.mainController = mainController;
    }

    /**
     * @brief Carica e mostra la vista di dettaglio di una playlist.
     * @param playlist La playlist da visualizzare.
     */
    public void loadPlaylistView(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/View/PlaylistView.fxml"));
            Node playlistViewNode = loader.load();

            PlaylistController controller = loader.getController();
            controller.setMainController(mainController);
            controller.setPlaylistData(playlist);
            mainController.getAppState().setPlaylistController(controller);
            centerContentArea.getChildren().clear();
            centerContentArea.getChildren().add(playlistViewNode);

            mainController.setTrackManagementButtonVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Ripristina la visualizzazione della libreria principale.
     * @param mainTable Il nodo contenente la tabella principale.
     */
    public void restoreMainLibraryView(Node mainTable) {
        mainController.setTrackManagementButtonVisible(true);
        PlaylistController activePlaylistController = mainController.getAppState().getPlaylistController();
        if (activePlaylistController != null) {
            activePlaylistController.dispose();
            mainController.getAppState().setPlaylistController(null);
        }
        centerContentArea.getChildren().clear();
        centerContentArea.getChildren().add(mainTable);
    }

}
