package util;

import java.util.HashMap;
import java.util.List;

import util.request.Request;

public class PersistantStorage {
	private PersistantStorage() {}
	
	private static HashMap<Integer, List<Request>> H = new HashMap<>();
	
	public static List<Request> getH(int id) {
		return H.get(id);
	}
	
	public static void setH(int id , List<Request> list) {
		H.replace(id,list);
	}
}
