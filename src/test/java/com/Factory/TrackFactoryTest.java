package com.Factory;

import com.Model.Track;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

/***
 * @brief Classe di test per la validazione della TrackFactory
 *        Si verifica che l'istanziazione dei brani rispetti gli Acceptance
 *        Criteria della [US-1]
 *        osservando sia i casi di successo che la gestione delle eccezioni per
 *        dati non validi.
 */

public class TrackFactoryTest {

    @Test
    public void testCreateTrack_success() throws IOException {
        File tmpFile = File.createTempFile("test_audio", ".wav");
        tmpFile.deleteOnExit();

        Track track = TrackFactory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238,
                "A Night at the opera", tmpFile.getAbsolutePath());
        assertNotNull(track);
        assertEquals("Creep", track.getTitle());
        assertEquals("Radiohead", track.getAuthor());
        assertEquals(1992, track.getYear());
        assertEquals("Alternative Rock", track.getGenre());
        assertEquals(238, track.getDuration());
        assertEquals("A Night at the opera", track.getAlbum());
        assertNotNull(track.getAudioSource(), "Il proxy deve essere stato assegnato");
    }

    @Test
    public void testCreateTrack_noTitle() throws IOException {
        File tmpFile = File.createTempFile("test_audio", ".wav");
        tmpFile.deleteOnExit();

        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            TrackFactory.createTrack("", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera", tmpFile.getAbsolutePath());
        });
        assertTrue(exc.getMessage().contains("Titolo"));
    }

    @Test
    public void testCreateTrack_noAuthor() throws IOException {
        File tmpFile = File.createTempFile("test_audio", ".wav");
        tmpFile.deleteOnExit();

        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            TrackFactory.createTrack("Creep", "", 1992, "Alternative Rock", 238, "A Night at the opera", tmpFile.getAbsolutePath());
        });
        assertTrue(exc.getMessage().contains("Autore"));
    }

    @Test
    public void testCreateTrack_invalidDuration() throws IOException {
        File tmpFile = File.createTempFile("test_audio", ".wav");
        tmpFile.deleteOnExit();

        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            TrackFactory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", -2, "A Night at the opera", tmpFile.getAbsolutePath());
        });
        assertTrue(exc.getMessage().contains("Durata"));
    }

    @Test
    public void testCreateTrack_notExistentFilePath(){
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            TrackFactory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera", "C:/percorso/test.wav");
        });

        assertTrue(exc.getMessage().contains("mancante") ||
                    exc.getMessage().contains("non valido"));
    }

    @Test
    public void testCreateTrack_emptyFilePath(){
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            TrackFactory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera", "");
        });

        assertTrue(exc.getMessage().contains("File"));
    }
}