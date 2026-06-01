package com.DataLayer;

/**
 * @brief Implementazione del pattern Proxy per Lazy Loading dei file audio.
 *        Mantiene un riferimento al percorso del file, ma ritarda
 *        l'istanziazione
 *        dell'oggetto pesante (RealTrack) e il caricamento in RAM solo al
 *        momento
 *        dell'effettiva riproduzione del brano.
 *
 * @see DataLayer.IAudioTrack
 */
public class TrackProxy implements IAudioTrack {
    private String filePath;
    private RealTrack realTrack;

    /**
     * @brief Costruisce il Proxy fornendo solo il percorso del file.
     *        Non esegue alcun caricamento in memoria.
     *
     * @param filePath Il percorso del file audio (es. .wav o .mp3).
     */
    public TrackProxy(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @brief Instanzia la classe RealTrack e carica il file in memoria solo se non
     *        è già stato fatto.
     */
    @Override
    public void load() {
        if (realTrack == null) {
            realTrack = new RealTrack(filePath);
            realTrack.load();
        }
    }

    /**
     * @brief Delega l'avvio della riproduzione alla traccia reale.
     *        Assicura che la traccia venga caricata tramite load() prima di
     *        suonare.
     */
    @Override
    public void startPlayback() {
        load();
        realTrack.startPlayback();
    }

    /**
     * @brief Delega l'interruzione della riproduzione alla traccia reale, se questa
     *        esiste.
     */
    @Override
    public void stopPlayback() {
        if (realTrack != null) {
            realTrack.stopPlayback();
        }
    }

    /**
     * @brief Delega il controllo dello stato alla traccia reale.
     * @return boolean True se la traccia reale esiste e sta suonando.
     */
    @Override
    public boolean isPlaying() {
        if (realTrack != null) {
            return realTrack.isPlaying();
        }
        return false;
    }
}
