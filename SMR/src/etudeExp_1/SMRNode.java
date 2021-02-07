package etudeExp_1;

import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import java.util.Random;
import peersim.transport.Transport;
import util.FactoryMessage;
import util.messages.AcceptMessage;
import util.messages.AcceptedMessage;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.messages.PrepareMessage;
import util.messages.PromiseMessage;
import util.messages.RejectMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;



public class SMRNode implements EDProtocol{
	
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;
	private int protocol_id; 
	private int roundId;
	private int myId;
	
	private List<Integer> H2 = new ArrayList<>();//stock valeurs envoyé lors de la reception msg Accepted 
	private HashMap<Integer, Integer> H = new HashMap<Integer, Integer>();//historique de l'ensemble des valeurs acceptés

	boolean isSleeping = false;
	int incrWaitingTime = 0;//temps d'attente

	private int nbPromise = 0;//nombre de msg Promise reçus
	private int nbAccepted = 0;//nombre de msg Accepted reçus
	private int nbRejected = 0;//nombre de msg Rejected reçus

	private final int quorumSize = (Network.getCapacity()/2)+1;//valeur de la majorité

	private int myLeader;//valeur du leader choisi

	private boolean isAccepted = false;//true si le node a accepté une valeur 
	private boolean isPromise = false ;//true si le node a promise de ne pas participer à un autre round
	private boolean isFound = false;//true si un leader a été trouvé (pour la terminaison)
	
	private FactoryMessage factory ;
	private Random rand = new Random();
	
	/* Quantifie l’impact des paramètres */
	private boolean backOff = true	;//true: incrementation temps attente du wait
	private boolean version0 = false;//true: roundId=0 pour tout le monde
	
	
	//Constructor
	public SMRNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		protocol_id=Configuration.lookupPid(tmp[tmp.length-1]);
	}



	
	@Override
	public void processEvent(Node node, int pid, Object event) {
		if(isFound){return;}
		
		/* Message de ReProposition, indique qu'il faut relancer une requete car les autres ont refusé sa valeur */
		if (event instanceof AskAgainMessage ) {
			if(nbRejected ==  Network.getCapacity()) {
				roundId++;
				System.out.println("["+myId+"] Proposition rejetée - Redemande élection <"+myId+","+roundId+">");
				nbPromise = 0;
				nbAccepted = 0;
				nbRejected = 0;
				myLeader = myId;
				for (int i = 0; i < Network.size(); i++) {
					factory.sendPrepareMessage( Network.get(i), roundId);
				}
			}
		}

		/* ---- Étape 1B : Réception message Prepare sur un Acceptor a depuis un Proposer p pour un round n ---- */
		else if(event instanceof PrepareMessage ) {
			PrepareMessage msg = (PrepareMessage) event;
			System.out.println(myId +": ACCEPTOR reception message Prepare du PROPOSER [" + msg.getIdSrc()+"]");	
			 if(msg.getRoundId() > roundId){//round n supérieur au round accepté precedemment
				 roundId = msg.getRoundId();

				 System.out.println("\t Envoie message"+" <"+myLeader+","+roundId+"> Promise à [" + msg.getIdSrc() + 
							"]\n\t >> promet qu'il ne participera pas au round inférieur à: "+roundId);
				 factory.sendPromise(Network.get((int)msg.getIdSrc()), myLeader, roundId);
			 }else {//message rejeté
				 System.out.println("\t Envoie un msg Reject à : [" + msg.getIdSrc()+ "]\t  >> numéro de round obsolète");

				 factory.sendReject(Network.get((int)msg.getIdSrc()), myLeader);
				} 
			}


		/* ---- Étape 2A : P reçoit une majorité de Promise, décide d’une valeur e ---- */
		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbPromise++;
			//stockage des <round,valeur> promise acceptée
			System.out.println(myId+": PROMISE  majorité ?  "+nbPromise+" >= "+quorumSize);
			H.put(msgPromise.getRoundId(), msgPromise.getValue());
			if(nbPromise >= quorumSize) { //majorité reçue ?
				if(H.size()!=0) { 
					int max = -1;
					for (Map.Entry<Integer, Integer> entry : H.entrySet()) { 
						if(entry.getKey() > max){
							//max = entry.getValue();
							max = entry.getKey();
						}
					}
					myLeader = H.get(max);
					
				}else {//p renvoie avec la valeur que le client lui a envoyé à l’étape 0 (au départ val=id node)
					myLeader = myId;
				}
				System.out.println("\n[2A]  [PROPOSER - "+myId+"] a reçu une majorité de Promise(= "+nbPromise
						+ ")\n\t il doit décider d'une valeur");
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
				System.out.println("\n diffuse un Accepted contenant la valeur e à l'ensemble des Learners :  <" + myLeader +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) {
					factory.sendAccepted(Network.get(i),myLeader);
				}
			}else {
				System.out.println(myId+": message ignoré ...");
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

		/* Message de Rejet : Attend et réitère sa proposition */
		else  if (event instanceof RejectMessage) {
			RejectMessage msgRej = (RejectMessage) event;
			myLeader = msgRej.getTheChosenOne();
			nbRejected ++;
			System.out.println("["+msgRej.getIdDest()+"] RejectMessage = "+nbRejected+"  >>  numéro de round invalide = "+roundId);

			factory.sendAskAgaineMessage(incrWaitingTime+rand.nextInt(200));
			if(backOff == true) {
				incrWaitingTime += 50;
			}
		}
		
		/* Message de Terminaison : Signale que le leader a été trouvé */
		else if (event instanceof LeaderFoundMessage) {
			LeaderFoundMessage msg = (LeaderFoundMessage) event;
			isFound = true;
			myLeader = (int) msg.getLeader();
			System.out.println("["+msg.getIdDest()+"] TERMINAISON LEADER TROUVÉ  >> "+ myLeader);
			System.out.println(myId+": a envoyé "+factory.getNbMsgSent()+" msg au roundId =" + msg.getRound());
		}
	}





	/* ---- Étape 1A : Le Proposer p émet à l’ensemble des Acceptors un message Prepare ---- */
	public void findLeader(Node node) {	
		System.out.println("\n[1A] [PROPOSER - " + node.getID() + "] veut devenir le leader avec le numéro de ballot: " + roundId);
		//pour init l'algo envoit à tlm 
		// a voir avec l'algo mais normalement en doit bien envoyer a tlm
		myId = (int) node.getID();
		myLeader = myId; // a revoir ici
		
		if(version0 == true) {
			roundId = 0;
		}else {
			roundId = myId;
		}
		factory = new FactoryMessage(node,(Transport) node.getProtocol(transport_id),protocol_id);	
		for (int i = 0; i < Network.size(); i++) {
			factory.sendPrepareMessage(Network.get(i), roundId);
		}
	}

	
	
	@Override
	public Object clone() {
		SMRNode n = null;
		try {n = (SMRNode) super.clone();} 
		catch (CloneNotSupportedException e) {/*Never happen*/}
		return n;
	}
	
	
	public int getId() {
		return myId;
	}
	
	
	public int getLeader() {
		return myLeader;
	}


}
