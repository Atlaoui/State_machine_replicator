package etudeExp_2;


import java.util.List;
import java.util.Map;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import util.PersistantStorage;
import util.request.Request;

public class EndControlerE2  implements Control {

	private static final String PAR_PROTO_APPLICATIF="node";
	
	private final int pid_application;
	
	public EndControlerE2(String prefix) {
		pid_application=Configuration.getPid(prefix+"."+PAR_PROTO_APPLICATIF);
	}
	
	@Override
	public boolean execute() {
		// ajouter ici ce que tu veux executer a la fin pour les teste etc
		System.out.println("################################# AFFICHAGE DES VALEURS ###########################");
		for(int i=0;i<Network.size();i++){
			Node node =Network.get(i);
			MPSNode prot = (MPSNode)node.getProtocol(pid_application);
			System.out.println(prot.getId()+":node s'est terminé et son leader est : "+prot.getLeader());
		}
		System.out.println("################################# AFFICHAGE DE  H  #################################");
		System.out.println("H size : "+PersistantStorage.getH().size());
		for (Map.Entry<Integer, List<Request>> entry :PersistantStorage.getH().entrySet() ) {
			System.out.println(entry.getKey());
			System.out.println(entry.getValue());
		}
				
		return false;
	}

}
