package util.request;

public class RequestLater {
	private Request seq;
	public RequestLater(Request seq) {
		this.seq = seq;
	}
	
	public Request getRequest() {
		return seq;
	}
}
