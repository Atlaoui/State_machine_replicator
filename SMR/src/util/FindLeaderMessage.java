package util;

public class FindLeaderMessage extends  Message{
	
	private final int propId;
	
	
	/**
	 * id du proc qui envois
	 * @param idsrc
	 * id du destinateur
	 * @param iddest
	 * @param pid
	 * @param propId
	 */
	public FindLeaderMessage(long idsrc, long iddest, int pid, int propId) {
		super(idsrc, iddest, pid);
		this.propId=propId;
	}
	
	/**
	 * Retour l'id de la proposition courante
	 * @return
	 */
	public int getRoundId() {return propId;}
}
