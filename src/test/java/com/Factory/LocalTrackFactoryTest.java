package com.Factory;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;
import com.Model.LocalTrackFactory;

/***
 * @brief Classe di test per la validazione della LocalTrackFactory
 *        Si verifica che l'istanziazione dei brani rispetti gli Acceptance
 *        Criteria della [US-1]
 *        osservando sia i casi di successo che la gestione delle eccezioni per
 *        dati non validi.
 */

public class LocalTrackFactoryTest {
    private TrackFactory factory;
    private File tempAudioFile;
    private String nameAudio;

    @BeforeEach
    public void setUp() throws IOException {
        this.factory = new LocalTrackFactory();

        File libraryAudio = new File("data/library_audio");
        if (!libraryAudio.exists()) {
            libraryAudio.mkdir();
        }

        nameAudio = "Track.mp3";
        tempAudioFile = new File(libraryAudio, nameAudio);
        if (!tempAudioFile.exists()) {
            tempAudioFile.createNewFile();
        }
    }

    @AfterEach
    public void tearDown() {
        if (tempAudioFile != null && tempAudioFile.exists()) {
            tempAudioFile.delete();
        }
    }

    @Test
    public void testCreateTrack_success() throws IOException {

        Track track = factory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238,
                "A Night at the opera", tempAudioFile.getAbsolutePath(), TrackTag.NONE);
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
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTrack("", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera",
                    tempAudioFile.getAbsolutePath(), TrackTag.NONE);
        });
        assertNotNull(exc);
    }

    @Test
    public void testCreateTrack_noAuthor() throws IOException {
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTrack("Creep", "", 1992, "Alternative Rock", 238, "A Night at the opera",
                    tempAudioFile.getAbsolutePath(), TrackTag.NONE);
        });
        assertNotNull(exc);
    }

    @Test
    public void testCreateTrack_invalidDuration() throws IOException {
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", -2, "A Night at the opera",
                    tempAudioFile.getAbsolutePath(), TrackTag.NONE);
        });
        assertNotNull(exc);
    }

    @Test
    public void testCreateTrack_notExistentFilePath() {
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera",
                    "C:/percorso/test.wav", TrackTag.NONE);
        });
        assertNotNull(exc);
    }

    @Test
    public void testCreateTrack_emptyFilePath() {
        Exception exc = assertThrows(IllegalArgumentException.class, () -> {
            factory.createTrack("Creep", "Radiohead", 1992, "Alternative Rock", 238, "A Night at the opera",
                    "",
                    TrackTag.NONE);
        });
        assertNotNull(exc);
    }
}