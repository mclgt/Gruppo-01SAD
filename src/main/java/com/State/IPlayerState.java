package com.State;

import com.Model.Track;

/**
 * @interface IPlayerState
 * @brief Interfaccia del pattern State per il player musicale.
 *
 *        Definisce il contratto che ogni stato concreto del player deve
 *        implementare. Le operazioni di riproduzione vengono delegate dallo
 *        stato attivo, consentendo comportamenti diversi a seconda che il
 *        player sia in riproduzione, in pausa o fermo.
 *
 * @see PlayerContext
 * @see PlayingState
 * @see PausedState
 */
public interface IPlayerState {

    /**
     * @brief Avvia la riproduzione del brano specificato.
     * @param track Il brano da riprodurre.
     */
    void play(Track track);

    /**
     * @brief Mette in pausa la riproduzione corrente.
     *        Il comportamento varia in base allo stato: da PlayingState
     *        transisce a PausedState; da PausedState è un no-op.
     */
    void pause();

    /**
     * @brief Ferma definitivamente la riproduzione.
     *        Ogni stato implementa la logica di arresto appropriata.
     */
    void stop();

    /**
     * @brief Avanza al brano successivo secondo la strategia attiva.
     *        Se non esiste un brano successivo, la riproduzione si arresta.
     */
    void next();

    /**
     * @brief Torna al brano precedente secondo la strategia attiva.
     *        Se non esiste un brano precedente, la riproduzione si arresta.
     */
    void previous();
}
