import java.io.Serializable;

public class ClientInfo extends Object implements Serializable {

	private String nick;
	
	public ClientInfo(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
}
