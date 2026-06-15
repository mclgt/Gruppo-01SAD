package com.Command;

import com.Model.Track;

import javafx.collections.ObservableList;
public class MoveDownTrack implements ICommand{
    private ObservableList<Track> list;
    private Track track;
    private int index;

    public MoveDownTrack(ObservableList <Track> list, Track track){
        this.list=list;
        this.track=track;
    }

    @Override
    public void execute(){
        int currentIndex=list.indexOf(track);
        if(currentIndex >=0 && currentIndex < list.size()-1){
            this.index=currentIndex;
            java.util.Collections.swap(list, currentIndex, currentIndex + 1);
        }
    }

    @Override
    public void undo(){
        if(index >= 0 && index < list.size()-1){
            java.util.Collections.swap(list,index + 1,index);
        }
    }
    
}
