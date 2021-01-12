package util;

import peersim.core.Node;
import peersim.transport.Transport;
// pas sur qu'il y est besoin de ça 
public class SafeTransport implements Transport  {



	@Override
	public long getLatency(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void send(Node arg0, Node arg1, Object arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object clone()  {
		Object res=null;
		try{
			res=super.clone();
		}catch( CloneNotSupportedException e ) {} // never happens
		return res;
	}


}
