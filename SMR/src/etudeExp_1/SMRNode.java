package etudeExp_1;

import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import util.FindLeaderMessage;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;


public class SMRNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private final int nodeId;

	private int roundId=0;

	public SMRNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		nodeId=Configuration.lookupPid(tmp[tmp.length-1]);
	}




	@Override
	public void processEvent(Node node, int pid, Object event) {
		// Implementation de l'algo TO DO
		if(event instanceof FindLeaderMessage) {
			//on ignore les roundId inferieur a celui courant
			FindLeaderMessage msg = (FindLeaderMessage) event;
			if(msg.getRoundId() >= roundId ) {
				//update du round courant
				roundId = msg.getRoundId();
				
				
				
				
			}
			else {
				//osef
				return;
			}
		}
		
	}

	public void findLeader(Node node) {
		//pour init l'algo envois a tlm 
		// a voir avec l'algo mais normalement en doit bien envoyer a tlm
		long idsrc = node.getID();
		Transport tr = (Transport) node.getProtocol(transport_id);
		for (int i = 0; i < Network.size(); i++) {
			if(nodeId == i)
				continue;
			Node dst = Network.get(i);
			long idDest = Network.get(i).getID();
			FindLeaderMessage msg = new FindLeaderMessage(idsrc, idDest, nodeId,roundId);
			tr.send(node, dst, msg, nodeId);
		}
	}


	@Override
	public Object clone() {
		SMRNode n = null;
		try {n = (SMRNode) super.clone();} 
		catch (CloneNotSupportedException e) {/*Never happends*/}
		return n;
	}

}
