import java.net.*;
import java.io.*;
import java.util.*;

public class CmdServer extends Thread {
	String[] sourceArray = { "whoami", "ls", "ps", "man", "echo", "date" };
	Set<String> specialCases = new HashSet<String>(Arrays.asList(sourceArray));
	ServerSocket sock;
	Socket client;
	PrintWriter pout;
	BufferedReader in;
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;

	String clientMessage = "empty";
	String revClientMessage = "rev";

	// Function to see if port is avaliable.
	public static boolean isTcpAvailable(int port) {
		boolean portAvalability;
		try (ServerSocket testSocket = new ServerSocket(port)) {
			portAvalability = true;
		} catch (Exception ex) {
			portAvalability = false;
		}
		return portAvalability;
	}

	// Function to return reverse message from client
	String ReverseMessage(String message) {
		StringBuilder input = new StringBuilder();
		input.append(message);
		input = input.reverse();
		//String ReverseStringMessage = input.toString();
		return message;//ReverseStringMessage;
		// SysLib.cout("(Server) Reversed string: "+ revClientMessage);
	}

	String ResponseMessage(String message){
		SysLib.cout("(from client) "+message+"\n");
		StringBuffer stringBuffer = new StringBuffer();
		SysLib.cout(" (Server) Enter Message: ");
		SysLib.cin(stringBuffer);
		return stringBuffer.toString();
	}

	// Set up server port
	void setUpServer() {
		try {
			for (int i = 0; i <= 500; i++) {
				if (isTcpAvailable(5000 + i) == true) {
					sock = new ServerSocket(5000 + i);
					SysLib.cout(sock.getLocalSocketAddress() + " Listening on port: " + sock.getLocalPort() + "\n");
					break;
				}
			}
		} catch (IOException e) {
			SysLib.cout("Error in Server setup: " + e);
		}
	}

	void connectToClientServer() {
		try {
			client = sock.accept();
			pout = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(client.getInputStream()));

		} catch (IOException e) {
			SysLib.cout("Error in Server setup: " + e);
		}
	}

	void DisplayClientMessage() {
		try {
			String line;
			while ((line = in.readLine()) != null) {
				String out = "";
				String[] arrOfStr = line.split(" ");
				if (specialCases.contains(arrOfStr[0])) {
					out = processCmds(arrOfStr);
				} else {
					out = ResponseMessage(line);//ReverseMessage(line);
				}
				// pout.println("breakdance");
				// SysLib.cout(out);
				pout.println(out + "\nBREAKDANCING");

			}

		} catch (IOException e) {
			SysLib.cout("Error in Server setup: " + e);
		}
	}

	void getDataStream() {
		try {
			dataInputStream = new DataInputStream(
					client.getInputStream());
					dataOutputStream = new DataOutputStream(
				client.getOutputStream());
				// Here we call receiveFile define new for that
		// file
		receiveFile("NewFile1.txt");
		dataInputStream.close();
		dataOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	String processCmds(String[] lines) {
		String output = "";
		Process p;
		try {
			p = Runtime.getRuntime().exec(lines);
			BufferedReader pRead = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			try {
				while ((line = pRead.readLine()) != null) {
					output += "\n\t" + line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return output;
	}

	private static void receiveFile(String fileName)
			throws Exception {
		int bytes = 0;
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		long size = dataInputStream.readLong(); // read file size
		byte[] buffer = new byte[4 * 1024];
		while (size > 0
				&& (bytes = dataInputStream.read(
						buffer, 0,
						(int) Math.min(buffer.length, size))) != -1) {
			// Here we write the file using write method
			fileOutputStream.write(buffer, 0, bytes);
			size -= bytes; // read upto file size
		}
		// Here we received file
		//System.out.println("File is Received");
		fileOutputStream.close();
	}

	public void run() {
		try {

			setUpServer();
			while (true) {
				connectToClientServer();
				DisplayClientMessage();
				getDataStream();
				pout.flush();
				pout.close();
				in.close();
				client.close();
			}
		} catch (IOException ioe) {
			System.err.println(ioe);
			SysLib.exit();
		}
	}

	// Start CmdClient here
	public static void main(String args[]) {
		new CmdServer().start();
	}
}