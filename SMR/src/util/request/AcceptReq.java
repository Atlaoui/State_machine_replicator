package util.request;

import util.messages.Message;

public class AcceptReq  extends Message{
	private Request seq;
	private int roundId;
	public AcceptReq(long idsrc, long iddest , int roundId , Request seq) {
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