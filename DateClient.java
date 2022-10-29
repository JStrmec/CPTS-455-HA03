import java.util.*;
import java.net.*;
import java.io.*;

public class DateClient extends Thread {

    private int port;
    public DateClient(String [] args){
        this.port = Integer.parseInt(args[0]);
    }

    @Override
    public void run() {
        main();
        SysLib.exit();
    }

    public void main() {
        try {
            Socket sock = new Socket("localhost", this.port);

            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String date;
            while ((date = in.readLine()) != null) {
                System.out.println(date);
            }

            sock.close();
        } catch (IOException ioe) {
            System.err.println(ioe);
        }

    }

}

