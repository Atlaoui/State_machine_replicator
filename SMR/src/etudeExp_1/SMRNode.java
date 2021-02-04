package etudeExp_1;

import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.AcceptMessage;
import util.AcceptedMessage;
import util.AppSleepToMessage;
import util.FindLeaderMessage;
import util.PromiseMessage;
import util.RejectMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

import peersim.core.CommonState;


public class SMRNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private int nodeId;//identifiant du node 

	private int roundId=0;

	//private List<Integer> H = new ArrayList<>();
	private HashMap<Integer, Integer> H;//historique de l'ensemble des valeurs
	
	boolean isSleeping = false;
	int incrWaitingTime = 0;//temps d'attente

	private int nbAccept = 0; //nombre de node ayant reçus un msg Accept
	private int nbAccepted = 0; //nombre de node ayant reçus un msg Accepted
	private final int quorumSize = Network.getCapacity()/2+1; //valeur de la majorité
	
	private int myLeader;//valeur du leader choisis
	
	public SMRNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		nodeId=Configuration.lookupPid(tmp[tmp.length-1]);
		H = new HashMap<Integer, Integer>();
		myLeader = nodeId;
	}
	
	
	
	/*
	 * la condition pour avoir une majorité est de recevoir N2 + 1 message Promise et Accepted contenant la
	même valeur pour pouvoir passer en phase 2 et 3
	 */	
	@Override
	public void processEvent(Node node, int pid, Object event) {
		
		if (event instanceof AppSleepToMessage) {
			isSleeping = false;
			incrWaitingTime++;
			return;
		}
		
		
/* ---- Étape 1B : À la réception d’un message Prepare sur un Acceptor a depuis un Proposer p pour un round n ---- */
		else if(event instanceof FindLeaderMessage) {//réception du msg PREPARE
			//on ignore les roundId inferieur a celui courant
			FindLeaderMessage msg = (FindLeaderMessage) event;
			Transport tr = (Transport) node.getProtocol(transport_id);
			Object toSend;
			if(msg.getRoundId() >= roundId){ //round n est supérieur à round de a
				//myLeader = (int) msg.getIdSrc();
				roundId = msg.getRoundId();//update du round courant

				System.out.println("[ACCEPTOR] [" + node.getIndex() +"] : envoie au Proposer [" + msg.getIdSrc() + 
						"]  <"+myLeader+","+roundId+">"+"\n\t  >> promet qu'il ne participera pas au round inférieur au n° de round: "+roundId);
				toSend = new PromiseMessage(node.getID(), msg.getIdSrc(), myLeader, roundId);
			}else {//renvoi un message Reject à p
					System.out.println("[ACCEPTOR] [" + node.getIndex()+"] : envoie un msg Reject à : [" + msg.getIdSrc()+
										"]\n\t  >> numéro de round obsolète");
					toSend = new RejectMessage(node.getID(), msg.getIdSrc() ,(int) msg.getIdSrc());
			}
			tr.send(node, Network.get((int)msg.getIdSrc()), toSend, nodeId);	
		}
		
		
/* ---- Étape 2A : Lorsque p reçoit une majorité de Promise, il doit décider d’une valeur e ---- */
		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbAccept++;
			H.put(msgPromise.getValue(), msgPromise.getRoundId());
			if(nbAccept >= quorumSize) {
				//myLeader = (int) msgPromise.getIdSrc();
				//p choisit la valeur v avec le numéro nv le plus grand
			    for (Map.Entry<Integer, Integer> entry : H.entrySet()) {
			        if (entry == null || entry.getValue().compareTo(entry.getValue()) > 0) {
			            myLeader = entry.getKey();
			            roundId = entry.getValue();
			        }
			    }
			}else {//p renvoie avec la valeur que le client lui a envoyé à l’étape 0 (au départ val=id node)
				myLeader = nodeId;
			}
			
			//Proposer p envoie alors à l’ensemble des Acceptors la valeur e qu’il a choisie associée au numéro de round n
			System.out.println("["+node.getIndex()+"] : diffuse un Accept à tous les Acceptor :  <" + myLeader +","+roundId+">");
			Transport tr = (Transport) node.getProtocol(transport_id);
			for (int i = 0; i < Network.size(); i++) {
				if (i != node.getID()){
					Node dst = Network.get(i);
					long idDest = Network.get(i).getID();
					AcceptMessage msgAccept = new AcceptMessage(node.getID(), idDest, myLeader, roundId);
					tr.send(node, dst, msgAccept, nodeId);
				}
			}
		}
		
		
/* ---- Étape 2b À la réception d’un message Accept sur un Acceptor a depuis un Proposer p pour un round n et une valeur e ---- */
		else if (event instanceof AcceptMessage) {
			AcceptMessage msg = (AcceptMessage) event;
			
			if(msg.getRoundId() > roundId) {//n est plus grand ou égal au numéro de round du dernier Promise
				myLeader = (int) msg.getVal();
				roundId = msg.getRoundId();
				System.out.println("["+node.getIndex()+"] :diffuse un Accepted contenant la valeur e à l'ensemble des Learners :  <" + myLeader +","+roundId+">");
				Transport tr = (Transport) node.getProtocol(transport_id);
				for (int i = 0; i < Network.size(); i++) {
					if (i != node.getID()){
						Node dst = Network.get(i);
						long idDest = Network.get(i).getID();
						AcceptedMessage msgAccepted = new AcceptedMessage(node.getID(), idDest, myLeader);
						tr.send(node, dst, msgAccepted, nodeId);
					}
				}
			}else {//msg ignoré
				//System.out.println();
			}
			
		}
		
		
/* ---- Étape 3 Lorsqu’un Learner l recoit une majorité de messages Accepted pour une même valeur e ---- */
		else if (event instanceof AcceptedMessage) {
			AcceptedMessage msg = (AcceptedMessage) event;
			if(msg.getVal() == myLeader) {
				nbAccepted ++;
			}
			if(nbAccepted >= quorumSize) {
				System.out.println("[DECIDE] ["+nodeId+"] le leader est : "+ myLeader);
			}
			
		}
		
		
		else  if (event instanceof RejectMessage) {//reception un message Reject, attend avant de réitérer sa proposition
			//sinon, a ignore le message ou éventuellement renvoi un message Reject 
			//à p lui indiquant que son numéro de round est invalide et obsolète.
			RejectMessage msgRej = (RejectMessage) event;
			myLeader = msgRej.getTheChosenOne();
			System.out.println("RejectMessage  >>  numero de round invalide");
			try {
				wait(nodeId, 0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
	}
	
	

	
	
	
	
/* ---- Étape 1A : Le Proposer p émet à l’ensemble des Acceptors un message Prepare ---- */
	public void findLeader(Node node) {	
		System.out.println("[PROPOSER] : [" + node.getIndex() + "] veut devenir le leader avec le numéro de ballot: " + roundId);
		//pour init l'algo envoit à tlm 
		// a voir avec l'algo mais normalement en doit bien envoyer a tlm
		long idsrc = node.getID();
		Transport tr = (Transport) node.getProtocol(transport_id);
		//ETAPE 1A : émet à l’ensemble des Acceptors un message Prepare contenant un numéro de round
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
	
 
	
	
	
    public void wait(Node node,int nbCycle) {
        AppSleepToMessage appMes = new  AppSleepToMessage();
        EDSimulator.add(nbCycle+incrWaitingTime, appMes, node, transport_id);
        isSleeping = true;
    }

}
