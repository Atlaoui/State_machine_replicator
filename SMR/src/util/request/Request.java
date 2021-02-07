package util.request;

public abstract class Request {
	private static long cpt = 0;
	private final long ID;

	public Request(){
		ID = cpt++;
	}
	public long getID() {return ID;}
	
	@Override
	public String toString() {
		return "Request [ID=" + ID + "]";
	}
}
