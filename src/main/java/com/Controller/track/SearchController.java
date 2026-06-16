package com.Controller.track;

import com.Model.Track;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * @brief Controller responsabile della ricerca e del filtraggio delle tracce
 *        nella TableView. Opera sulla lista originale senza modificarla,
 *        applicando un filtro reattivo tramite {@code ObservableList.filtered}.
 */
public class SearchController {

    /** Lista originale non modificata, usata come sorgente per il filtro. */
    private ObservableList<Track> originalList;

    /** Tabella grafica su cui vengono mostrati i risultati filtrati. */
    private TableView<Track> trackableTable;

    /** Riferimento al campo di testo della barra di ricerca. */
    private TextField boundField;

    /**
     * @brief Inizializza il controller con i riferimenti necessari al
     *        funzionamento della ricerca. Deve essere chiamato prima di
     *        qualsiasi altro metodo.
     *
     * @param originalList Lista osservabile di tracce da filtrare. Non deve essere null.
     * @param trackTable   TableView su cui visualizzare i risultati. Non deve essere null.
     * @throws IllegalArgumentException se uno dei parametri è null.
     */
    public void init(ObservableList<Track> originalList, TableView<Track> trackTable) {
        if (originalList == null || trackTable == null) {
            throw new IllegalArgumentException("I parametri di init non possono essere null.");
        }
        this.originalList = originalList;
        this.trackableTable = trackTable;
    }

    /**
     * @brief Filtra la lista di tracce in base al testo fornito e aggiorna la
     *        TableView. La ricerca è case-insensitive e confronta il testo con
     *        titolo, autore e genere di ogni traccia. I campi null vengono
     *        trattati come stringhe vuote per evitare NullPointerException.
     *        Se il testo è null o vuoto, ripristina la lista completa.
     *
     * @param text Stringa di ricerca inserita dall'utente. Può essere null o vuota.
     * @throws IllegalStateException se {@code init} non è stato ancora chiamato.
     */
    public void handleSearch(String text) {
        if (originalList == null || trackableTable == null) {
            throw new IllegalStateException("SearchController non è stato inizializzato. Chiamare init() prima di handleSearch().");
        }

        if (text == null || text.isBlank()) {
            trackableTable.setItems(originalList);
            return;
        }

        String lowerText = text.toLowerCase();

        ObservableList<Track> filteredList = originalList.filtered(track ->
            safeGet(track.getTitle()).contains(lowerText) ||
            safeGet(track.getAuthor()).contains(lowerText) ||
            safeGet(track.getGenre()).contains(lowerText)
        );

        trackableTable.setItems(filteredList);
    }

    /**
     * @brief Converte una stringa in minuscolo in modo sicuro.
     *        Restituisce una stringa vuota se il valore è null, evitando
     *        NullPointerException nel predicate di filtraggio.
     *
     * @param value Stringa da convertire. Può essere null.
     * @return La stringa in minuscolo, oppure {@code ""} se null.
     */
    private String safeGet(String value) {
        return value == null ? "" : value.toLowerCase();
    }




    /**
     * @brief Collega il campo di testo alla logica di ricerca in modo dinamico.
     *        Ad ogni carattere digitato dall'utente viene invocato {@code handleSearch},
     *        aggiornando la TableView senza bisogno di premere alcun pulsante.
     *
     * @param field Il TextField della barra di ricerca. Non deve essere null.
     * @throws IllegalArgumentException se {@code field} è null.
     */
    public void bindSearchField(TextField field) {
        if (field == null) {
            throw new IllegalArgumentException("Il campo di ricerca non può essere null.");
        }
        this.boundField = field;
        field.textProperty().addListener((observable, oldValue, newValue) -> handleSearch(newValue));
    }

    /**
     * @brief Cambia il contesto di ricerca (lista e tabella target) e pulisce
     *        il campo di testo. Da chiamare quando si entra o si esce da una
     *        playlist, per redirigere il filtro sulla lista corretta.
     *
     * @param list  La nuova lista osservabile da filtrare. Non deve essere null.
     * @param table La nuova TableView su cui mostrare i risultati. Non deve essere null.
     */
    public void resetContext(ObservableList<Track> list, TableView<Track> table) {
        init(list, table);
        trackableTable.setItems(originalList);
        if (boundField != null) {
            boundField.clear();
        }
    }
}
