import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.Object;

public class CmdClient extends Thread {
  // Variables
  private String hostName;
  private int port;
  String[] sourceArray = { "bye", "die" };
  Set<String> specialCases = new HashSet<String>(Arrays.asList(sourceArray));
  Socket sock = null;
  BufferedReader in;
  PrintWriter out;
  private static DataOutputStream dataOutputStream = null;
  private static DataInputStream dataInputStream = null;

  // Validation function to enforce Tcp ports from 5000-5500.
  boolean isPortValid(int Port) {
    if (Port >= 5000 || Port <= 5500) {
      return true;
    } else {
      return false;
    }
  }

  // Function to Set up Client server port.
  private void setUpClientServer() {
    try {
      /* make connection to server socket */
      sock = new Socket(hostName, port);
      SysLib.cout("Client Connected on : " + hostName + "\n");
    } catch (IOException e) {
      SysLib.cout("Error in Port Set up: " + e);
    }
  }

  /**
   * Constructor to set user inputed port
   * and hostname.If hostname isnt provided
   * the default to localhost.
   */
  public CmdClient(String args[]) {

    // Default to local host
    if (args.length < 2) {
      SysLib.cout("No Hostname was provided. Default to local host.");
      hostName = "127.0.0.1";

      int temp_port = Integer.parseInt(args[0]);
      // Port validator
      if (isPortValid(temp_port)) {
        port = Integer.parseInt(args[0]);
      } else {
        SysLib.cout("Port out of bounds... \n\n");
      }
    }
    // Connect to remote host.
    else {
      hostName = args[1];
      port = Integer.parseInt(args[0]);
    }
  }

  // sendFile function define here
  private static void sendFile(String path)
      throws Exception {
    int bytes = 0;
    // Open the File where he located in your pc
    File file = new File(path);
    FileInputStream fileInputStream = new FileInputStream(file);

    // Here we send the File to Server
    dataOutputStream.writeLong(file.length());
    // Here we break file into chunks
    byte[] buffer = new byte[4 * 1024];
    while ((bytes = fileInputStream.read(buffer)) != -1) {
      // Send the file to Server Socket
      dataOutputStream.write(buffer, 0, bytes);
      dataOutputStream.flush();
    }
    // close the file here
    fileInputStream.close();
    
  }

  public void run() {
    try {
      // Set up Client Server with port and hostname.
      setUpClientServer();
      // Check for stdin or message from server.
      String fromServer;
      StringBuffer stringBuffer = new StringBuffer();
      out = new PrintWriter(sock.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
      while (true) {
        SysLib.cout(" (Client) Enter Message: ");
        SysLib.cin(stringBuffer);
        StringBuffer fileResponse = new StringBuffer();
        SysLib.cout("(Client) Would you like to send a file? (y/n): ");
        SysLib.cin(fileResponse);
        if (fileResponse.toString().equals("y")) {
          StringBuffer filePath = new StringBuffer();
          SysLib.cout("Enter file path: ");
          SysLib.cin(filePath);
          dataInputStream = new DataInputStream(
              sock.getInputStream());
          dataOutputStream = new DataOutputStream(
              sock.getOutputStream());
          SysLib.cout("Sending the File to the Server\n");
          
          // Call SendFile Method 
          try{
          sendFile(filePath.toString());
        } catch(Exception e) {}
        }
        if (specialCases.contains(stringBuffer.toString())) {
          break; // dance
        }
        out.println(stringBuffer.toString());
        SysLib.cout("Client: (from server) ");
        String serverLine;
        while (!(serverLine = in.readLine()).equals("BREAKDANCING")) {
          SysLib.cout(serverLine + "\n");
        }
        stringBuffer.setLength(0);
      }
      dataInputStream.close();
      out.close();
      in.close();
      //sock.close();
      SysLib.exit();

    } catch (IOException e) {
      System.out.println("message execption: " + e);
    }
  }

  // Start CmdClient here
  public static void main(String args[]) {
    new CmdClient(args).start();
  }
}
