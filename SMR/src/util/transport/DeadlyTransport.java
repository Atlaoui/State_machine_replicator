package util.transport;
import java.util.ArrayList;
import java.util.List;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Node;
import peersim.transport.Transport;

public final class DeadlyTransport implements Transport {
	
	
	
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_DROP = "drop";
	private static final String PAR_FAULTYNODES = "faultynodes";
	
	
	private final int transport;
	private final float loss;
	private final List<Long> faulty_ids;
	
	
	public DeadlyTransport(String prefix)
	{
		transport = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		loss = (float) Configuration.getDouble(prefix+"."+PAR_DROP);
		faulty_ids = new ArrayList<Long>();
		String tmp = Configuration.getString(prefix+"."+PAR_FAULTYNODES);
		for(String s : tmp.split("_")){
			faulty_ids.add(Long.parseLong(s));
		}
	}
	
	@Override
	public Object clone()
	{
		Object res=null;
		try{
			res=super.clone();
		}catch( CloneNotSupportedException e ) {} // never happens
		return res;
	}

	
	@Override
	public void send(Node src, Node dest, Object msg, int pid) {
		try
		{
			if(src.getFailState() != Fallible.OK){
				return;
			}
			Transport t = (Transport) src.getProtocol(transport);
			t.send(src, dest, msg, pid);
			if(faulty_ids.contains(src.getID()) &&  CommonState.r.nextFloat() <= loss ){
				// ON EST MORT
				System.err.println("time "+CommonState.getTime()+" "+src.getID()+" est mort en envoyant un message à "+dest.getID());
				src.setFailState(Fallible.DEAD);
			}
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("Protocol " +
					Configuration.lookupPid(transport) + 
					" does not implement Transport");
		}
	}

	@Override
	public long getLatency(Node src, Node dest) {
		Transport t = (Transport) src.getProtocol(transport);
		return t.getLatency(src, dest);
	}

}