package util.request;

import java.util.List;

import util.messages.Message;

public class PromiseSeq extends Message{
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
