package util.messages;


/**
 * Un message Accepted est envoyé lorsque n est plus grand ou égal au numéro de round
 * du dernier Promise que l'Acceptor a ait envoyé
 */
public class AcceptedMessage extends Message{
	private final long val;
	
	
	/**
	 * 
	 * @param idsrc : identifiant de l'envoyeur
	 * @param iddest : identifiant du destinataire
	 * @param val : valeur qui a été choisie par l'envoyeur
	 */
	public AcceptedMessage(long idsrc, long iddest, long val) {
		super(idsrc, iddest);
		this.val = val;
	}
	
	
	/* Retourne la valeur seléctionnée par l'envoyeur */
	public long getVal() {return val;}
	
}
