import java.io.Serializable;

public class Nick extends Object implements Serializable {

	private String nick;
	private String msg;
	
	public Nick(String nick) {
		this.nick = nick;
		this.msg = "";
	}
	
	public String getNick() {
		return this.nick;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
}
