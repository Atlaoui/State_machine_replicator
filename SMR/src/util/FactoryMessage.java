
package util;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.messages.AcceptMessage;
import util.messages.AcceptedMessage;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.messages.PingMessage;
import util.messages.PongMessage;
import util.messages.PrepareMessage;
import util.messages.PromiseMessage;
import util.messages.RejectMessage;
import util.request.StartSequentialPaxos;

public class FactoryMessage {
	/* Methode de transport*/
	private Transport tr;

	/*Node src*/
	private final Node node;

	// l'idée du protocol
	private int protocol_Id;

	private int nb_msg_sent = 0;



	public FactoryMessage(Node node , Transport tr , int protocol_Id) {
		this.tr = tr;
		this.node = node;
		this.protocol_Id = protocol_Id;
	}

	public void sendAccept(Node destinataire , long valeurAccepter, int roundId) {
		AcceptMessage msg = new AcceptMessage(node.getID(), destinataire.getID(), valeurAccepter, roundId);
		tr.send(node, destinataire, msg, protocol_Id);
		nb_msg_sent++;
	}

	public void sendAccepted(Node destinataire , long valeurAccepter) {
		AcceptedMessage msg = new AcceptedMessage(node.getID(), destinataire.getID(), valeurAccepter);
		tr.send(node, destinataire, msg, protocol_Id);
		nb_msg_sent++;
	}

	public void sendPromise(Node destinataire , int val , int roundId) {
		PromiseMessage msg = new PromiseMessage(node.getID(), destinataire.getID(),val,roundId);
		tr.send(node, destinataire, msg, protocol_Id);
		nb_msg_sent++;
	}

	public void sendPrepareMessage(Node destinataire , int roundId) {
		PrepareMessage msg = new PrepareMessage(node.getID(), destinataire.getID(),roundId);
		tr.send(node, destinataire, msg, protocol_Id);
		nb_msg_sent++;
	}

	public void sendReject(Node destinataire , int Idaccepter) {
		RejectMessage msg =	new RejectMessage(node.getID(),destinataire.getID() ,Idaccepter);
		tr.send(node, destinataire, msg, protocol_Id);
		nb_msg_sent++;
	}

	public void broadcastFoundLead(int leader, int round) {
		for (int i = 0; i < Network.size(); i++) {
			LeaderFoundMessage msgFound = new LeaderFoundMessage(node.getID(), Network.get(i).getID(), leader, round);
			tr.send(node, Network.get(i), msgFound, protocol_Id);
			nb_msg_sent++;
		}
	}
	
	
	public void sendPingMessage(Node destinataire) {
		PingMessage msg = new PingMessage(node.getID(), destinataire.getID());
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void sendPongMessage(Node destinataire) {
		PongMessage msg = new PongMessage(node.getID(), destinataire.getID());
		tr.send(node, destinataire, msg, protocol_Id);
	}
	
	/* je ne sais pas si il faudrait le compter vue que c'est juste pour attendre */
	public void sendAskAgaineMessage(int InnbCycle) {
		AskAgainMessage appMes = new  AskAgainMessage();
		EDSimulator.add(InnbCycle,appMes , node, protocol_Id);
	}
	
	public void sendSequentialPaxosReq(Node destinataire ,int InnbCycle) {
		StartSequentialPaxos msg = new StartSequentialPaxos(node.getID(), destinataire.getID());
		EDSimulator.add(InnbCycle, msg , destinataire, protocol_Id);
	}


	public int getNbMsgSent() {return nb_msg_sent;}

	public void setTransport(Transport tr) {
		this.tr = tr;
	}
	
}
