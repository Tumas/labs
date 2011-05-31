package psi.observer.musicServer;

import java.util.ArrayList;
import java.util.Observable;

import psi.observer.MediaDeviceState;
import psi.observer.assets.Song;

public class MusicServer extends Observable {
	private boolean isPlaying = false;
	private Song current;
	private ArrayList<Song> queue = new ArrayList<Song>();

	private int songsPlayed = 0;
	
	public void addSongToQueue(Song s) {
		queue.add(s);
		setChanged();
		notifyObservers(captureMedaDeviceState());
	}

	public Song play() {
		if (!queue.isEmpty()){
			current = queue.remove(0);
	
			isPlaying = true;
			songsPlayed += 1;
			
			setChanged();
			notifyObservers(captureMedaDeviceState());
		}
		return current;
	}

	private MediaDeviceState captureMedaDeviceState() {
		MediaDeviceState st = new MediaDeviceState();
		
		st.setCurrent(current);
		st.setMediasPlayed(songsPlayed);
		st.setQueue(queue);
		st.setPlaying(isPlaying);
		
		return st;
	}

	public boolean removeFromQueue(Song s) {
		if (!queue.isEmpty()){
			boolean val = queue.remove(s);
			setChanged();
			notifyObservers(captureMedaDeviceState());
			return val;
		}
		return false;
	}

	public void stop() {
		isPlaying = false;
		setChanged();
		notifyObservers(captureMedaDeviceState());

	}
	
	public Song getCurrentSong(){
		return this.current;
	}

	public MusicServerMemento createMemento(){
		MusicServerMemento memento = new MusicServerMemento();
		
		memento.current = current;
		memento.isPlaying = isPlaying;
		
		return memento;
	}
	
	public void setMemento(MusicServerMemento memento){
		current = memento.current;
		isPlaying = memento.isPlaying;
	}
	
	/*
	 *  Memento == MusicServerState | VideoServerState
	 *  Command before executing asks for memento and stores it 
	 *  Upon undoing command, command gives memento back to model
	 *  MusicServerMemento has package access -> so wide interface is seen to musicServer but not to command
	 */
}