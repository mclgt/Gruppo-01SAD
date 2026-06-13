package com.Controller.track;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.Model.MockTrackFactory;
import com.Model.Track;
import com.Model.TrackFactory;
import com.Model.TrackTag;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * @brief Test per la classe SearchController. Verifica la corretta
 *        inizializzazione, il filtraggio delle tracce per titolo, autore e
 *        genere, la gestione dei casi limite (testo null, vuoto, nessun
 *        risultato) e il comportamento di resetContext e bindSearchField.
 */
public class SearchControllerTest {

    private SearchController searchController;
    private ObservableList<Track> trackList;
    private TableView<Track> tableView;
    private TrackFactory factory;

    /**
     * @brief Avvia il toolkit JavaFX prima di tutti i test. Necessario per
     *        poter istanziare componenti JavaFX come TableView e TextField
     *        al di fuori di un'applicazione grafica in esecuzione.
     *        Se il toolkit è già attivo, l'eccezione viene ignorata.
     *
     * @throws InterruptedException se il thread viene interrotto durante l'attesa.
     */
    @BeforeAll
    static void avviaJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await(5, TimeUnit.SECONDS);
    }

    /**
     * @brief Prepara l'ambiente di test prima di ogni caso.
     *        Crea una lista di tre tracce con titoli, autori e generi distinti
     *        e inizializza il SearchController con questa lista e una TableView vuota.
     */
    @BeforeEach
    void setUp() {
        factory = new MockTrackFactory();
        searchController = new SearchController();
        tableView = new TableView<>();
        trackList = FXCollections.observableArrayList();

        trackList.add(factory.instantiateTrack("Bohemian Rhapsody", "Queen",           1975, "Rock", 354, "A Night at the Opera", "dummy.mp3", TrackTag.NONE));
        trackList.add(factory.instantiateTrack("Billie Jean",        "Michael Jackson", 1982, "Pop",  294, "Thriller",             "dummy.mp3", TrackTag.NONE));
        trackList.add(factory.instantiateTrack("Superstition",       "Stevie Wonder",   1972, "Funk", 245, "Talking Book",         "dummy.mp3", TrackTag.NONE));

        searchController.init(trackList, tableView);
    }

    // ---------------------------------------------------------------
    // Test su init()
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che init() lanci IllegalArgumentException se la lista
     *        passata è null.
     */
    @Test
    void testInit_listaNull_lanciaEccezione() {
        SearchController sc = new SearchController();
        assertThrows(IllegalArgumentException.class, () -> sc.init(null, tableView));
    }

    /**
     * @brief Verifica che init() lanci IllegalArgumentException se la TableView
     *        passata è null.
     */
    @Test
    void testInit_tabellaNull_lanciaEccezione() {
        SearchController sc = new SearchController();
        assertThrows(IllegalArgumentException.class, () -> sc.init(trackList, null));
    }

    // ---------------------------------------------------------------
    // Test su handleSearch() - stato non inizializzato
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che handleSearch() lanci IllegalStateException se viene
     *        chiamato prima di init().
     */
    @Test
    void testHandleSearch_primaDelInit_lanciaEccezione() {
        SearchController sc = new SearchController();
        assertThrows(IllegalStateException.class, () -> sc.handleSearch("rock"));
    }

    // ---------------------------------------------------------------
    // Test su handleSearch() - testo null o vuoto
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che passare null come testo ripristini la lista completa
     *        nella TableView.
     */
    @Test
    void testHandleSearch_testoNull_ripristinaListaCompleta() {
        searchController.handleSearch(null);
        assertEquals(trackList, tableView.getItems());
    }

    /**
     * @brief Verifica che passare una stringa vuota ripristini la lista completa
     *        nella TableView.
     */
    @Test
    void testHandleSearch_testoVuoto_ripristinaListaCompleta() {
        searchController.handleSearch("");
        assertEquals(trackList, tableView.getItems());
    }

    /**
     * @brief Verifica che passare una stringa di soli spazi ripristini la lista
     *        completa nella TableView.
     */
    @Test
    void testHandleSearch_testoBianco_ripristinaListaCompleta() {
        searchController.handleSearch("   ");
        assertEquals(trackList, tableView.getItems());
    }

    // ---------------------------------------------------------------
    // Test su handleSearch() - filtro per campo
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che la ricerca per titolo restituisca solo il brano
     *        il cui titolo contiene il testo cercato.
     */
    @Test
    void testHandleSearch_perTitolo_trovaCorrispondenza() {
        searchController.handleSearch("Bohemian");
        assertEquals(1, tableView.getItems().size());
        assertEquals("Bohemian Rhapsody", tableView.getItems().get(0).getTitle());
    }

    /**
     * @brief Verifica che la ricerca per autore restituisca solo il brano
     *        il cui autore contiene il testo cercato.
     */
    @Test
    void testHandleSearch_perAutore_trovaCorrispondenza() {
        searchController.handleSearch("Queen");
        assertEquals(1, tableView.getItems().size());
        assertEquals("Queen", tableView.getItems().get(0).getAuthor());
    }

    /**
     * @brief Verifica che la ricerca per genere restituisca solo il brano
     *        il cui genere contiene il testo cercato.
     */
    @Test
    void testHandleSearch_perGenere_trovaCorrispondenza() {
        searchController.handleSearch("Pop");
        assertEquals(1, tableView.getItems().size());
        assertEquals("Billie Jean", tableView.getItems().get(0).getTitle());
    }

    // ---------------------------------------------------------------
    // Test su handleSearch() - case-insensitive
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che la ricerca sia case-insensitive: cercare "queen"
     *        in minuscolo deve trovare "Queen".
     */
    @Test
    void testHandleSearch_caseInsensitive_trovaCorrispondenza() {
        searchController.handleSearch("queen");
        assertEquals(1, tableView.getItems().size());
        assertEquals("Queen", tableView.getItems().get(0).getAuthor());
    }

    // ---------------------------------------------------------------
    // Test su handleSearch() - casi limite
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che la ricerca con un testo che non corrisponde a nessun
     *        brano restituisca una lista vuota nella TableView.
     */
    @Test
    void testHandleSearch_nessunaCorrispondenza_listaVuota() {
        searchController.handleSearch("xyz_non_esiste_123");
        assertEquals(0, tableView.getItems().size());
    }

    /**
     * @brief Verifica che la ricerca restituisca più brani quando più di uno
     *        soddisfa il criterio di filtro.
     */
    @Test
    void testHandleSearch_piuRisultati_tuttiRestituiti() {
        trackList.add(factory.instantiateTrack("Stairway to Heaven", "Led Zeppelin", 1971, "Rock", 482, "Led Zeppelin IV", "dummy.mp3", TrackTag.NONE));
        searchController.handleSearch("Rock");
        assertEquals(2, tableView.getItems().size());
    }

    // ---------------------------------------------------------------
    // Test su bindSearchField()
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che bindSearchField() lanci IllegalArgumentException
     *        se il TextField passato è null.
     */
    @Test
    void testBindSearchField_null_lanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> searchController.bindSearchField(null));
    }

    // ---------------------------------------------------------------
    // Test su resetContext()
    // ---------------------------------------------------------------

    /**
     * @brief Verifica che resetContext() cambi correttamente il contesto di
     *        ricerca: dopo il reset, handleSearch deve filtrare la nuova lista
     *        e aggiornare la nuova TableView.
     */
    @Test
    void testResetContext_cambiaListaETabella() {
        ObservableList<Track> nuovaLista = FXCollections.observableArrayList();
        nuovaLista.add(factory.instantiateTrack("Shape of You", "Ed Sheeran", 2017, "Pop", 234, "Divide", "dummy.mp3", TrackTag.NONE));
        TableView<Track> nuovaTabella = new TableView<>();

        searchController.resetContext(nuovaLista, nuovaTabella);
        searchController.handleSearch("Ed Sheeran");

        assertEquals(1, nuovaTabella.getItems().size());
        assertEquals("Shape of You", nuovaTabella.getItems().get(0).getTitle());
    }

    /**
     * @brief Verifica che resetContext() svuoti il testo del TextField collegato
     *        tramite bindSearchField(), riportandolo a stringa vuota.
     */
    @Test
    void testResetContext_pulisceTextField() {
        TextField field = new TextField("testo precedente");
        searchController.bindSearchField(field);

        searchController.resetContext(trackList, tableView);

        assertEquals("", field.getText());
    }

    /**
     * @brief Verifica che resetContext() con parametri null lanci
     *        IllegalArgumentException (delegata a init()).
     */
    @Test
    void testResetContext_parametriNull_lanciaEccezione() {
        assertThrows(IllegalArgumentException.class, () -> searchController.resetContext(null, null));
    }
}
