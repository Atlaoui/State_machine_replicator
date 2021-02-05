package util;

import peersim.core.Node;
import peersim.transport.Transport;

public class Factorymessage {
	/* Methode de transport*/
	private Transport tr;
	
	/*Node src*/
	private Node node;

	public Factorymessage(Node node , Transport tr) {
		this.tr = tr;
		this.node = node;
	}
	
	public void sendAskAgaineMessage(int InnbCycle) {
		//to do schedual
	}
	
	
	public void sendAccept(long iddest , long val, int roundId) {
	}
	
	public void sendPromise() {
		
	}
	
}
