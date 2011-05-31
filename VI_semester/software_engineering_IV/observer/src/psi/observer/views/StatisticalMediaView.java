package psi.observer.views;

import psi.observer.MediaDeviceState;

public class StatisticalMediaView extends MediaView {

	@Override
	public String display(MediaDeviceState state) {
		String output = "";
		
		output += "Total plays: " + state.getMediasPlayed() + "\n";
		output += "Queue size: " + state.getQueue().size() + "\n";
		
		System.out.println(output);
		return output;
	}
}
