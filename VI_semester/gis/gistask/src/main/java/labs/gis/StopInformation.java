package labs.gis;

import org.opengis.feature.Feature;

public class StopInformation {
	private Feature from;
	private Feature destination;
	private Double distance;
	
	public StopInformation(Feature from, Feature dest, Double distance){
		this.setFrom(from);
		this.setDestination(dest);
		this.setDistance(distance);
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDestination(Feature destination) {
		this.destination = destination;
	}

	public Feature getDestination() {
		return destination;
	}

	public void setFrom(Feature from) {
		this.from = from;
	}

	public Feature getFrom() {
		return from;
	}
}
