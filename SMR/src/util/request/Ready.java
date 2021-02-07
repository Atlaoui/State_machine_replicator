package util.request;

import util.messages.Message;

public class Ready extends Message implements RequestMessage{

	public Ready(long idsrc, long iddest) {
		super(idsrc, iddest);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Request getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

}
