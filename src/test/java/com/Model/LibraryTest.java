package com.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @brief Test per l'entità Library. Verifica le funzionalità principali della
 *        classe Library, come l'aggiunta, la rimozione e l'aggiornamento dei
 *        brani (Track) al suo interno.
 */
public class LibraryTest {
    private Library l;
    private Track t;
    private Track track1;
    private Track track2;
    private Track track3;
    private TrackFactory factory;

    /**
     * @brief inizializza l'ambiente di test, crendo una istanza vuota di library e
     *        popolando le track con dati fittizi.
     */
    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        l = new Library();
        t = factory.createTrack("Bohemian Rhapsody", "Queen", 1975, "Rock", 355, "A Night at the Opera",
                "C:/audio.wav", null);
        track1 = factory.createTrack("Track 1", "Artist 1", 2020, "Genre 1", 240, "Album 1", "path/to/track1.mp3",
                null);
        track2 = factory.createTrack("Track 2", "Artist 2", 2021, "Genre 2", 300, "Album 2", "path/to/track2.mp3",
                null);
        track3 = factory.createTrack("Track 3", "Artist 3", 2022, "Genre 3", 180, "Album 3", "path/to/track3.mp3",
                null);
    }

    /**
     * @brief testa l'aggiunta di un brano alla libreria, dopo aver aggiunto il
     *        brano, il conteggio totale deve aumentare di uno e il brano specifico
     *        deve essere contenuto nella collezione.
     */

    @Test
    public void testAddTrack_success() {
        l.addTrack(t);
        assertEquals(1, l.getTracksCount(), "La libreria dovrebbe contenere 1 brano");
        assertTrue(l.getTracks().contains(t), "La libreria dovrebbe contenre il brano");
    }

    /**
     * @brief Testa la rimozione di un brano dalla libreria. Inserisce 3 brani, ne
     *        rimuove uno, verifica che il conteggio di aggiorni correttamente e che
     *        il brano non sia contenuto più nella collezione.
     */
    @Test
    public void testRemoveTrack_success() {
        l.addTrack(track1); // dim=1
        l.addTrack(track2); // dim=2
        l.addTrack(track3); // dim=3
        l.removeTrack(track1);
        assertEquals(2, l.getTracksCount());
        assertTrue(!l.getTracks().contains(track1), "La libreria non dovrebbe contenere più il brano");
    }

    /**
     * @brief Testa l'aggiornamento dei dati relativi a un brano esistente. Aggiunge
     *        un brano, ne modifica i campi e verifica che i gettere restituiscano i
     *        valori aggiornati.
     */
    @Test
    public void testUpdateTrack_success() {
        l.addTrack(track1);
        l.updateTrack(track1, "esempio1", "esempio2", 2020, "esempio3", 1, "esempio4", "esempio5.mp3", null);
        assertEquals("esempio1", track1.getTitle(), "Il titolo dovrebbe essere cambiato");
        assertEquals("esempio2", track1.getAuthor(), "L'autore dovrebbe essere cambiato");
        assertEquals(2020, track1.getYear(), "L'anno dovrebbe essere cambiato");
        assertEquals("esempio3", track1.getGenre(), "Il genere dovrebbe essere cambiato");
        assertEquals(1, track1.getDuration(), "La durata dovrebbe essere cambiata");
        assertEquals("esempio4", track1.getAlbum(), "L'album dovrebbe essere cambiato");
        assertEquals("esempio5.mp3", track1.getFilePath(), "Il path dovrebbe essere cambiato");

    }

}
