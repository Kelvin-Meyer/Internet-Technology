import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientThread extends Thread {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private Scanner scanner;
	private String nickname;
	
	public ClientThread(){
		
		try {
			socket = new Socket("localhost", 2000);
			socket.setKeepAlive(true);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			scanner = new Scanner(System.in);
			
			System.out.println("Welcome to this message system!");
			System.out.print("Choose a nickname: ");
			nickname = scanner.next();
			System.out.println("Hello " + nickname + "\n");
			
		} catch (UnknownHostException e) {
			System.err.print("Unknown host!");
		} catch (IOException e) {
			System.err.print("Can't connect to host!");
		}
		
	}
	
	@Override
	public void run() {
		
		while (socket.isConnected()){
			
			try {
				String line = in.readLine();
				
				if (line != null && !line.equals("")){
					System.out.println("> " + line);
				}
				
			} catch (IOException e) {
				//NOTHING
			} catch (NullPointerException e){
				//NOTHING
			}
			
		}
		
	}
	
	public void chat(){
		if (scanner.hasNextLine()){
			String line = scanner.nextLine();
			writeMessage(line);
		}
		
		chat();
	}
	
	private void writeMessage(String message){
		if (message != null && !message.isEmpty()){
			out.println(nickname + ": " + message);
			out.flush();
		}
	}
	
}
