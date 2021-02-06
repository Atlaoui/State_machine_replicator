
package util;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.messages.AcceptMessage;
import util.messages.AcceptedMessage;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.messages.PrepareMessage;
import util.messages.PromiseMessage;
import util.messages.RejectMessage;

public class FactoryMessage {
	/* Methode de transport*/
	private Transport tr;
	
	/*Node src*/
	private Node node;
	
	// l'idée du protocol
	private int protocol_Id;

	public FactoryMessage(Node node , Transport tr , int protocol_Id) {
		this.tr = tr;
		this.node = node;
		this.protocol_Id = protocol_Id;
	}
	
	public void sendAccept(Node destinataire , long valeurAccepter, int roundId) {
		AcceptMessage msg = new AcceptMessage(node.getID(), destinataire.getID(), valeurAccepter, roundId);
		tr.send(node, destinataire, msg, protocol_Id);
	}
	
	public void sendAccepted(Node destinataire , long valeurAccepter) {
		AcceptedMessage msg = new AcceptedMessage(node.getID(), destinataire.getID(), valeurAccepter);
		tr.send(node, destinataire, msg, protocol_Id);
	}
	
	public void sendPromise(Node destinataire , int val , int roundId) {
		PromiseMessage msg = new PromiseMessage(node.getID(), destinataire.getID(),val,roundId);
		tr.send(node, destinataire, msg, protocol_Id);
	}
	
    public void sendPrepareMessage(Node destinataire , int roundId) {
    	PrepareMessage msg = new PrepareMessage(node.getID(), destinataire.getID(),roundId);
    	tr.send(node, destinataire, msg, protocol_Id);
	}
    
    public void sendReject(Node destinataire , int Idaccepter) {
    	RejectMessage msg =	new RejectMessage(node.getID(),destinataire.getID() ,Idaccepter);
    	tr.send(node, destinataire, msg, protocol_Id);
    }
    
    public void broadcastFoundLead(int leader) {
    	for (int i = 0; i < Network.size(); i++) {
			LeaderFoundMessage msgFound = new LeaderFoundMessage(node.getID(), Network.get(i).getID(), leader);
			tr.send(node, Network.get(i), msgFound, protocol_Id);
		}
    }
	
	public void sendAskAgaineMessage(int InnbCycle) {
		AskAgainMessage appMes = new  AskAgainMessage();
		EDSimulator.add(InnbCycle,appMes , node, protocol_Id);
	}
	
	
}
