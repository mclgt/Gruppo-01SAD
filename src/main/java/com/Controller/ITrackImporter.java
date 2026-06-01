package com.Controller;

import javafx.stage.Window;
import java.io.File;

/**
 * @brief Interfaccia per il caricamento del brano dall'interfaccia grafica.
 */
public interface ITrackImporter {
    File selectAudioFile(Window ownerWindow);
}
