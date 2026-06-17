package com.Command;

/**
 * @brief Classe di test per verificare l'aggiunta di un brano 
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.DataLayer.DAO.Track.TrackDAO;
import com.Model.Library;
import com.Model.Track;
import com.Model.TrackFactory;

import com.Model.MockTrackFactory;

/**
 * @class AddTrackTest
 * @brief Classe di test per verificare il corretto funzionamento del comando
 *        AddTrack.
 *        Il test sfrutta una libreria fittizia in memoria e un MockTrackFactory
 *        per istanziare un brano
 *        di prova isolando la logica dai file audio reali, assicurando così
 *        tes indipendenti dal file system.
 */
public class AddTrackTest {
    private Library l;
    private Track t;
    private TrackFactory factory;
    private TrackDAO trackDAO;

    /**
     * @brief Metodo di configurazione iniziale (Setup).
     *        Eseguito automaticamente prima di ogni singolo test.
     *        Inizializza un ambiente pulito creando una libreria vuota, istanziando
     *        il MockTrackFactory e generando un brano di test specifico ("Bohemian
     *        Rhapsody")
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        l = new Library();
        t = factory.createTrack("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "dummy.mp3", null);
    }

    /**
     * @brief Testa il ciclo di vita completo (esecuzione e annullamento) del
     *        comando di aggiunta traccia.
     * 
     *        Execute: invoca il comando per inserire la traccia e verifica che
     *        il contatore della libreria salga a 1 e che la traccia sia
     *        effettivamente presente.
     *        Undo: invoca il ripristino dell'azione e verifica che la traccia
     *        sia stata rimossa con successo, riportando il contatore della libreria
     *        a 0.
     */
    @Test
    public void testAddTrack_executeAndUndo() {
        ICommand addCommand = new AddTrack(l, t, trackDAO);

        // Fase di execute
        addCommand.execute();
        assertEquals(1, l.getTracksCount());
        assertTrue(l.getTracks().contains(t));

        // Fase di undo
        addCommand.undo();
        assertEquals(0, l.getTracksCount());
        assertFalse(l.getTracks().contains(t));
    }
}