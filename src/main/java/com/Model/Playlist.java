package com.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private StringProperty name;
    private ObservableList<Track> tracks;

    public Playlist(String name){
        this.name = new SimpleStringProperty(name);
        this.tracks = FXCollections.observableArrayList();
    }

    public void setName(String name){
        this.name.set(name);
    }

    public String getName(){
        return name.get();
    }

    public void addTrack(Track track){
        if(track != null && !tracks.contains(track)){
            tracks.add(track);
        }
    }

    public Track removeTrack(Track track){
        if(tracks.remove(track)){
            return track;
        }

        return null;
    }

    public Track removeTrack(int index){
        if(index >= 0 && index < tracks.size()){
            return tracks.remove(index);
        }
        return null;
    }

    public ObservableList<Track> getTracks(){
        return tracks;
    }

    public int getTotalDuration(){
        int total = 0;
        for(Track track : tracks){
            total += track.getDuration();
        }
        
        return total;
    }
    public String getFormattedTotalDuration(){
        int total = this.getTotalDuration();
        int hours = total / 3600;
        int minutes = (total % 3600) / 60;
        int seconds = total % 60;

        if(hours > 0){
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }else{
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    @Override
    public String toString() {
        return name + " (" + tracks.size() + " brani)";
    }
}