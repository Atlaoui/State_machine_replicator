package util;

import java.util.List;
import java.util.Random;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import util.messages.AskAgainMessage;
import util.messages.LeaderFoundMessage;
import util.request.AcceptReq;
import util.request.AcceptedReq;
import util.request.BeginSeq;
import util.request.PrepareSeqence;
import util.request.PromiseSeq;
import util.request.ReadRequest;
import util.request.Ready;
import util.request.RejectSeq;
import util.request.Request;
import util.request.RequestLater;
import util.request.ResetReq;
import util.request.RunSequenceAgain;
import util.request.SeqFound;
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


	public void sendRequest(Node destinataire,Request req) {
		tr.send(node, destinataire, req, protocol_Id);
		nbWritReq++;
	}

	
	public void retryRequest(int nbCycle ,  Request request) {
		RunSequenceAgain appMes = new RunSequenceAgain(node.getID(),node.getID(),request);
		EDSimulator.add(nbCycle,appMes , node, protocol_Id);
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
	
	public void sendBeginSeq(int nbCycle) {
		BeginSeq appMes = new BeginSeq();
		EDSimulator.add(nbCycle,appMes , node, protocol_Id);
	}

	
	public void broadCastReq(int roundId, Request r) {
		for (int i = 0; i < Network.size(); i++) {
			SeqFound msgFound = new SeqFound(node.getID(), Network.get(i).getID(),roundId,r);
			tr.send(node, Network.get(i), msgFound, protocol_Id);
		}
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


	public void sendRequestLater(long nbCycle , Request currentReq) {
		RequestLater appMes = new RequestLater(currentReq);
		EDSimulator.add(nbCycle,appMes , node, protocol_Id);
	}


	public void sendReset(Node dest) {
		ResetReq msg = new ResetReq(node.getID(),dest.getID());
		tr.send(node, dest, msg, protocol_Id);
	}


	public void sendReady(Node dest) {
		Ready msg = new Ready(node.getID(),dest.getID());
		tr.send(node, dest, msg, protocol_Id);
	}
}
