package util;

import etudeExp_1.SMRNode;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Client implements Control{

	public  Client(String prefix) {}
	
	@Override
	public boolean execute() {
		
		int applicative_pid=Configuration.lookupPid("node");
		// pour l'instant 0 mais ont va ptet mettre ça en mult
		
		//Envoie de requete à tous les Proposer
		for(int i=0; i<Network.size(); i++) {
			Node src = Network.get(i);
			SMRNode node = (SMRNode)src.getProtocol(applicative_pid);
			node.findLeader(src);
			
		}
		
		return false;
	}


}
