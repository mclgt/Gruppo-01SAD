package com.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AutoPlaylistServiceTest {

    private TrackFactory factory;
    private List<Track> library;

    @BeforeEach
    public void setUp() {
        factory = new MockTrackFactory();
        library = new ArrayList<>();
        library.add(factory.createTrack("Bohemian Rhapsody", "Queen",    1975, "Rock",  354, "A Night at the Opera", "t1.wav", TrackTag.FAVOURITE));
        library.add(factory.createTrack("Smells Like Teen Spirit", "Nirvana", 1991, "Rock", 301, "Nevermind",          "t2.wav", TrackTag.EXPLICIT));
        library.add(factory.createTrack("Volare",  "Modugno",  1958, "Pop",  185, "Volare",   "t3.wav", TrackTag.NONE));
        library.add(factory.createTrack("Shape of You", "Ed Sheeran", 1991, "Pop", 234, "Divide", "t4.wav", TrackTag.NEW_RELEASE));
        library.add(factory.createTrack("Hotel California", "Eagles", 1977, "Rock", 391, "Hell Freezes Over", "t5.wav", TrackTag.NONE));
    }

    // ── filtro anno ──────────────────────────────────────────────────────────

    @Test
    public void testFilter_byYear_returnsMatchingTracks() {
        List<Track> result = AutoPlaylistService.filter(library, "1991", AutoPlaylistService.ANY, AutoPlaylistService.ANY);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getYear() == 1991));
    }

    @Test
    public void testFilter_byYear_noMatch_returnsEmpty() {
        List<Track> result = AutoPlaylistService.filter(library, "2000", AutoPlaylistService.ANY, AutoPlaylistService.ANY);
        assertTrue(result.isEmpty());
    }

    // ── filtro genere ─────────────────────────────────────────────────────────

    @Test
    public void testFilter_byGenre_caseInsensitive() {
        List<Track> result = AutoPlaylistService.filter(library, AutoPlaylistService.ANY, "rock", AutoPlaylistService.ANY);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(t -> t.getGenre().equalsIgnoreCase("Rock")));
    }

    @Test
    public void testFilter_byGenre_noMatch_returnsEmpty() {
        List<Track> result = AutoPlaylistService.filter(library, AutoPlaylistService.ANY, "Jazz", AutoPlaylistService.ANY);
        assertTrue(result.isEmpty());
    }

    // ── filtro tag ────────────────────────────────────────────────────────────

    @Test
    public void testFilter_byTag_favourite() {
        List<Track> result = AutoPlaylistService.filter(library, AutoPlaylistService.ANY, AutoPlaylistService.ANY, "Preferita");
        assertEquals(1, result.size());
        assertEquals(TrackTag.FAVOURITE, result.get(0).getTag());
    }

    @Test
    public void testFilter_byTag_none_matchesTracksWithNoTag() {
        List<Track> result = AutoPlaylistService.filter(library, AutoPlaylistService.ANY, AutoPlaylistService.ANY, AutoPlaylistService.TAG_NONE_LABEL);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getTag() == TrackTag.NONE));
    }

    // ── filtro combinato (AND) ────────────────────────────────────────────────

    @Test
    public void testFilter_combinedYearAndGenre() {
        List<Track> result = AutoPlaylistService.filter(library, "1991", "Rock", AutoPlaylistService.ANY);
        assertEquals(1, result.size());
        assertEquals("Smells Like Teen Spirit", result.get(0).getTitle());
    }

    @Test
    public void testFilter_allAny_returnsAll() {
        List<Track> result = AutoPlaylistService.filter(library,
                AutoPlaylistService.ANY, AutoPlaylistService.ANY, AutoPlaylistService.ANY);
        assertEquals(library.size(), result.size());
    }

    @Test
    public void testFilter_emptyLibrary_returnsEmpty() {
        List<Track> result = AutoPlaylistService.filter(new ArrayList<>(), "1991", "Rock", AutoPlaylistService.ANY);
        assertTrue(result.isEmpty());
    }

    // ── validazione nome ──────────────────────────────────────────────────────

    @Test
    public void testValidateName_valid_doesNotThrow() {
        assertDoesNotThrow(() -> AutoPlaylistService.validateName("Rock anni 90", new ArrayList<>()));
    }

    @Test
    public void testValidateName_empty_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AutoPlaylistService.validateName("", new ArrayList<>()));
        assertTrue(ex.getMessage().contains("vuoto"));
    }

    @Test
    public void testValidateName_tooLong_throws() {
        String longName = "A".repeat(AutoPlaylistService.MAX_NAME_LENGTH + 1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AutoPlaylistService.validateName(longName, new ArrayList<>()));
        assertTrue(ex.getMessage().contains("superare"));
    }

    @Test
    public void testValidateName_invalidChars_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AutoPlaylistService.validateName("Playlist/Vietata", new ArrayList<>()));
        assertTrue(ex.getMessage().contains("non ammessi"));
    }

    @Test
    public void testValidateName_duplicate_throws() {
        List<Playlist> existing = List.of(new Playlist("Rock anni 90"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> AutoPlaylistService.validateName("rock anni 90", existing));
        assertTrue(ex.getMessage().contains("già una playlist"));
    }

    @Test
    public void testValidateName_maxLengthExact_doesNotThrow() {
        String name = "A".repeat(AutoPlaylistService.MAX_NAME_LENGTH);
        assertDoesNotThrow(() -> AutoPlaylistService.validateName(name, new ArrayList<>()));
    }
}
