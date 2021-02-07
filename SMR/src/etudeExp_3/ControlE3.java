package etudeExp_3;

import etudeExp_2.MPSNode;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import util.Sequence;

public class ControlE3 implements Control{

	public  ControlE3(String prefix) {}
	
	@Override
	public boolean execute() {
		
		int applicative_pid=Configuration.lookupPid("node");
		System.out.println("Étape 0 : Le Client C envoie sa requête à tous les Proposer p");
		
			
		for(int i=0; i<Network.size(); i++) { //parcours du tableau de Node
			Node src = Network.get(i);
			System.out.println("[CLIENT] C : envois au protocole -> " + src.getIndex());
			OSMRNode node = (OSMRNode)src.getProtocol(applicative_pid);
			node.findLeader(src);	
		}
		
		return false;
	}

}