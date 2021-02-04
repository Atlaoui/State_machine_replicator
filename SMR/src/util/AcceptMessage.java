package util;



/**
  * Un Proposer p envoie la valeur e qu’il a choisie associée 
  * au numéro de round n qu’il a envoyé dans le message Prepare
 */
public class AcceptMessage extends Message{
	private final long val;
	private final int roundId;
	
	
	/**
	 * 
	 * @param idsrc : identifiant de l'envoyeur
	 * @param iddest : identifiant du destinataire
	 * @param val : valeur qui a été choisie par l'envoyeur
	 * @param roundId : round associé à cette valeur
	 */
	public AcceptMessage(long idsrc, long iddest, long val, int roundId) {
		super(idsrc, iddest);
		this.val = val;
		this.roundId=roundId;
	}
	
	
	/* Retourne la valeur seléctionnée par l'envoyeur */
	public long getVal() {return val;}
	
	
	/* Retourne le numéro de round asocié à la valeur choisie */
	public int getRoundId() {return roundId;}
}
