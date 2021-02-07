package util;

import java.util.List;
import java.util.Random;

import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.messages.AskAgainMessage;
import util.request.AcceptReq;
import util.request.AcceptedReq;
import util.request.PrepareSeqence;
import util.request.PromiseSeq;
import util.request.ReadRequest;
import util.request.RejectSeq;
import util.request.Request;
import util.request.Result;
import util.request.RunSequenceAgain;
import util.request.WriteRequest;

public class FactoryReqest {
	/* Methode de transport*/
	private Transport tr;

	/*Node src*/
	private final Node node;

	// l'idée du protocol
	private int protocol_Id;

	private int nbWritReq = 0;


	private int nbReadReq = 0;


	public FactoryReqest(Node node , Transport tr , int protocol_Id) {
		this.setTransport(tr);
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

	public void retryRequest(int nbCycle ,  Request request) {
		RunSequenceAgain appMes = new RunSequenceAgain(node.getID(),node.getID(),request);
		EDSimulator.add(nbCycle,appMes , node, protocol_Id);
	}

	public void deliverResult(Node destinataire , long idReq) {
		Result res = new Result(idReq);
		tr.send(node, destinataire, res, protocol_Id);
	}

	public void sendPrepareSeq(Node destinataire , int roundId,Request seq) {
		PrepareSeqence msg = new PrepareSeqence(node.getID(),destinataire.getID(),roundId,seq);
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void sendPromiseSeq(Node destinataire, int roundId,Request seq) {
		PromiseSeq msg = new PromiseSeq(node.getID(),destinataire.getID(),roundId,seq);
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void sendRejectSeq(Node destinataire,Request seq) {
		RejectSeq msg = new RejectSeq(node.getID(),destinataire.getID(),seq);
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void sendAcceptReq(Node destinataire, int roundId,Request seq) {
		AcceptReq msg = new AcceptReq(node.getID(),destinataire.getID(),roundId,seq);
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void sendAcceptedReq(Node destinataire,Request seq) {
		AcceptedReq msg = new AcceptedReq(node.getID(),destinataire.getID(),seq);
		tr.send(node, destinataire, msg, protocol_Id);
	}

	public void broadCastReq(int roundId, Request r) {
		
	}
	
	public int getNbWritReq() {
		return nbWritReq;
	}

	public int getNbReadReq() {
		return nbReadReq;
	}

	public void setTransport(Transport tr) {
		this.tr = tr;
	}

}
