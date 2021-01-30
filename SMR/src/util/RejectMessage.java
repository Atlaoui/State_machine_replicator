package util;

public class RejectMessage extends Message{

	private int theChosenOne;

	//message indiquant que son numéro de round est invalide et obsolète.
	public RejectMessage(long idsrc, long iddest , int theChosenOne) {
		super(idsrc, iddest);
		this.theChosenOne=theChosenOne;
	}
	
	public int getTheChosenOne() {
		return theChosenOne;
	}
}
