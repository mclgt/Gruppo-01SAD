package com.DataLayer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

/**
 * @brief Classe di testper la classe RealTrack.
 *        Verifica la corretta gestione del ciclo di vita dei file audio e
 *        il comportamento rispetto a file mancanti
 */
public class RealTrackTest {

    private RealTrack realTrack;
    private File testDir;

    /**
     * @brief Configurazione dell'ambiente di test.
     *        Crea un'istanza di RealTrack e prepara una directory temporanea
     *        per le operazioni sui file.
     */
    @BeforeEach
    public void setUp() {
        realTrack = new RealTrack("audio.wav");
        testDir = new File("temp_test_dir");
        if (!testDir.exists()) {
            testDir.mkdir();
        }
    }

    /**
     * @brief Elimina tutti i file temporanei creati e rimuove la directory di test.
     */
    @AfterEach
    public void tearDown() {
        File[] files = testDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        testDir.delete();
    }

    /**
     * @brief Verifica che il caricamento di un file inesistente non causi
     *        eccezioni. L'evento deve essere gestito da altre entità.
     */
    @Test
    public void testLoad_FileNotFound() {
        assertDoesNotThrow(() -> realTrack.load());
        assertFalse(realTrack.isPlaying());
    }

    /**
     * @brief Verifica lo stato iniziale del track.
     *        Un oggetto RealTrack appena istanziato non deve essere in
     *        riproduzione.
     */
    @Test
    public void testIsPlaying_FalseByDefault() {
        assertFalse(realTrack.isPlaying(), "Non dovrebbe essere in riproduzione appena creato");
    }

    /**
     * @brief Verifica il caricamento di un file esistente.
     *        Fornendo un percorso valido, la procedura di caricamento viene
     *        effettuata senza problemi.
     * @throws IOException Se si verificano errori di I/O nella creazione del file.
     */
    @Test
    public void testLoad_WithValidFile() throws IOException {
        File validFile = new File(testDir, "test.wav");
        validFile.createNewFile();

        RealTrack trackWithFile = new RealTrack(validFile.getAbsolutePath());
        assertDoesNotThrow(() -> trackWithFile.load());
    }
}