package util.messages;


/**
 * Message Reject indiquant à P que son numéro de round est invalide et obsolète
 */
public class RejectMessage extends Message{
	private int theChosenOne;

	public RejectMessage(long idsrc, long iddest , int theChosenOne) {
		super(idsrc, iddest);
		this.theChosenOne=theChosenOne;
	}
	
	public int getTheChosenOne() {
		return theChosenOne;
	}
}
