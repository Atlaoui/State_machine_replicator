package util;


/**
 * Le message Promise permet d’indiquer à dest que id ne participera plus à un scrutin de round inférieur.
 * Le message Promise contient éventuellement la précédente valeur v que a a déjà acceptée 
 * (lors d’une précédente phase 2b) associé à son numéro de round.
 */
public class PromiseMessage extends Message{
	private int value;
	private final int roundId;
	
	/**
	 * @param idsrc : id de l'envoyeur
	 * @param iddest : id du receveur
	 * @param value
	 * @param roundId
	 */
	public PromiseMessage(long idsrc, long iddest, int value, int roundId) {
		super(idsrc, iddest);
		this.value = value;
		this.roundId = roundId;
	}
	
	
	/* Retourne le valeur envoyé par l'envoyeur */
	public int getValue() {return value;}
	
	
	/* Retourne le round associé à l'envoyeur */
	public int getRoundId() {return roundId;}

}
