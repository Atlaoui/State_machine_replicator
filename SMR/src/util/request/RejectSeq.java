package util.request;

import java.util.List;

import util.messages.Message;

public class RejectSeq extends Message{
	private Request seq;

	public RejectSeq(long idsrc, long iddest , Request seq) {
		super(idsrc, iddest);
		this.seq=seq;

	}
	public Request getRequest() {
		return seq;
	}

}