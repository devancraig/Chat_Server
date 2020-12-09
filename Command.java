
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Command extends Object implements Serializable {
	
	private String cmd;
	private String nick;
	private String msg;
	//private ArrayList<String,Integer> users = new ArrayList<>();
	
    public Command(){
    	msg = "";
    }
    
    public String getCommand() {
    	return cmd;
    }
    
    public void setCommand(String cmd){
      this.cmd = cmd;
    }
    
    public void setNickName(String nick) {
    	this.nick = nick;
    }
    
    public String getNickName() {
    	return nick;
	}

    public void setMsg(String msg) {
    	this.msg = msg;
    }
    
    public String getMsg() {
    	return msg;
    }
    
	
}
