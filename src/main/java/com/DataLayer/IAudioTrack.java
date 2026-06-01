package com.DataLayer;

/**
 * @brief Interfaccia comune per il pattern Proxy dedicata alla gestione audio.
 * Definisce il contratto che sia la traccia reale (RealTrack) sia il suo
 * sostituto (TrackProxy) devono rispettare per garantire l'intercambiabilità.
 *
 * @see DataLayer.TrackProxy
 * @see DataLayer.RealTrack
 */
public interface IAudioTrack {

    /**
     * @brief Carica il file audio in memoria.
     */
    void load();

    /**
     * @brief Avvia la riproduzione del brano dall'inizio.
     */
    void startPlayback();

    /**
     * @brief Interrompe la riproduzione del brano se è attualmente in esecuzione.
     */
    void stopPlayback();

    /**
     * @brief Verifica lo stato della riproduzione.
     * @return boolean True se il brano sta suonando in questo momento, False altrimenti.
     */
    boolean isPlaying();
}
