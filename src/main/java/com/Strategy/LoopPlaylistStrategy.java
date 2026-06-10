package com.Strategy;

import com.Model.Track;
import java.util.List;

/**
 * @class LoopPlaylisrStrategy
 * @brief Strategia di riproduzione in loop sulla playlist corrente.
 *
 *        I brani vengono riprodotti sequenzialmente. Arrivati all'ultimo brano,
 *        il comando "successivo" riparte dal primo. Quando ci si trova nel
 *        primo brano il camndo "precedente" va all'ultimo.
 */
public class LoopPlaylistStrategy implements IPlaybackStrategy {

    /**
     * @brief Restituisce il brano successivo (con loop all'inizio se si è alla
     *        fine).
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return Il brano successivo, o il primo se current è l'ultimo.
     */
    @Override
    public Track nextTrack(List<Track> queue, Track current) {
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        if (current == null) {
            return queue.get(0);
        }
        int currentIndex = queue.indexOf(current);
        if (currentIndex == -1 || currentIndex == queue.size() - 1) {
            return queue.get(0);
        }
        int nextIndex = (currentIndex + 1) % queue.size();
        return queue.get(nextIndex);
    }

    /**
     * @brief Restituisce il brano precedente, con loop alla fine se si è
     *        all'inizio.
     *
     * @param queue   Lista ordinata dei brani nella coda di riproduzione.
     * @param current Brano attualmente in riproduzione.
     * @return IL brano precedente, o l'ultimo se current è il primo.
     */
    @Override
    public Track previousTrack(List<Track> queue, Track current) {
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        if (current == null) {
            return queue.get(0);
        }
        int currentIndex = queue.indexOf(current);
        if (currentIndex == -1 || currentIndex == queue.size() - 1) {
            return queue.get(0);
        }
        int prevIndex = (currentIndex - 1 + queue.size()) % queue.size();
        return queue.get(prevIndex);
    }

}
