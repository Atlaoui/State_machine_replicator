package util.request;

import util.messages.Message;

public class ResetReq extends Message implements RequestMessage{

	public ResetReq(long idsrc, long iddest) {
		super(idsrc, iddest);
	}

	@Override
	public Request getRequest() {
		// TODO Auto-generated method stub
		return null;
	}

}
