package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.request.ReadRequest;
import util.request.Request;
import util.request.WriteRequest;

public class Sequence {
	private static List<Request> seqR = new ArrayList<>();

	private Sequence() {}

	public static void set(int len) {
		Random r = new Random();
		for(int i = 0 ;i<len;i++){
			if(r.nextBoolean())
				seqR.add(new WriteRequest());
			else
				seqR.add(new ReadRequest());
		}
	}


public static List<Request> get() {
	return seqR;
}
public static boolean popOne() {
	if(seqR.size()!=0) {
		seqR.remove(0);
		return true;
	}
	return false;
}
public static boolean isEmpty() {
	return seqR.isEmpty();
}
}
