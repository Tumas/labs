package psi.observer.assets;

public class Video {
	private String title;
	private int length;

	public Video(String title, int length){
		setTitle(title);
		setLength(length);
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String toString(){
		return getTitle();
	}
}
