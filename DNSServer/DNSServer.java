import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class DNSServer {

	private DatagramSocket datagramSocket;
	
	public DNSServer(){
		try {
			datagramSocket = new DatagramSocket(53);
			progress();
		} catch (SocketException e) {
			System.err.print("Error with server setup");
		}
	}
	
	/**
	 * Loop while the datagramsocket isn't closed
	 * Each request a DNSPacket is created
	 */
	private void progress(){
		
		while (!datagramSocket.isClosed()){
			
			try {
				
				byte[] buffer = new byte[1024];
				DatagramPacket packetIn = new DatagramPacket(buffer, buffer.length);
				
				datagramSocket.receive(packetIn);
				
				DNSPacket dnspacket = new DNSPacket(packetIn);
				
				datagramSocket.send(dnspacket.getPacketOut());
				
			} catch (IOException e) {
				System.err.print("Error while receiving packet");
			}
			
		}
		
		System.out.println("DNS Server closed");
	}
	
	public static void main(String[] args){
		new DNSServer();
	}
	
}
