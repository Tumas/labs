package psi.observer.views;

import java.util.Observable;
import java.util.Observer;

import psi.observer.MediaDeviceState;

public abstract class MediaView implements Observer {

	@Override
	public void update(Observable o, Object arg) {
		display((MediaDeviceState) arg);
	}

	public abstract String display(MediaDeviceState state);
}
