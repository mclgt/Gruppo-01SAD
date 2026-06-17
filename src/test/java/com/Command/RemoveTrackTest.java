package com.Command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.ITrackContainer;
import com.Model.Library;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.MockTrackFactory;

/**
 * @class RemoveTrackTest
 * @brief Classe di test per verificare la rimozione di un brano musicale e
 *        l'integrazione con l'UndoManager.
 *        L'uso di un MockTrackFactory garantisce che i brani creati per
 *        il test siano fittizi e non richiedano l'accesso al file system.
 */
public class RemoveTrackTest {

    private ITrackContainer container;
    private UndoManager undoManager;
    private TrackDAO trackDAO;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    /**
     * @brief Metodo di configurazione iniziale (Setup dell'ambiente di test).
     *        Viene eseguito automaticamente prima di ogni test.
     *        Prepara un ambiente isolato: inizializza il mock factory, crea un
     *        contenitore
     *        vuoto (Library), istanzia il gestore degli undo e genera tre tracce
     *        fittizie di prova per popolare la libreria.
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        container = new Library();
        undoManager = new UndoManager();
        track1 = factory.createTrack("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "dummy.mp3",
                null);
        track2 = factory.createTrack("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "dummy.mp3",
                null);
        track3 = factory.createTrack("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "dummy.mp3",
                null);
    }

    /**
     * @brief Testa l'esecuzione della rimozione di una traccia e il successivo
     *        annullamento (undo).
     * 
     *        Popolamento e Verifica Iniziale: Inserisce le tre tracce fittizie nel
     *        contenitore
     *        ed esegue un cast a Library per verificare esplicitamente che il
     *        conteggio sia 3
     *        e che la traccia selezionata (track2) si trovi nella posizione attesa.
     *        Execute: Instanzia il comando RemoveTrack per eliminare track2 e lo fa
     *        eseguire dall'UndoManager. Asserisce che la dimensione della libreria
     *        scenda a 2
     *        e che la traccia eliminata non sia più presente.
     *        Undo: Richiama l'annullamento dell'operazione. Verifica che la traccia
     *        venga correttamente reinserita, riportando la dimensione totale della
     *        libreria a 3.
     */
    @Test
    public void testRemoveTrackCommand() {
        container.addTrack(track1);
        container.addTrack(track2);
        container.addTrack(track3);

        // Convertiamo il container in Library per usare metodi specifici
        // del test per accedere a metodi che non sono nell'interfaccia
        Library library = (Library) container;

        assertEquals(3, library.getTracksCount(), "La libreria dovrebbe contenere 3 tracce");
        assertEquals(track2, library.getTracks().get(1), "Il brano track2 dovrebbe essere in posizione 1");

        ICommand removeCommand = new RemoveTrack(container, track2, trackDAO);
        undoManager.executeCommand(removeCommand);

        assertEquals(2, library.getTracksCount(), "La libreria dovrebbe contenere 2 tracce dopo la rimozione");
        assertFalse(library.getTracks().contains(track2), "La traccia non è stata rimossa dalla libreria");

        undoManager.undo();
        assertEquals(3, library.getTracksCount(), "La libreria dovrebbe contenere di nuovo 3 tracce");
    }
}