package com.Model;

/**
 * @brief Enumerazione che rappresenta i tag visivi applicabili a una traccia musicale. 
 */
public enum TrackTag {
    NONE(" "),
    FAVOURITE("Preferita"),
    EXPLICIT("Esplicita"),
    NEW_RELEASE("Novità");

    private final String tagName;

    /**
     * @brief Costruttore privato dell'enumerazione TrackTag
     * @param tagName
     */
    TrackTag(String tagName) {
        this.tagName = tagName;
    }

    /**
     * @brief Restituisce il nome leggibile associato al tag
     * @return Stringa contenente la descrizione testuale del tag
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * @brief Converte l'elemento dell'enum nella usa rappresentazione testuale
     * @return Il nome testuale leggibile del tag
     */
    @Override
    public String toString() {
        return tagName;
    }

}
