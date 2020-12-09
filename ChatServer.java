
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.net.InetAddress;

/**
 * A multithreaded time server.
 * 
 * @author amit
 */
public class ChatServer {
	private ServerSocket ss;
	// private static Object lock = new Object();

	/**
	 * Creates a server socket that listens on the specified port number.
	 * 
	 * @param port The port number for the server.
	 */
	public ChatServer(int port) {

		try {

			ss = new ServerSocket(port);
			System.out.println("TimeServer up and running on port " + port + " " + InetAddress.getLocalHost());
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * The main server method that accepts connections and starts off a new thread
	 * to handle each accepted connection.
	 */
	public void runServer(int debug) {
		Socket client;
		ArrayList<Channel> channels = new ArrayList<Channel>();
		ArrayList<Connection> conns = new ArrayList<Connection>();
		ArrayList<String> names = new ArrayList<String>();
		int count = 0;
		Channel channelOne = new Channel("Channel 1");
		Channel channelTwo = new Channel("Channel 2");
		Channel channelThree = new Channel("Channel 3");

		channels.add(channelOne);
		channels.add(channelTwo);
		channels.add(channelThree);
		TimerStart time = new TimerStart();
		Timer timer = new Timer();

		timer = time.myTimer(timer);

		try {
			while (true) {

				client = ss.accept();

				System.out.println(
						"Received connect from " + client.getInetAddress().getHostAddress() + ": " + client.getPort());

				new ServerConnection(client, count, channels, conns, names, timer, debug).start();

			}
		} catch (IOException e) {
			System.err.println(e);
		} catch (Exception ee) {
			System.out.println("idle for to longs");
		}
	}

	public static void main(String args[]) {
		if (args.length != 4) {
			System.err.println("Usage: java ChatServer -p <port#> -d <debug-level>");
			System.exit(1);
		}
		int port = Integer.valueOf(args[1]);
		int debug = Integer.valueOf(args[3]);
		ChatServer server = new ChatServer(port);
		server.runServer(debug);
	}

	/**
	 * Handles one connection in a separate thread.
	 */
	class ServerConnection extends Thread {
		private ArrayList<Connection> conns;
		private ArrayList<Channel> channels;
		private ArrayList<String> names;
		private Command cmd = new Command();
		private Socket client;
		private Timer timer;
		private int debug;
		TimerStart time = new TimerStart();

		ServerConnection(Socket client, int count, ArrayList<Channel> channels, ArrayList<Connection> conns,
				ArrayList<String> names, Timer timer, int debug) throws SocketException {
			this.conns = conns;
			this.channels = channels;
			this.client = client;
			this.names = names;
			this.timer = timer;
			this.debug = debug;
			setPriority(NORM_PRIORITY - 1);
		}

		public void run() {

			try {

				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				timer = time.myTimer(timer);
				while (true) {
					
					Object obj = in.readObject();
					if (obj instanceof Nick) {
						timer = time.myTimer(timer);
						String nick = ((Nick) obj).getNick();
						// TODO: if client nickname in conns for a connection we know we are changing a
						// nickname
						// must also check new nick is not already taken.
						boolean check = true;
						for (String s : names) {
							if (s.equals(nick)) {
								check = false;
							}
						}
						if (check) {
							// Store connection
							ClientInfo new_client = new ClientInfo(nick);
							Connection cur_conn = new Connection(out, in, UUID.randomUUID().toString(), new_client);
							names.add(nick);
							conns.add(cur_conn);
							((Nick) obj).setMsg("Server: Allows nickname");
							if (debug == 1) {
								System.out.println("SUCCESS: Client nickname recorded for " + nick);
							}
							out.writeObject(obj);
							out.flush();
						} else {
							// Store connection
							Random rand = new Random();
							nick = "Anonymous" + rand.nextInt(1000);
							ClientInfo new_client = new ClientInfo(nick);
							Connection cur_conn = new Connection(out, in, UUID.randomUUID().toString(), new_client);
							names.add(nick);
							conns.add(cur_conn);

							((Nick) obj).setNick(nick);
							((Nick) obj).setMsg(
									"Server: nickname denied, setting anonymous nickname \nYou can change username to a one thats not take with command /nick <username>");
							System.out.println("ERROR: Client nickname is a duplicate");
							out.writeObject(obj);
							out.flush();

						}
						

					} else if (obj instanceof Message) {
						timer = time.myTimer(timer);
						Message msg = ((Message) obj);
						if (debug == 1) {
							System.out.println("SUCCESS: Received message from " + msg.getNickName());
						}
						int i = 0;
						int c = 0;
						String broadcastChannel = "";
						int channelIndex = 0;
						for (Channel ch : channels) {
							for (Connection conn : ch.getConnections()) {
								if (conn.getClient().getNick().equals(msg.getNickName())) {
									broadcastChannel = channels.get(i).getChannelName();
									channelIndex = i;
								}
								c = c + 1;
							}
							i = i + 1;
						}
						if (broadcastChannel == "") {

							for (Connection conn : conns) {
								if (conn.getClient().getNick().equals(msg.getNickName())) {
									System.out.println("ERROR: Client can't send a message, not in a channel");
									msg.setMessage("Join a channel to send a messsage.");
									conn.getObjOutStream().writeObject(msg);
									conn.getObjOutStream().flush();
								}
							}

						} else {

							for (Connection conn : channels.get(channelIndex).getConnections()) {
								if (debug == 1) {
									System.out.println("SUCCESS: Broadcasting to all users in channel "
											+ channels.get(channelIndex).getChannelName());
								}
								conn.getObjOutStream().writeObject(msg);
								conn.getObjOutStream().flush();
							}
						}

					} else {
						timer = time.myTimer(timer);
						Command cmd = (Command) obj;
						String CmdOrMsg = getCmdOrMsg(cmd.getCommand().trim());
						if (CmdOrMsg.equals("Command")) {
							if (debug == 1) {
								System.out.println("SUCCESS: Received command from " + cmd.getNickName()
										+ ". Command is " + cmd.getCommand());
							}
							if (cmd.getCommand().equals("/list")) {
								String ret = "Channels - Connections\n";
								for (Channel ch : channels) {
									int conn_count = 0;
									for (Connection each : ch.getConnections()) {
										if (debug == 1) {
											System.out.println(
													"SUCCESS: Working on counting users in channel, one found for channel "
															+ ch.getChannelName());
										}
										conn_count = conn_count + 1;
									}
									ret += ch.getChannelName() + " - " + conn_count + "\n";
								}
								out.writeObject(ret);
								out.flush();
							}else if(cmd.getCommand().equals("/stats")){
								String ret = "Channels - Connections\n";
								for (Channel ch : channels) {
									int conn_count = 0;
									for (Connection each : ch.getConnections()) {
										if (debug == 1) {
											System.out.println(
													"SUCCESS: Working on counting users in channel, one found for channel "
															+ ch.getChannelName());
										}
										conn_count = conn_count + 1;
									}
									ret += ch.getChannelName() + " - " + conn_count + "\n";
								}
								for (String s : names){
									ret += "\nUsers: " + s;
								}

								out.writeObject(ret);
								out.flush();
							} else if(cmd.getCommand().contains("/help")){
								Message msg = new Message("hello");

								cmd.setMsg(msg.getCommandString());
								out.writeObject(cmd);
								out.flush();

							}
							else if (cmd.getCommand().contains("/nick")) {

								boolean nameCheck = true;
								String oldNick = cmd.getNickName();
								if (cmd.getCommand().length() < 5) {
									System.out.println("ERROR: No nick name provided");
									((Command) obj).setMsg("Server: You did not provide a nickname.");
									out.writeObject(obj);
									out.flush();
								} else {
									String newNick = cmd.getCommand().substring(6);
									if (debug == 1) {
										System.out.println(
												"SUCCESS: Received new nickname request from " + cmd.getNickName());
									}
									for (String s : names) {
										if (s.equals(newNick)) {
											nameCheck = false;
										}
									}
									if (nameCheck) {
										if (debug == 1) {
											System.out.println("SUCCESS: Client nickname recorded for " + newNick);
										}
										names.add(newNick);
										names.remove(oldNick);
										Nick officialNick = new Nick(newNick);
										officialNick.setMsg("Server: Allows nickname");
										int c = 0;
										for (Connection each : conns) {
											if (each.getClient().getNick().equals(oldNick)) {
												conns.get(c).getClient().setNick(newNick);
											}
											c = c + 1;
										}
										out.writeObject(officialNick);
										out.flush();
									} else {
										cmd.setMsg(
												"Server: nickname denied, nickname already taken. You're keeping your old one for now.");
										System.out.println("ERROR: Client nickname is a duplicate");

										cmd.setNickName(oldNick);
										out.writeObject(cmd);
										out.flush();
									}
								}

							} else if (cmd.getCommand().contains("/join")) {
								String oldNick = cmd.getNickName();
								if (debug == 1) {
									System.out.println(
											"SUCCESS: Received new channel join request from " + cmd.getNickName());
								}
								if (cmd.getCommand().length() < 5) {
									System.out.println("ERROR: No channel name provided");
									((Command) obj).setMsg("Server: You did not provide a channel name");
									out.writeObject(obj);
									out.flush();
								} else {
									String newChannel = cmd.getCommand().substring(6);
									if (debug == 1) {
										System.out.println(
												"SUCCESS: Received request to join channel named " + newChannel);
									}
									int i = 0;
									int c = 0;
									boolean inChannel = false;
									int channelIndex = 0;
									for (Channel ch : channels) {
										if (ch.getChannelName().equals(newChannel)) {
											for (Connection each : ch.getConnections()) {
												if (each.getClient().getNick().equals(oldNick)) {
													inChannel = true;
													System.out.println("ERROR: User already in channel");
													((Command) obj).setMsg("Server: You are already in the channel");
													out.writeObject(obj);
													out.flush();
													inChannel = true;
												}
												i = i + 1;
											}
										}
										c = c + 1;
									}
									if (!inChannel) {
										if (i > channels.size()) {
											System.out.println("ERROR: No channel found");
											((Command) obj).setMsg("Server: Channel does not exist");
											out.writeObject(obj);
											out.flush();
										} else {
											i = 0;
											c = 0;
											for (Channel ch : channels) {
												if (ch.getChannelName().equals(newChannel)) {
													for (Connection each : conns) {
														if (each.getClient().getNick().equals(oldNick)) {
															channels.get(c).addConnection(conns.get(i));

														}
														i = i + 1;
													}
												}
												c = c + 1;
											}
										}
										if (debug == 1) {
											System.out.println("SUCCESS: User " + cmd.getNickName()
													+ " has joined the channel " + newChannel);
										}
										((Command) obj).setMsg("Server: You have joined the channel");
										out.writeObject(cmd);
										out.flush();
									}
								}
							} else if (cmd.getCommand().contains("/leave")) {

								String oldNick = cmd.getNickName();
								if (debug == 1) {
									System.out.println(
											"SUCCESS: Received new channel leave request from " + cmd.getNickName());
								}
								boolean inChannel = false;
								int i = 0;
								int c = 0;
								// Find user in a channel as they did not specify
								if (cmd.getCommand().length() == 6) {
									for (Channel ch : channels) {
										for (Connection each : ch.getConnections()) {
											if (each.getClient().getNick().equals(oldNick)) {
												if (debug == 1) {
													System.out.println("SUCCESS: User " + cmd.getNickName()
															+ " has left the channel " + ch.getChannelName());
												}
												((Command) obj).setMsg("Server: You have left the channel");
												channels.get(c)
														.removeConnection(channels.get(c).getConnections().get(i));
												out.writeObject(obj);
												out.flush();
												inChannel = true;
												break;
											}
											i = i + 1;
										}

										c = c + 1;
									}
									// Check to see if a user was not found in any channel
									if (!inChannel) {
										System.out.println("ERROR: User not in channel");
										((Command) obj).setMsg("Server: You are not in the channel");
										out.writeObject(obj);
										out.flush();
									}
								} else { // User specified channel, find channel and remove user
									String newChannel = cmd.getCommand().substring(7);
									if (debug == 1) {
										System.out.println(
												"SUCCESS: Received request to leave channel named " + newChannel);
									}
									i = 0;
									c = 0;
									for (Channel ch : channels) {
										if (ch.getChannelName().contains(newChannel)) {
											// Find current connection add to channel
											for (Connection each : conns) {
												if (each.getClient().getNick().equals(oldNick)) {
													if (debug == 1) {
														System.out.println("SUCCESS: User " + cmd.getNickName()
																+ " has left the channel " + ch.getChannelName());
													}
													((Command) obj).setMsg("Server: You have left the channel");
													channels.get(i)
															.removeConnection(channels.get(i).getConnections().get(c));
													out.writeObject(cmd);
													out.flush();
												}
												c = c + 1;
											}
										}
										i = i + 1;
									}
									if (i > channels.size()) {
										System.out.println("ERROR: User not in channel");
										((Command) obj).setMsg("Server: You are not in the channel");
										out.writeObject(obj);
										out.flush();
									}
								}
							} else {
								out.writeObject(cmd);
								out.flush();
							}
						}

					}

				}

			} catch (IOException e) {
				System.out.println("Client left the server");
			} catch (ClassNotFoundException eee) {
				// TODO Auto-generated catch block
				eee.printStackTrace();
			} catch (ClassCastException ee) {

			}
		}

		public String getCmdOrMsg(String s) {
			// For a command
			// cmd = new Command();
			if (s.contains("/")) {
				if (s.contains("/nick")) {
					s = "Command";
				} else if (s.equals("/connect")) {
					cmd.setCommand(s);
					s = "Command";
				} else if (s.equals("/list")) {
					s = "Command";
				} else if (s.contains("/join")) {
					s = "Command";
				} else if (s.equals("/leave")) {
					cmd.setCommand(s);
					s = "Command";
				} else if (s.equals("/quit")) {
					cmd.setCommand(s);
					s = "Command";
				} else if (s.equals("/stats")) {
					cmd.setCommand(s);
					s = "Command";
				} else if (s.equals("/help")) {
					System.out.println("made it");
					cmd.setCommand(s);
					s = "Command";
				}  
				else {
					cmd.setCommand("Not a valid Command");
					s = "Command";
				}

			} else {
				s = "Message";
			}
			return s;
		}

	}

}