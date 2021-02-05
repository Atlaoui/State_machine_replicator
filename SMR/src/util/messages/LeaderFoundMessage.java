package util.messages;

public class LeaderFoundMessage extends Message {
	private final long leader;
	
	/**
	 * 
	 * @param idsrc : identifiant de l'envoyeur
	 * @param iddest : identifiant du destinataire
	 * @param leader : valeur finale du leader
	 */
	public LeaderFoundMessage(long idsrc, long iddest, long leader) {
		super(idsrc, iddest);
		this.leader = leader;
	}
	
	
	/* Retourne la valeur seléctionnée par l'envoyeur */
	public long getLeader() {return leader;}
}
