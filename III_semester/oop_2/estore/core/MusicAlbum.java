/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package estore.core;

import java.util.ArrayList;
import java.math.BigDecimal;

/**
 *
 * @author hanniph
 */
public class MusicAlbum extends Item
{
    protected ArrayList<String> trackList;
    protected String artist;

    public MusicAlbum(String title, String artist, String description, BigDecimal price, int quantity,
         ArrayList<String> trackList){
            super(title, description, price, quantity);
            setArtist(artist);
            setTrackList(trackList);
    }

    //GETTERS and SETTERS
    public ArrayList<String> getTrackList(){
        return trackList;
    }

    public String getArtist(){
        return artist;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getTrack(int index){
        return getTrackList().get(index);
    }

    public int getTrackListSize(){
        return getTrackList().size();
    }

    public boolean addTrack(String track){
        if (getTrackList() == null)
            setTrackList(new ArrayList<String>());

        return getTrackList().add(track);
    }

    public void setTrackList(ArrayList<String> trackList){
        this.trackList = trackList;
    }

    //Special methods
    @Override public String toString(){
        return getArtist() + getTitle();
    }

    //methods from the abstract class
    public String generateDescription(){
        String oldDescription = getDescription();
        String newDescription = new String(this.toString() + "\nTracklist: \n");

        int size = getTrackListSize();
        for (int i = 0; i < size; i++){
            newDescription += (getTrack(i) + "\n");
        }
        setDescription(newDescription);
        return oldDescription;
    }
}
