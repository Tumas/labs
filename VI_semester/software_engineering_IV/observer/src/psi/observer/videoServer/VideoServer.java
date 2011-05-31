package psi.observer.videoServer;

import java.util.ArrayList;
import java.util.Observable;

import psi.observer.MediaDeviceState;
import psi.observer.assets.Video;

public class VideoServer extends Observable {
	private boolean isPlaying = false;
	private int videosPlayed = 0;
	private Video current;
	private ArrayList<Video> queue = new ArrayList<Video>();

	public void addVideoToQueue(Video v) {
		queue.add(v);
		setChanged();
		notifyObservers(captureMedaDeviceState());
	}

	public Video play() {
		if (!queue.isEmpty()){
			current = queue.remove(0);
			isPlaying = true;
			videosPlayed += 1;

			setChanged();
			notifyObservers(captureMedaDeviceState());
		}
		return current;
	}


	private MediaDeviceState captureMedaDeviceState() {
		MediaDeviceState st = new MediaDeviceState();
		
		st.setCurrent(current);
		st.setMediasPlayed(videosPlayed);
		st.setQueue(queue);
		st.setPlaying(isPlaying);
		
		return st;
	}

	public boolean removeFromQueue(Video v) {
		if (!queue.isEmpty()){
			boolean val = queue.remove(v);
		
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
	
	public Video getCurrentVideo(){
		return this.current;
	}

	public VideoServerMemento createMemento(){
		VideoServerMemento memento = new VideoServerMemento();
		
		memento.current = current;
		memento.isPlaying = isPlaying;
		
		return memento;
	}
	
	public void setMemento(VideoServerMemento memento){
		current = memento.current;
		isPlaying = memento.isPlaying;
	}
}