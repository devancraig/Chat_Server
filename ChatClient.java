import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String userName;

	public static void main(String args[]) {
		if (args.length != 2) {
			System.err.println("Usage: java ChatClient -p <port#>");
			System.exit(1);
		}

		while (true) {
			System.out.print("Enter /connect <server-name>: ");
			Scanner scanner = new Scanner(System.in);
			String cmd = scanner.nextLine();
			if (cmd.equals("/connect localhost")) {
				String host = cmd.substring(9);
				ChatClient client = new ChatClient();
				if (client.ClientStart(host, args[1])) {
					client.startCommands();
				}
			} else {
				System.err.println("\nError: Try /connect localhost");
			}
		}
		
	}

	public Boolean ClientStart(String hostname, String port) {

		try {
			Socket server = new Socket(hostname, Integer.parseInt(port));

			out = new ObjectOutputStream(server.getOutputStream());
			in = new ObjectInputStream(server.getInputStream());

			Console console = System.console();

			String userName = console.readLine("\nSet a NickName: ");
			new ListenForChat().start();

			// Set intial nickname
			Nick nick = new Nick(userName);
			try {
				out.writeObject(nick);
				out.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return true;
			// Start normal chat server interaction functionality

		} catch (IOException e) {
			System.out.println("I/O error " + e); // I/O error
			return false;
		}
	}

	public void startCommands() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String input = scanner.nextLine();
			Command cmd = new Command();
			if(input.contains("/quit")){
				break;
			}
			else if (input.contains("/")) {
				cmd.setCommand(input);
				cmd.setNickName(this.userName);
				try {
					out.writeObject(cmd);
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			else {
//				cmd.setCommand(input);
//				cmd.setNickName(this.userName);
				Message msg = new Message(this.userName);
				msg.setMessage(input);
				try {
					out.writeObject(msg);
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class ListenForChat extends Thread {

		public void run() {
			while (true) {
				try {
					Object obj = in.readObject();
					if (obj instanceof String) {
						System.out.println((String) obj);
					}
					if (obj instanceof Command) {
						System.out.println( ((Command)obj).getMsg());
					}
					if (obj instanceof Message) {
						System.out.println("> "+ "@" + ((Message) obj).getNickName() + ": " + ((Message) obj).getMessage());
					}
					if (obj instanceof Nick) {
						String nick = ((Nick) obj).getNick();
						userName = nick;
						System.out.println( ((Nick)obj).getMsg());
						
						
					}
				} catch (ClassNotFoundException | IOException e2) {
				}
			}
		}
	}
}
