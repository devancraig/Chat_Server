import java.io.Serializable;
import java.util.ArrayList;

public class Message extends Object implements Serializable {

	private String msg;
	private String nick;
	private String error;
	
    public Message(String nick){
    	this.nick = nick;
    	this.error = "";
    }
    
	public String getCommandString() {
		StringBuilder s = new StringBuilder();
		s.append("----------------------------" + "---------------------------------------------------------------------\n");
		s.append("|command                    " + "| description                                                       |\n");
		s.append("----------------------------" + "---------------------------------------------------------------------\n");
		s.append("|/connect <server-name>     " + "| Connect to named server                                           |\n");
		s.append("|/nick <username>           " + "| Pick a nickname (should be unique among active users)             |\n");
		s.append("|/list                      " + "| List channels and number of users                                 |\n");
		s.append("|/join <channel>            " + "| Join a channel, all text typed is sent to all users on the channel|\n");
		s.append("|/leave [<channel>]         " + "| Leave the current (or named) channel                              |\n");
		s.append("|/quit                      " + "| Leave chat and disconnect from server                             |\n");
		s.append("|/stats                     " + "| Ask server for some stats                                         |\n");
		s.append("----------------------------" + "---------------------------------------------------------------------\n");
		return s.toString();
	}

	public String getMessage(){
		return msg;
	}

	public void setMessage(String msg){
		this.msg = msg;
	}
    
	public void setNickName(String nick) {
		this.nick = nick;
	}
	
	public String getNickName() {
		return nick;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public String getError() {
		return error;
	}
}
