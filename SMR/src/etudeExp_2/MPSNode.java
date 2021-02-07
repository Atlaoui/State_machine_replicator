package etudeExp_2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import etudeExp_1.SMRNode;
import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import util.FactoryMessage;
import util.messages.AcceptMessage;
import util.messages.AcceptedMessage;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.messages.PrepareMessage;
import util.messages.PromiseMessage;
import util.messages.RejectMessage;

public class MPSNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private int protocol_id; 

	private int roundId=0;
	private int myId;
	//private List<Integer> H = new ArrayList<>();
	private List<Integer> H2 = new ArrayList<>();
	private HashMap<Integer, Integer> H = new HashMap<Integer, Integer>(); //historique de l'ensemble des valeurs

	boolean isSleeping = false;
	int incrWaitingTime = 100;//temps d'attente

	private int nbPromise = 0; //nombre de Promise reçu

	private int nbAccepted = 0; //nombre de node ayant reçus un msg Accepted
	
	private int nbRejected = 0;


	private final int quorumSize = (Network.getCapacity()/2)+1; //valeur de la majorité

	private int myLeader;//valeur du leader choisis

	/** true si le node a accepter une valeur */
	private boolean isAccepted = false;

	/**  */
	private boolean isPromise = false ;
	
	/*true si un leader a était trouver */
	private boolean isFound = false;
	
	private FactoryMessage factory ;

	private Random rand = new Random();
	
	public MPSNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		protocol_id=Configuration.lookupPid(tmp[tmp.length-1]);
	}



	/*
	 * la condition pour avoir une majorité est de recevoir N2 + 1 message Promise et Accepted contenant la
	même valeur pour pouvoir passer en phase 2 et 3
	 */	
	@Override
	public void processEvent(Node node, int pid, Object event) {
			// je suis pas sur mais ça a l'aire de marcher si en mets ça xD
			if(isFound) return;
		/**
		 * ici ont ici en recois un message de nous meme nous disons que il faudrait refaire une election
		 * ça c'est en cas de rejet
		 */
		if (event instanceof AskAgainMessage ) {

			//if(isAccepted == false) {
			if(nbRejected ==  Network.getCapacity()) {

				roundId++;
				System.out.println("["+myId+"] Proposition rejeté, redemande election <"+myId+","+roundId+">");
				nbPromise = 0; //nombre de Promise reçu
				nbAccepted = 0; //nombre de node ayant reçus un msg Accepted
				nbRejected = 0;
				myLeader = myId;
				for (int i = 0; i < Network.size(); i++) {
					factory.sendPrepareMessage( Network.get(i), roundId);
				}
			}
		}

		/* ---- Étape 1B : À la réception d’un message Prepare sur un Acceptor a depuis un Proposer p pour un round n ---- */
		else if(event instanceof PrepareMessage ) {
			//on ignore les roundId inferieur a celui courant
			PrepareMessage msg = (PrepareMessage) event;
			System.out.println(myId +":ACCEPTOR reception message Prepare du proposer [" + msg.getIdSrc()+"]");
			//if (isPromise) {
				// si ont a déja promis
			//factory.sendReject(Network.get((int)msg.getIdSrc()), myLeader);	
			 if(msg.getRoundId() > roundId){ //round n est supérieur à round de a
					//myLeader = (int) msg.getIdSrc();
					roundId = msg.getRoundId();//update du round courant

					System.out.println("\t Envoie message"+" <"+myLeader+","+roundId+"> Promise à [" + msg.getIdSrc() + 
							"]\n\t >> promet qu'il ne participera pas au round inférieur au n° de round: "+roundId);
					isPromise = true;
					factory.sendPromise(Network.get((int)msg.getIdSrc()), myLeader, roundId);
					}else {//renvoi un message Reject à p
					System.out.println("\t envoie un msg Reject à : [" + msg.getIdSrc()+
							"]\n\t  >> numéro de round obsolète");

					factory.sendReject(Network.get((int)msg.getIdSrc()), myLeader);
				} 
			}


		/* ---- Étape 2A : Lorsque p reçoit une majorité de Promise, il doit décider d’une valeur e ---- */
		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbPromise++;
			H.put(msgPromise.getRoundId(), msgPromise.getValue());
			if(nbPromise >= quorumSize) {
				//myLeader = (int) msgPromise.getIdSrc();
				//p choisit la valeur v avec le numéro nv le plus grand
				if(H.size()!=0) { 
					int max = -1;
					for (Map.Entry<Integer, Integer> entry : H.entrySet()) { 
						if(entry.getKey() > max){
							max = entry.getValue();
						}
					}
					myLeader = max;
					
				}else {//p renvoie avec la valeur que le client lui a envoyé à l’étape 0 (au départ val=id node)
					myLeader = myId;
				}
				System.out.println("\n[2A]  [PROPOSER - "+myId+"] a reçu une majorité de Promise(= "+nbPromise
						+ ")\n\t il doit décider d'une valeur");
				//Proposer p envoie alors à l’ensemble des Acceptors la valeur e qu’il a choisie associée au numéro de round n
				System.out.println("\t diffuse un Accept à tous les Acceptor :  <" + myLeader +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) {
					factory.sendAccept(Network.get(i), myLeader, roundId);
				}
			}
		}


		/* ---- Étape 2B À la réception d’un message Accept sur un Acceptor a depuis un Proposer p pour un round n et une valeur e ---- */
		else if (event instanceof AcceptMessage) {
			AcceptMessage msg = (AcceptMessage) event;
			System.out.println("\n[2B] [ACCEPTOR - "+ myId+"] reception message Accept de ["+msg.getIdSrc()+"]");
			if(msg.getRoundId() >= roundId) {//n est plus grand ou égal au numéro de round du dernier Promise
				myLeader = (int) msg.getVal();
				//roundId = msg.getRoundId();
				System.out.println("\n diffuse un Accepted contenant la valeur e à l'ensemble des Learners :  <" + myLeader +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) {
					factory.sendAccepted(Network.get(i),myLeader);
				}
			}else {//msg ignoré
				System.out.println(myId+": a ignoré un msg");
			}
		}


		/* ---- Étape 3 Lorsqu’un Learner l recoit une majorité de messages Accepted pour une même valeur e ---- */
		else if (event instanceof AcceptedMessage) {
			AcceptedMessage msg = (AcceptedMessage) event;
			H2.add((int) msg.getVal());
			Set<Integer> st = new HashSet<Integer>(H2); 
		
			for (Integer s : st) {
				nbAccepted = Collections.frequency(H2, s);
				if(nbAccepted >= quorumSize) {
					isFound = true;
					myLeader = s;
					System.out.println("\n[3] [LEARNER - "+myId+"] le leader est >>>> "+ myLeader+"je signale aux autres");
					factory.broadcastFoundLead(myLeader, roundId); 
				}
			}
		}


		else  if (event instanceof RejectMessage) {//reception un message Reject, attend avant de réitérer sa proposition
			//sinon, a ignore le message ou éventuellement renvoi un message Reject 
			//à p lui indiquant que son numéro de round est invalide et obsolète.
			RejectMessage msgRej = (RejectMessage) event;
			myLeader = msgRej.getTheChosenOne();
			System.out.println("["+msgRej.getIdDest()+"] RejectMessage  >>  numero de round invalide = "+roundId);

			nbRejected ++;
			factory.sendAskAgaineMessage(incrWaitingTime+rand.nextInt(200));
			incrWaitingTime += 1000;
		}
		
		else if (event instanceof LeaderFoundMessage) {
			LeaderFoundMessage msg = (LeaderFoundMessage) event;
			isFound = true;
			myLeader = (int) msg.getLeader();
			System.out.println("["+msg.getIdDest()+"] TERMINAISON LEADER TROUVÉ  >> "+ myLeader);
			System.out.println(myId+": a envoyer "+factory.getNbMsgSent()+" msg au roundId =" + roundId);
		}
	}







	/* ---- Étape 1A : Le Proposer p émet à l’ensemble des Acceptors un message Prepare ---- */
	public void findLeader(Node node) {	
		System.out.println("\n[1A] [PROPOSER - " + node.getIndex() + "] veut devenir le leader avec le numéro de ballot: " + roundId);
		//pour init l'algo envoit à tlm 
		// a voir avec l'algo mais normalement en doit bien envoyer a tlm
		myId = (int) node.getID();
		myLeader = myId; // a revoir ici
		
		factory = new FactoryMessage(node,(Transport) node.getProtocol(transport_id),protocol_id);	
		//ETAPE 1A : émet à l’ensemble des Acceptors un message Prepare contenant un numéro de round
		for (int i = 0; i < Network.size(); i++) {
			factory.sendPrepareMessage(Network.get(i), roundId);
		}
	}

	@Override
	public Object clone() {
		MPSNode n = null;
		try {n = (MPSNode) super.clone();} 
		catch (CloneNotSupportedException e) {/*Never happends*/}
		return n;
	}
	
	public int getId() {
		return myId;
	}
	
	public int getLeader() {
		return myLeader;
	}


}