package util;

import java.util.Random;

import peersim.core.Node;
import peersim.transport.Transport;
import util.request.ReadRequest;
import util.request.WriteRequest;

public class FactoryReqest {
	/* Methode de transport*/
	private Transport tr;
	
	/*Node src*/
	private Node node;
	
	// l'idée du protocol
	private int protocol_Id;
	
	private int nbWritReq = 0;


	private int nbReadReq = 0;


	public FactoryReqest(Node node , Transport tr , int protocol_Id) {
		this.tr = tr;
		this.node = node;
		this.protocol_Id = protocol_Id;
	}
	
	
	public void sendWritRequest(Node destinataire) {
		WriteRequest req = new WriteRequest(node.getID(),destinataire.getID());
		tr.send(node, destinataire, req, protocol_Id);
		nbWritReq++;
	}
	
	public void sendReadRequest(Node destinataire) {
		ReadRequest req = new ReadRequest(node.getID(),destinataire.getID());
		tr.send(node, destinataire, req, protocol_Id);
		nbReadReq++;
	}
	
	public void sendRandRequest(Node destinataire) {
		Random rand = new Random();
		if(rand.nextBoolean()) 
			sendReadRequest(destinataire);
		else
			sendWritRequest(destinataire);
	}
}
