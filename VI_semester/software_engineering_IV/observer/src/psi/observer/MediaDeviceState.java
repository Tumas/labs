package psi.observer;

import java.util.ArrayList;

public class MediaDeviceState {
	private boolean isPlaying;
	private int mediasPlayed;
	private Object current;
	private ArrayList<?> queue = new ArrayList<Object>();

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void setMediasPlayed(int mediasPlayed) {
		this.mediasPlayed = mediasPlayed;
	}
	
	public int getMediasPlayed() {
		return mediasPlayed;
	}

	public void setCurrent(Object current) {
		this.current = current;
	}

	public Object getCurrent() {
		return current;
	}

	public void setQueue(ArrayList<?> queue) {
		this.queue = queue;
	}

	public ArrayList<?> getQueue() {
		return queue;
	}
}