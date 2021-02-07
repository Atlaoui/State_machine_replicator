package util.request;

import util.messages.Message;

public class AcceptedReq  extends Message{
	private Request seq;

	public AcceptedReq(long idsrc, long iddest , Request seq) {
		super(idsrc, iddest);
		this.seq=seq;
	}
	public Request getRequest() {
		return seq;
	}

}