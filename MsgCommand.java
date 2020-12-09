
public class MsgCommand {

	private String msg;
	private String command;
	private String nick;
	
	public MsgCommand(String nick) {
		this.nick = nick;
	}
	
	
	public String getNickName() {
		return nick;
	}
}
