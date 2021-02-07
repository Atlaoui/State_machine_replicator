package util.messages;

public class LeaderFoundMessage extends Message {
	private final long leader;
	private final int round;
	
	/**
	 * 
	 * @param idsrc : identifiant de l'envoyeur
	 * @param iddest : identifiant du destinataire
	 * @param leader : valeur finale du leader
	 */
	public LeaderFoundMessage(long idsrc, long iddest, long leader, int round) {
		super(idsrc, iddest);
		this.leader = leader;
		this.round = round;
	}
	
	
	/* Retourne la valeur seléctionnée par l'envoyeur */
	public long getLeader() {return leader;}
	
	public long getRound() {return round;}
}
