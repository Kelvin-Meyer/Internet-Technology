import java.io.File;
import java.io.FileNotFoundException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DNSPacket {
	
	private DatagramPacket packetOut;
	
	public DNSPacket(DatagramPacket packetIn){
		this.packetOut = packetIn;
	}
	
	/**
	 * Remove first 12 bytes from an array
	 * @param buffer	The buffer to remove the first 12 bytes from
	 * @return			An array with the first 12 bytes removed
	 */
	private byte[] removeFirst12Byte(byte[] buffer){
		byte[] bufferCopy = new byte[buffer.length - 12];

		System.arraycopy(buffer, 12, bufferCopy, 0, bufferCopy.length);
		
		return bufferCopy;
	}
	
	/**
	 * Checks if the "hosts.txt" file contains a host
	 * @param host	The host to check for
	 * @return		True if the file contains a host
	 * 				False if the file doesn't contain the host
	 */
	private boolean containsHost(String host){
		File file = new File("hosts.txt");

		try {
		    Scanner scanner = new Scanner(file);

		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        if (line.contains(host + " ")) {
		        	scanner.close();
		            return true;
		        }
		    }
		    scanner.close();
		    
		} catch(FileNotFoundException e) { 
		    e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Get host from packetOut
	 * @return	The host in a String
	 */
	private String getHost(){
		byte[] buffer = packetOut.getData();
		buffer = removeFirst12Byte(buffer);
		
		int startIndex = 0;
		String result = "";
		int wordLength;
	    
		while (result.equals("") || !containsHost(result)) {
			wordLength = buffer[startIndex];
			
			if (startIndex != 0){
				result += ".";
			}
			
			for (int i = (startIndex + 1); i <= wordLength + startIndex; i++) {
				result += (char) buffer[i];
			}
			
			startIndex = wordLength + startIndex + 1;
			
			if (startIndex >= buffer.length || (buffer[startIndex] + startIndex) >= buffer.length){
				break;
			}
		}
		
		return result;
		
	}
	
	/**
	 * Get an IP from the "hosts.txt" file when you know the host
	 * @param host	The host to get the ip from
	 * @return		The IP adress
	 */
	private String getIpByHost(String host){
		File file = new File("hosts.txt");
		host = host + " ";
		
		try {
		    Scanner scanner = new Scanner(file);

		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        if (line.contains(host)) { 
		        	scanner.close();
		            line = line.replace(host, "");
		            return line;
		        }
		    }
		    
		    scanner.close();
		    
		} catch(FileNotFoundException e) { 
		    e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Create a response from a request
	 * @param quest		The request
	 * @param ips		The ips to be given back to the user
	 * @return			Response in a byte array
	 */
	private byte[] createDNSResponse(byte[] question, byte[] ips) {
	    int start = 0;
	    byte[] response = new byte[1024];

	    int[] headers = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	    int[] payload = { 192, 12, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 };
	    
	    for (int value : headers) {
	    	response[start] = (byte) value;
	    	start++;
	    }
	    
	    response[0] = question[0]; // Identification
	    response[1] = question[1]; // Identification
	    response[4] = question[4]; // Type
	    response[5] = question[5]; // Type
	    response[6] = question[4]; // Class
	    response[7] = question[5]; // Class
	    
	    System.arraycopy(question, 12, response, start, question.length - 12); // Name
	    start += question.length - 12;

	    for (int value : payload) {
	    	response[start] = (byte) value;
	    	start++;
	    }

	    // IP address in response
	    for (byte ip : ips) {
	    	response[start] = ip;
	    	start++;
	    }

	    byte[] result = new byte[start];
	    System.arraycopy(response, 0, result, 0, start);

	    return result;
	}
	
	/**
	 * Get packet out with the answer
	 * @return	the datagrampacket with an answer
	 */
	public DatagramPacket getPacketOut(){
		
		byte[] data = packetOut.getData();
        final byte[] udpreq = new byte[packetOut.getLength()];
        System.arraycopy(data, 0, udpreq, 0, packetOut.getLength());
		
        String domain = getHost();
        String ipString = getIpByHost(domain);
        
        System.out.println("Request: " + domain + " -> " + ipString);
        
		byte[] ip = null;
		
		InetAddress ipInet;
		try {
			ipInet = InetAddress.getByName(ipString);
			
			ip = ipInet.getAddress();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
        byte[] answer = createDNSResponse(udpreq, ip);
        
        this.packetOut.setData(answer);
		return this.packetOut;
	}
	
	@Override
	public String toString() {
		byte[] buffer = packetOut.getData();
		int length = packetOut.getLength();
		
		String response = "";
		
		for (int i = 0; i < length; i++) {
			response += buffer[i];
			if ( buffer[i] >= 32 && buffer[i] <= 128 )
				response += " - " + (char) buffer[i] + "\n";
			else
				response += "\n";
		}
		
		response += "\n-------------------------------------------------------\n\n";
		
		return response;
	}
	
}
