package util.messages;

/*  le node ce l'envois a a lui meme
	et des que il la recus il relance une 
	election si besoin */
public class AskAgainMessage extends Message {

	public AskAgainMessage(long idsrc, long iddest) {
		super(idsrc, iddest);
	}
}
