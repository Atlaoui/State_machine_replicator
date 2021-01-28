package util;

public class RejectMessage extends Message{

	
	//message indiquant que son numéro de round est invalide et obsolète.
	public RejectMessage(long idsrc, long iddest) {
		super(idsrc, iddest);
	}
	
}
