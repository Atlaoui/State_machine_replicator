package util;

public class PromiseMessage extends Message{
	
	private final int roundId;
	/**
	 * id du proc qui envois
	 * @param idsrc
	 * id du destinateur
	 * @param iddest
	 * a voir mes ptet pas besoin
	 * @param pid
	 * 
	 * @param propId
	 */
	public PromiseMessage(long idsrc, long iddest, int pid, int roundId , boolean isAccept) {
		super(idsrc, iddest, pid);
		this.roundId=roundId;
	}
	
	/**
	 * Retour l'id de la proposition courante
	 * @return
	 */
	public int getRoundId() {return roundId;}

}
