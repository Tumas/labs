package psi.observer.views;

import psi.observer.MediaDeviceState;

public class CurrentStatusMediaView extends MediaView {

		@Override
	public String display(MediaDeviceState state) {
		String output = "";

		if (state.isPlaying())
			output += "Playing: " + state.getCurrent() + "\n";
		else
			output += "Stopped.\n";
		
		output += "Files in line:  " + state.getQueue().size() + "\n";
		for (Object obj : state.getQueue()){
			output += "\t" + obj + "\n";
		}
		
		System.out.println(output);
		return output;
	}
}
