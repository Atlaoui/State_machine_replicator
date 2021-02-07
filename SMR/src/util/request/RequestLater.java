package util.request;

public class RequestLater implements RequestMessage{
	private Request seq;
	public RequestLater(Request seq) {
		this.seq = seq;
	}
	
	public Request getRequest() {
		return seq;
	}
}
