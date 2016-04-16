import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

	private ServerSocket serverSocket;
	private ArrayList<Socket> sockets;
	
	public ChatServer(){
		try {
			serverSocket = new ServerSocket(2000);
			System.out.println("Server is started!\n");
			this.sockets = new ArrayList<>();
		} catch (IOException e) {
			System.err.println("Server failed to setup");
		}
		
	}
	
	public void serverLoop(){
		while(true){
			// Wacht tot client verbind
			
			try {
				
				Socket socket = serverSocket.accept();
				sockets.add(socket);
				System.out.println("A client connection has been setup (Address: " + socket.getRemoteSocketAddress() + ")");
				
				// maak client thread aan
				ClientThread client = new ClientThread(socket);
				client.start();
			} catch (IOException e) {
				System.out.println("A client socket failed to setup");
			}
			
		}
	}
	
	public class ClientThread extends Thread {

		private Socket socket;
		private PrintWriter writer;
		
		public ClientThread(Socket socket){
			this.socket = socket;
		}
		
		public void run(){
			while (socket.isConnected()){
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line = reader.readLine();
					
					if (line != null && !line.equals("")){
						System.out.println("> " + line);
						
						for (Socket client : sockets){
							if (!client.equals(socket)){
								writer = new PrintWriter(client.getOutputStream());
								writer.println(line);
								writer.flush();
							}
						}
					}
					
				} catch (IOException e) {
					//Nothing to do, error with connection
				}
			}
			System.out.println("Client connection disconnected!");
		}
		
	}
	
	public static void main(String[] args){
		ChatServer s = new ChatServer();
		s.serverLoop();
	}
	
}
