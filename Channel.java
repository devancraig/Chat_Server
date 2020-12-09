import java.io.Serializable;
import java.util.ArrayList;

public class Channel extends Object implements Serializable{
	
	private String name;
	private ArrayList<Connection> conns;
	public Channel(String name) {
		this.name = name;
		conns = new ArrayList<Connection>();
	}
	
	public String getChannelName() {
		return name;
	}
	
	public void addConnection(Connection c) {
		conns.add(c);
	}
	
	public void removeConnection(Connection c) {
		if(conns.contains(c)) {
			conns.remove(c);
		}
	}
	
	public ArrayList<Connection> getConnections() {
		return conns;
	}
	
	public ArrayList<String> getNicks() {
		ArrayList<String> ret = new ArrayList<String>();
		for(Connection each : conns) {
			ret.add(each.getClient().getNick());
		}
		
		return ret;
	}
}
