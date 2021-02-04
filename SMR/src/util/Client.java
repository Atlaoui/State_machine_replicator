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
		System.out.println("Étape 0 : Le Client C envoie sa requête à tous les Proposer p");

		for(int i=0; i<Network.size(); i++) { //parcours du tableau de Node
			Node src = Network.get(i);
			System.out.println("[CLIENT] C : envois au protocole -> " + src.getIndex());
			SMRNode node = (SMRNode)src.getProtocol(applicative_pid);
			node.findLeader(src);
			
		}
		
		return false;
	}


}
