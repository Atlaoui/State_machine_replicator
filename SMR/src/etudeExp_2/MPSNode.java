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
import util.FactoryReqest;
import util.PersistantStorage;
import util.Sequence;
import util.messages.AcceptMessage;
import util.messages.AcceptedMessage;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.messages.PrepareMessage;
import util.messages.PromiseMessage;
import util.messages.RejectMessage;
import util.request.AcceptReq;
import util.request.AcceptedReq;
import util.request.BeginSeq;
import util.request.PrepareSeqence;
import util.request.PromiseSeq;
import util.request.RejectSeq;
import util.request.Request;
import util.request.RequestLater;
import util.request.RequestMessage;
import util.request.ResetReq;
import util.request.RunSequenceAgain;
import util.request.SeqFound;

public class MPSNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private int protocol_id; 

	private int roundId=0;

	private int seqRoundId = 0;

	private int myId;
	private List<Integer> Haccept = new ArrayList<>();//ancienement H2
	private HashMap<Integer, Integer> HRespLeader = new HashMap<>(); //historique de l'ensemble des valeurs


	private HashMap<Integer, Request> Hreq = new HashMap<>();
	private List<Request> Hseq = new ArrayList<>();//ancienement H2

	private int incrWaitingTime = 100;//temps d'attente

	private int seqWaitingTime = 100;

	private int nbPromise = 0; //nombre de Promise reçu

	private int nbAccepted = 0; //nombre de node ayant reçus un msg Accepted

	private int nbRejected = 0;
	
	private int nbReady = 0;


	private int myLeader;//valeur du leader choisis


	private final int quorumSize = (Network.getCapacity()/2)+1; //valeur de la majorité


	private Request currentReq = null;

	/*true si un leader a était trouver */
	private boolean isFound = false;

	private boolean isSeqValid = false;

	private boolean startSecondPhase = false;

	private FactoryMessage factoryMsg ;

	private FactoryReqest factoryReq;


	private Random rand = new Random();


	public MPSNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		protocol_id=Configuration.lookupPid(tmp[tmp.length-1]);
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		// paxos pour le leader
		if(!isFound) {
			myLeader = runPaxos(node, pid, event ,myLeader);
		}else if(myLeader == myId && !startSecondPhase) {
			startSecondPhase=true;
			factoryReq.sendBeginSeq(10000);
		}
		
		
		if(event instanceof RequestLater) {
			System.out.println(myId+": va lancer une instance de paxos");
			if(Sequence.isEmpty()!=true) {
				currentReq = Sequence.get().get(0);
				sequentialReqest(node,currentReq);
				if(Sequence.popOne())
					factoryReq.sendRequestLater(1000,currentReq);
			}
		}
		else if(event instanceof BeginSeq && !Sequence.isEmpty()) {
			System.out.println(myId+":Begin Sequence ");
			if(currentReq == null)
				currentReq = Sequence.get().get(0);
			sequentialReqest(node,currentReq);
			if(Sequence.popOne())
				factoryReq.sendRequestLater(1000,currentReq);
		}else

		if(event instanceof ResetReq) {
			isSeqValid = false;
			ResetReq msg = (ResetReq) event;
			factoryReq.sendReady(Network.get((int)msg.getIdSrc()));
		}else if(event instanceof RequestMessage && !isSeqValid) {
			System.out.println(myId+": commence le secend paxasos");
			RequestMessage r = (RequestMessage)event;
			isSeqValid = runSequentialPaxos(node,  pid, event  ,r.getRequest()); 
		}

	}



	public void findLeader(Node node) {	
		myId = (int) node.getID();
		System.out.println(myId+"P: fait une 1er proposition roundId =" + roundId);
		myLeader = myId; 
		factoryMsg = new FactoryMessage(node,(Transport) node.getProtocol(transport_id),protocol_id);
		
		factoryReq = new FactoryReqest(node, (Transport) node.getProtocol(transport_id), protocol_id);
		//ont ajoute notre Array
		PersistantStorage.setH(myId, new ArrayList<Request>());
		for (int i = 0; i < Network.size(); i++) {
			factoryMsg.sendPrepareMessage(Network.get(i), myId);
		}
	}



	public void sequentialReqest(Node node,Request r) {
		for (int i = 0; i < Network.size(); i++) {
			factoryReq.sendReset(Network.get(i));
			factoryReq.sendRequest(Network.get(i),r);
		}
	}




	private int runPaxos(Node node, int pid, Object event , int Value) {

		if (event instanceof AskAgainMessage ) {
			if(nbRejected ==  Network.getCapacity()) {
				roundId++;
				System.out.println("["+myId+"] Proposition rejeté, redemande election <"+myId+","+roundId+">");
				nbPromise = 0; //nombre de Promise reçu
				nbAccepted = 0; //nombre de node ayant reçus un msg Accepted
				nbRejected = 0;
				Value = -1;
				for (int i = 0; i < Network.size(); i++) {
					factoryMsg.sendPrepareMessage( Network.get(i), roundId);
				}
			}
		}

		else if(event instanceof PrepareMessage ) {
			PrepareMessage msg = (PrepareMessage) event;
			System.out.println(myId +":A reception message Prepare du proposer [" + msg.getIdSrc()+"]");
			if(msg.getRoundId() > roundId){ //round n est supérieur à round de a
				//myLeader = (int) msg.getIdSrc();
				roundId = msg.getRoundId();//update du round courant

				System.out.println("\t Envoie message"+" <"+Value+","+roundId+"> Promise à [" + msg.getIdSrc() + 
						"]\n\t >> promet qu'il ne participera pas au round inférieur au n° de round: "+roundId);
				factoryMsg.sendPromise(Network.get((int)msg.getIdSrc()), Value, roundId);
			}else {//renvoi un message Reject à p
				System.out.println("\t envoie un msg Reject à : [" + msg.getIdSrc()+
						"]\n\t  >> numéro de round obsolète");

				factoryMsg.sendReject(Network.get((int)msg.getIdSrc()), Value);
			} 
		}


		else if (event instanceof PromiseMessage) {
			PromiseMessage msgPromise = (PromiseMessage) event;
			nbPromise++;
			HRespLeader.put(msgPromise.getRoundId(), msgPromise.getValue());
			if(nbPromise >= quorumSize) {
				//myLeader = (int) msgPromise.getIdSrc();
				//p choisit la valeur v avec le numéro nv le plus grand
				if(HRespLeader.size()!=0) { 
					int max = -1;
					for (Map.Entry<Integer, Integer> entry : HRespLeader.entrySet()) { 
						if(entry.getKey() > max){
							max = entry.getValue();
						}
					}
					Value = max;

				}else {
					Value = myId;
				}
				System.out.println(myId+"P: a reçu une majorité de Promise(= "+nbPromise+ ")");
				System.out.println(myId+"P: diffuse un Accept à tous les Acceptor :  <" + Value +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) 
					factoryMsg.sendAccept(Network.get(i), Value, roundId);
			}
		}

		else if (event instanceof AcceptMessage) {
			AcceptMessage msg = (AcceptMessage) event;
			System.out.println(myId+"A: reception message Accept de ["+msg.getIdSrc()+"]");
			if(msg.getRoundId() >= roundId) {
				Value = (int) msg.getVal();
				System.out.println(myId+"A: diffuse un Accepted contenant la valeur e à l'ensemble des Learners :  <" + Value +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) {
					factoryMsg.sendAccepted(Network.get(i),Value);
				}
			}else {//msg ignoré
				System.out.println(myId+"A: a ignoré un msg");
			}
		}

		else if (event instanceof AcceptedMessage) {
			AcceptedMessage msg = (AcceptedMessage) event;
			Haccept.add((int) msg.getVal());
			Set<Integer> st = new HashSet<Integer>(Haccept); 

			for (Integer s : st) {
				nbAccepted = Collections.frequency(Haccept, s);
				if(nbAccepted >= quorumSize) {
					isFound = true;
					Value = s;
					System.out.println("\n[3] [LEARNER - "+myId+"] le leader est >>>> "+ Value+"je signale aux autres");

					factoryMsg.broadcastFoundLead(Value, roundId); 

				}
			}
		}

		else  if (event instanceof RejectMessage) {
			RejectMessage msgRej = (RejectMessage) event;
			Value = msgRej.getTheChosenOne();
			System.out.println("["+msgRej.getIdDest()+"] RejectMessage  >>  numero de round invalide = "+roundId);

			nbRejected ++;
			factoryMsg.sendAskAgaineMessage(incrWaitingTime+rand.nextInt(200));
			incrWaitingTime += 1000;
		}

		else if (event instanceof LeaderFoundMessage) {
			LeaderFoundMessage msg = (LeaderFoundMessage) event;
			isFound = true;
			Value = (int) msg.getLeader();
			System.out.println("["+msg.getIdDest()+"] TERMINAISON LEADER TROUVÉ  >> "+ Value);
			System.out.println(myId+": a envoyer "+factoryMsg.getNbMsgSent()+" msg au roundId =" + roundId);
		}
		return Value;
	}

	

	private boolean runSequentialPaxos(Node node, int pid, Object event ,Request Value) {
		boolean isFinished = false;

		if(Value!= null && Hcontains(Value)) return true;

		if (event instanceof RunSequenceAgain ) {
			RunSequenceAgain msg = (RunSequenceAgain) event;
			if(nbRejected ==  Network.getCapacity()) {
				seqRoundId++;
				System.out.println("["+myId+"] Proposition rejeté, redemande election <"+myId+","+seqRoundId+">");
				nbPromise = 0; //nombre de Promise reçu
				nbAccepted = 0; //nombre de node ayant reçus un msg Accepted
				nbRejected = 0;
				Value = msg.getRequest();

				for (int i = 0; i < Network.size(); i++) 
					factoryReq.sendPrepareSeq(Network.get(i), seqRoundId, Value);
			}
		}

		else if(event instanceof PrepareSeqence ) {
			PrepareSeqence msg = (PrepareSeqence) event;
			System.out.println(myId +":A reception message Prepare du proposer [" + msg.getIdSrc()+"]");
			if(msg.getRoundId() > seqRoundId){ //round n est supérieur à round de a
				seqRoundId = msg.getRoundId();//update du round courant
				System.out.println("\t Envoie message"+" <"+Value+","+seqRoundId+"> Promise à [" + msg.getIdSrc() + 
						"]\n\t >> promet qu'il ne participera pas au round inférieur au n° de round: "+seqRoundId);
				factoryReq.sendPromiseSeq(Network.get((int)msg.getIdSrc()),seqRoundId, Value );
			}else {//renvoi un message Reject à p
				System.out.println("\t envoie un msg Reject à : [" + msg.getIdSrc()+
						"]\n\t  >> numéro de round obsolète");
				factoryReq.sendRejectSeq(Network.get((int)msg.getIdSrc()), Value);
			} 
		}


		else if (event instanceof PromiseSeq) {
			PromiseSeq msgPromise = (PromiseSeq) event;
			nbPromise++;
			Hreq.put(msgPromise.getRoundId(), msgPromise.getRequest());
			if(nbPromise >= quorumSize) {
				if(Hreq.size()!=0) { 
					Integer max = -1;
					for (Map.Entry<Integer, Request> entry : Hreq.entrySet())
						if(entry.getKey() > max)
							max = entry.getKey();
					Value = Hreq.get(max);
				}else 
					Value = Hreq.get(0);

				System.out.println(myId+"P: a reçu une majorité de Promise(= "+nbPromise+ ")");
				System.out.println(myId+"P: diffuse un Accept à tous les Acceptor :  <" + Value +","+seqRoundId+">");
				for (int i = 0; i < Network.size(); i++) 
					factoryReq.sendAcceptReq(Network.get(i), seqRoundId, Value);
			}
		}

		else if (event instanceof AcceptReq) {
			AcceptReq msg = (AcceptReq) event;
			System.out.println(myId+"A: reception message Accept de ["+msg.getIdSrc()+"]");
			if(msg.getRoundId() >= seqRoundId) {
				Value = msg.getRequest();
				System.out.println(myId+"A: diffuse un Accepted contenant la valeur e à l'ensemble des Learners :  <" + Value +","+roundId+">");
				for (int i = 0; i < Network.size(); i++) {
					factoryReq.sendAcceptedReq(Network.get(i), Value);
				}
			}else {//msg ignoré
				System.out.println(myId+"A: a ignoré un msg");
			}
		}

		else if (event instanceof AcceptedReq) {
			AcceptedReq msg = (AcceptedReq) event;
			Hseq.add(msg.getRequest());
			Set<Request> st = new HashSet<Request>(Hseq); 
			for (Request s : st) {
				nbAccepted = Collections.frequency(Hseq, s);
				if(nbAccepted >= quorumSize) {
					isFinished = true;
					putInH(s);
					System.out.println("\n[3] [LEARNER - "+myId+"] le leader est >>>> "+ Value+"je signale aux autres");
					factoryReq.broadCastReq(seqRoundId, Value);
				}
			}
		}

		else  if (event instanceof RejectSeq) {
			RejectSeq msgRej = (RejectSeq) event;
			Value = msgRej.getRequest();
			System.out.println("["+msgRej.getIdDest()+"] RejectMessage  >>  numero de round invalide = "+seqRoundId);
			nbRejected ++;
			factoryReq.retryRequest(seqWaitingTime, Value);
			seqWaitingTime += 100;
		}

		else if (event instanceof SeqFound) {
			SeqFound msg = (SeqFound) event;
			isFinished = true;
			Value = msg.getRequest();
			putInH(Value);
			System.out.println("["+msg.getIdDest()+"] TERMINAISON LEADER TROUVÉ  >> "+ Value);
			System.out.println(myId+": a envoyer "+factoryMsg.getNbMsgSent()+" msg au roundId =" + seqRoundId);
		}
		return isFinished;
	}


	private boolean Hcontains(Request r) {
		return PersistantStorage.getH(myId).contains(r);
	}

	private List<Request> getMyPersistentH() {
		return PersistantStorage.getH(myId);
	}

	private void putInH(Request r) {
		List<Request> l = PersistantStorage.getH(myId);
		l.add(r);
		PersistantStorage.setH(myId, l);
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

	public void resetNode() {
		this.isSeqValid = false;
	}

}