package util.request;

public abstract class Request {
	private final long idsrc;
	private final long iddest;
	public long getIdSrc() {return idsrc;}
	public long getIdDest() {return iddest;}
	public Request(long idsrc, long iddest){
		this.iddest=iddest;
		this.idsrc=idsrc;
	}
}
