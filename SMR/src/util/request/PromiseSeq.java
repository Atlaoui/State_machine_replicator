package util.request;



import util.messages.Message;

public class PromiseSeq extends Message implements RequestMessage{
	private Request seq;
	private int roundId;
	public PromiseSeq(long idsrc, long iddest , int roundId ,Request seq) {
		super(idsrc, iddest);
		this.seq=seq;
		this.roundId=roundId;
	}
	public Request getRequest() {
		return seq;
	}
	public int getRoundId() {
		return roundId;
	}
}
