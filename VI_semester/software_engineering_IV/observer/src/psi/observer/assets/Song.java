package psi.observer.assets;

public class Song {
	private String title;
	private String artist;
	private int length;

	public Song(String title, String artist, int length){
		setTitle(title);
		setArtist(artist);
		setLength(length);
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString(){
		return getArtist() + " - " + getTitle();
	}
}