package etudeExp_3;


import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class EndControlerE3 implements Control {

	private static final String PAR_PROTO_APPLICATIF="node";
	
	private final int pid_application;
	
	public EndControlerE3(String prefix) {
		pid_application=Configuration.getPid(prefix+"."+PAR_PROTO_APPLICATIF);
	}
	
	@Override
	public boolean execute() {
		// ajouter ici ce que tu veux executer a la fin pour les teste etc
		System.out.println("################################# AFFICHAGE DES VALEURS ###########################");
		for(int i=0;i<Network.size();i++){
			Node node =Network.get(i);
			OSMRNode prot = (OSMRNode)node.getProtocol(pid_application);
			System.out.println(prot.getId()+":node s'est terminé et son leader est : "+prot.getLeader()+" et nbMsg :"+prot.getFactory().getNbMsgSent());
		}
		return false;
	}

}