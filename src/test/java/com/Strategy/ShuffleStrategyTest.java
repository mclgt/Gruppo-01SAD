package com.Strategy;

import com.Model.Track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ShuffleStrategyTest {

    private ShuffleStrategy strategy;
    private Track track1;
    private Track track2;
    private Track track3;


    @BeforeEach
    void setUp() {
        strategy = new ShuffleStrategy();
        track1 = new Track("Track 1", "Artista", 2020, "Pop", 180, "Album", "/path/1.mp3", null);
        track2 = new Track("Track 2", "Artista", 2021, "Pop", 200, "Album", "/path/2.mp3", null);
        track3 = new Track("Track 3", "Artista", 2022, "Pop", 210, "Album", "/path/3.mp3", null);
    
    }

    //test per nexttrack, il focus è sul restituire sempre brani diversi
    //per quanto possibile visto che sono 3 brani e 20 iterazioni 

    @Test
    void nextTrack_codaVuota_restituisceNull() {
        List<Track> queue= List.of();
        assertNull(strategy.nextTrack(queue, null));
    }

    @Test
    void nextTrack_unSoloBrano_restituisceStessoBrano() {
        List<Track> queue = List.of(track1);
        assertEquals(track1, strategy.nextTrack(queue, track1));
    }

    @Test
    void nextTrack_piuBrani_nonRestituisceCorrenteQuasiMai() {
    List<Track> queue = List.of(track1, track2, track3);
    //eseguo il controllo 20 volte perchè lo reputo un numero
    //sufficiente per verificare che non restituisca sempre lo stesso brano
    for (int i = 0; i < 20; i++) {
        assertNotEquals(track1, strategy.nextTrack(queue, track1));
    }
    }

    @Test
    void nextTrack_piuBrani_restituisceBranoValido() {
        List<Track> queue = List.of(track1, track2, track3);
        assertNotEquals(track1, strategy.nextTrack(queue, track1));
    }

    // previoustrack anche se si comporta come next altrimenti 
    //non sarebbe shuffle

    @Test
    void previousTrack_comportamentoUgualeANextTrack() {
        List<Track> queue = List.of(track1, track2, track3);
        for (int i = 0; i < 20; i++) {
            assertNotEquals(track1, strategy.previousTrack(queue, track1));
        }
    }
}
