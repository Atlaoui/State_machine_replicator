package util.request;

import util.messages.Message;

public class StartSequentialPaxos extends Message implements RequestMessage{
	public StartSequentialPaxos(long idsrc, long iddest) {
		super(idsrc, iddest);
	}
}
