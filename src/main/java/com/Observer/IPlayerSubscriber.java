package com.Observer;

import com.Model.Track;

/**
 * @interface IPLayerSubscriber
 * @brief Interfaccia per gli oggetti che vogliono essere notificati dei
 *        cambiamenti nello stato di riproduzione del player. Implementa il
 *        pattern Observer.
 */
public interface IPlayerSubscriber {

    /**
     * @brief Metodo richiamato quando la traccia in riproduzione viene aggiornata.
     *        Consente ai Subscriber di reagire al cambiamento, aggiornando
     *        l'interfaccia grafica.
     * 
     * @param newTrack nuova traccia impostata come corrente nel player (null se la
     *                 riproduzione è stata fermata)
     */
    void onPlaybackChanged(Track newTrack);
}