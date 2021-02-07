package util.request;

import util.messages.Message;

public class RunSequenceAgain extends Message{
	private Request r;
	public RunSequenceAgain(long idsrc, long iddest , Request r) {
		super(idsrc, iddest);
		this.r = r;
	}
	public Request getRquest() {
		return r;
	}
}
