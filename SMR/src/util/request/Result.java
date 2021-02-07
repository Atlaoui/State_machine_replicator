package util.request;

public class Result implements RequestMessage{
	private final long idRes;
	
	public Result(long idRes) {
		this.idRes = idRes;
	}

	public long getIdRes() {return idRes;}
}
