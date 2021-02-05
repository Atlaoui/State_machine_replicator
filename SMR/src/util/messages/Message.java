package util.messages;

public class Message {
	private final long idsrc;
	private final long iddest;
	public long getIdSrc() {return idsrc;}
	public long getIdDest() {return iddest;}
	public Message(long idsrc, long iddest){
		this.iddest=iddest;
		this.idsrc=idsrc;
	}
}
