package com.Command;

import com.Model.Track;

import javafx.collections.ObservableList;
public class MoveUpTrack implements ICommand{
    private final ObservableList <Track> list;
    private final Track track;
    private int index=0;

    public MoveUpTrack(ObservableList <Track> list, Track track){
        this.list=list;
        this.track=track;
    }

    @Override
    public void execute(){
        int currentIndex=list.indexOf(track);
        if(currentIndex>0){
            this.index=currentIndex;
            java.util.Collections.swap(list, currentIndex, currentIndex - 1);
        }
    }

    @Override
    public void undo(){
        if(index>0){
            java.util.Collections.swap(list,index-1,index);
        }
    }
    
}
