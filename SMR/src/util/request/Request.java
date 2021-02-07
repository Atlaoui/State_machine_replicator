package util.request;

public abstract class Request {
	private final long idsrc;
	private final long iddest;
	private static long cpt = 0;
	private final long ID;

	public Request(long idsrc, long iddest){
		this.iddest=iddest;
		this.idsrc=idsrc;
		ID = cpt++;
	}
	public long getID() {return ID;}
	public long getIdSrc() {return idsrc;}
	public long getIdDest() {return iddest;}
	
	@Override
	public String toString() {
		return "Request [ID=" + ID + "]";
	}
}
