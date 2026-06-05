package com.Command;
import com.Model.Playlist;
public class RemovePlaylist implements ICommand {
    private Playlist playlist;
    public RemovePlaylist(Playlist playlist){
        this.playlist = playlist;
    }
    @Override
    public void execute() {
        
    }

    @Override
    public void undo() {
        // TODO Auto-generated method stub
        
    }
    
}
