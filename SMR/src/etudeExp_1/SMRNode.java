package etudeExp_1;

import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import util.FindLeaderMessage;
import util.PromiseMessage;

import java.util.ArrayList;
import java.util.List;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;


public class SMRNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private final int nodeId;

	private int roundId=0;

	private List<Integer> H = new ArrayList<>();

	private boolean isAccepted = false;

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

				//si on a a pas déja accépter une valeur 
				if(!isAccepted) {
					isAccepted = true;
					Transport tr = (Transport) node.getProtocol(transport_id);
					Object toSend  = new PromiseMessage(node.getID(),msg.getIdSrc(),nodeId,roundId,true);
					tr.send(node, Network.get((int)msg.getIdSrc()), toSend, nodeId);
				}

			}else if (event instanceof PromiseMessage) {

			}
			else {
				//sinon, a ignore le message ou éventuellement renvoi un message Reject 
				//à p lui indiquant que son numéro de round est invalide et obsolète.
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
			Node dst = Network.get(i);
			long idDest = Network.get(i).getID();
			FindLeaderMessage msg = new FindLeaderMessage(idsrc, idDest,roundId);
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
