import java.net.*;
import java.io.*;
import java.util.*;

public class DateServer extends Thread {
    @Override
    public void run() {
        main();
        SysLib.exit();
    }

    public void main() {
        try {

            ServerSocket sock = new ServerSocket(0);
            SysLib.cout("Listening on port: " + sock.getLocalPort());

            while (true) {

                Socket client = sock.accept();

                PrintWriter pout = new PrintWriter(client.getOutputStream(), true);

                pout.println(new java.util.Date().toString());
                client.close();
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
}
