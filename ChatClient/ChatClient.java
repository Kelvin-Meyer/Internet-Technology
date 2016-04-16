
public class ChatClient {
	
	public static void main(String[] args) {

		ClientThread c = new ClientThread();
		c.start();
		c.chat();
		
	}

}
