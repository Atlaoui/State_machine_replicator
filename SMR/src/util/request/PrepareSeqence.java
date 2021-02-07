package util.request;

import java.util.List;

import util.messages.Message;

public class PrepareSeqence extends Message implements RequestMessage{
	private Request seq;
	private int roundId;
	public PrepareSeqence(long idsrc, long iddest, int roundId , Request seq) {
		super(idsrc, iddest);
		this.seq=seq;
		this.roundId=roundId;
	}
	public Request getSeq() {
		return seq;
	}
	
	public int getRoundId() {
		return roundId;
	}
	@Override
	public Request getRequest() {
		// TODO Auto-generated method stub
		return seq;
	}

}
