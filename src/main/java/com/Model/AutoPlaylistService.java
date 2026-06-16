package com.Model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief Logica pura per la generazione automatica di playlist.
 *        Filtra le tracce per anno, genere e tag (AND tra i criteri attivi).
 *        Separata dal controller per essere testabile senza JavaFX.
 */
public class AutoPlaylistService {

    public static final String ANY = "Qualsiasi";
    public static final String TAG_NONE_LABEL = "Nessuno";
    public static final int MAX_NAME_LENGTH = 50;

    private AutoPlaylistService() {}

    /**
     * @brief Filtra la lista di tracce in base ai criteri forniti.
     *        Un criterio impostato su ANY viene ignorato (nessun filtro su quel campo).
     *
     * @param tracks    lista sorgente di tracce
     * @param year      anno da filtrare, oppure ANY
     * @param genre     genere da filtrare, oppure ANY
     * @param tagLabel  etichetta del tag da filtrare, oppure ANY
     * @return lista filtrata (può essere vuota)
     */
    public static List<Track> filter(List<Track> tracks, String year, String genre, String tagLabel) {
        return tracks.stream()
                .filter(t -> matchesYear(t, year))
                .filter(t -> matchesGenre(t, genre))
                .filter(t -> matchesTag(t, tagLabel))
                .collect(Collectors.toList());
    }

    private static boolean matchesYear(Track t, String year) {
        if (ANY.equals(year)) return true;
        return t.getYear() > 0 && String.valueOf(t.getYear()).equals(year);
    }

    private static boolean matchesGenre(Track t, String genre) {
        if (ANY.equals(genre)) return true;
        return t.getGenre() != null && t.getGenre().equalsIgnoreCase(genre);
    }

    private static boolean matchesTag(Track t, String tagLabel) {
        if (ANY.equals(tagLabel)) return true;
        TrackTag trackTag = t.getTag();
        if (TAG_NONE_LABEL.equals(tagLabel)) {
            return trackTag == null || trackTag == TrackTag.NONE;
        }
        return trackTag != null && trackTag.toString().equalsIgnoreCase(tagLabel);
    }

    /**
     * @brief Valida il nome proposto per la nuova playlist.
     *
     * @param name              nome da validare
     * @param existingPlaylists lista delle playlist esistenti (controllo duplicati)
     * @throws IllegalArgumentException se il nome non è valido
     */
    public static void validateName(String name, List<Playlist> existingPlaylists) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Il nome della playlist non può essere vuoto.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Il nome non può superare " + MAX_NAME_LENGTH + " caratteri.");
        }
        if (name.matches(".*[<>:\"/\\\\|?*].*")) {
            throw new IllegalArgumentException(
                    "Il nome contiene caratteri non ammessi: < > : \" / \\ | ? *");
        }
        boolean duplicate = existingPlaylists.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "Esiste già una playlist chiamata \"" + name + "\".");
        }
    }
}
