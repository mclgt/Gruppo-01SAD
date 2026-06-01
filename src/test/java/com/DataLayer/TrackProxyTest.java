package com.DataLayer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Classe di test per validare il pattern Proxy.
 * Si verifica che il Lazy Loading funzioni correttamente e gestisca
 * in sicurezza le chiamate ai metodi senza lanciare NullPointerException.
 */
public class TrackProxyTest {

    @Test
    public void testProxyInitialState() {
        TrackProxy proxy = new TrackProxy("test_path.wav");

        // Dimostriamo che appena creato, non deve essere in riproduzione
        assertFalse(proxy.isPlaying(), "Il proxy appena istanziato non dovrebbe essere in riproduzione");
    }

    @Test
    public void testStopWithoutLoadingDoesNotCrash() {
        TrackProxy proxy = new TrackProxy("test_path.wav");
        
        // Dimostriamo la sicurezza del Lazy Loading: chiamare stop() su un proxy 
        // che non ha mai caricato la RealTrack non deve generare eccezioni.
        assertDoesNotThrow(() -> {
            proxy.stopPlayback();
        });
    }
    
    @Test
    public void testLoadInvalidFileIsHandled() {
        TrackProxy proxy = new TrackProxy("test_path.wav");
        
        // Verifichiamo che il proxy gestisca internamente l'eccezione di javax.sound
        // e non faccia "esplodere" il programma chiamando il metodo load()
        assertDoesNotThrow(() -> {
            proxy.load();
        });
    }
}