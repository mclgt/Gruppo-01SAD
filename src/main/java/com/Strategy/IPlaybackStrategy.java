package com.Strategy;

import java.util.List;

import com.Model.Track;

/**
 * @interface IPlaybackStrategy
 * @brief Strategia di riproduzione per una coda di brani.
 *
 * Definisce il contratto per le strategie di navigazione tra i brani
 * (sequenziale, loop, shuffle, ecc.).
 */
public interface IPlaybackStrategy {

    /**
     * @brief Inizializza la strategia con una nuova coda di riproduzione e il brano corrente.
     * @param queue La lista di brani che costituisce la coda di riproduzione.
     * @param currentTrack Il brano dal quale parte la riproduzione o attualmente attivo.
     */
    void setQueue(List<Track> queue, Track currentTrack);

    /**
     * @brief Aggiorna dinamicamente la coda di riproduzione interna mantenendo la consistenza dello stato.
     * Permette di sincronizzare la strategia in caso di aggiunta, rimozione o riarrangiamento
     * dei brani nella playlist anche durante la riproduzione attiva.
     * @param updatedQueue La nuova lista aggiornata di brani.
     */
    void updateQueue(List<Track> updatedQueue);
    
    /**
     * @brief Restituisce il brano successivo nella coda secondo la strategia corrente.
     *
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, o {@code null} se la riproduzione deve fermarsi.
     */

    Track nextTrack(Track current);

   /**
     * @brief Restituisce il brano precedente nella coda secondo la strategia corrente.
     *
     * @param current Brano attualmente in riproduzione.
     * @return Il brano precedente, o {@code null} se la riproduzione deve fermarsi.
     */

    Track previousTrack(Track current);
}
