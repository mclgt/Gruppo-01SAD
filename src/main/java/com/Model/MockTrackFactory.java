package com.Model;

public class MockTrackFactory extends TrackFactory {
    @Override
    public Track instantiateTrack(String title, String author, int year, String genre, int duration, String album,
            String filePath, TrackTag tag) {
        return new Track(title.trim(), author.trim(), year, genre.trim(), duration, album.trim(), filePath.trim(), tag);
    }
}
