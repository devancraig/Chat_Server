import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class Connection {
	private Socket s;
	private String id;
	private ClientInfo client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public Connection(ObjectOutputStream out, ObjectInputStream in, String id, ClientInfo client) {
		this.out = out;
		this.in = in;
		this.id = id;
		this.client = client;
	}
	
	public String getId() {
		return this.id;
	}
	
	public ObjectOutputStream getObjOutStream() {
		return this.out;
	}
	
	public ClientInfo getClient() {
		ClientInfo ret = client;
		return ret;
	}
}
