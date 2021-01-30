package etudeExp_1;

import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.AppSleepToMessage;
import util.FindLeaderMessage;
import util.PromiseMessage;
import util.RejectMessage;

import java.util.ArrayList;
import java.util.List;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

import peersim.core.CommonState;


public class SMRNode implements EDProtocol{
	private static final String PAR_TRANSPORT = "transport";

	private final int transport_id;

	private int nodeId;

	private int roundId=0;

	private List<Integer> H = new ArrayList<>();

	private boolean isAccepted = false;
	
	boolean isSleeping = false;
	int incrWaitingTime = 0;

	public SMRNode(String prefix) {
		transport_id = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		String tmp[]=prefix.split("\\.");
		nodeId=Configuration.lookupPid(tmp[tmp.length-1]);
	}


	
	private int nbAccepte = 0;
	
	private final int quorumSize =  Network.getCapacity()/2;
	private int myLeader =-1;
	@Override
	public void processEvent(Node node, int pid, Object event) {
		System.out.println(node.getIndex()+": a recus un msg");
		if (event instanceof AppSleepToMessage) {
			isSleeping = false;
			incrWaitingTime++;
			return;
		}
		else if(event instanceof FindLeaderMessage) {//reception du msg Prepare
			//on ignore les roundId inferieur a celui courant
			FindLeaderMessage msg = (FindLeaderMessage) event;
			if(msg.getRoundId() >= roundId ) {
				//update du round courant
				roundId = msg.getRoundId();

				/* ------ ETAPE 1B ------*///si on a a pas déjà accepté une valeur 
				Transport tr = (Transport) node.getProtocol(transport_id);
				Object toSend;
				if(!isAccepted) {
					System.out.println(node.getIndex()+": a accepter et envois un msg a -> " +msg.getIdSrc() );
					isAccepted = true;
					myLeader = (int) msg.getIdSrc();
					toSend  = new PromiseMessage(node.getID(),msg.getIdSrc(),roundId,true);
				}else {// ignore le message ou éventuellement renvoi un message Reject à p
					System.out.println(node.getIndex()+": a rejeter et envois un msg a -> " +msg.getIdSrc() );
					toSend  = new RejectMessage(node.getID(),msg.getIdSrc(),myLeader);
				}
				tr.send(node, Network.get((int)msg.getIdSrc()), toSend, nodeId);
				/* ------ FIN ETAPE 1B ------*/

			}else if (event instanceof PromiseMessage) {
				//reçoit une majorité de Promise, il doit décider d’une valeur e?
				PromiseMessage msgPromise = (PromiseMessage) event;
				nbAccepte++;
				if(nbAccepte >= quorumSize) {
					myLeader = (int) msgPromise.getIdSrc();
					System.out.println(node.getIndex()+": a décider du process -> " + myLeader);
				}
			}
			else  if (event instanceof RejectMessage) {
				//sinon, a ignore le message ou éventuellement renvoi un message Reject 
				//à p lui indiquant que son numéro de round est invalide et obsolète.
				RejectMessage msgRej = (RejectMessage) event;
				myLeader = msgRej.getTheChosenOne();
				System.out.println("RejectMessage  >>  numero de round invalide");
				return;
			}
		}

	}

	public void findLeader(Node node) {
		System.out.println(node.getIndex()+": veut devenir le leader ");
		//pour init l'algo envois a tlm 
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
