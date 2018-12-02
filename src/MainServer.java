import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer implements Runnable {

    private final Socket s;

    public MainServer(Socket s) {
        this.s = s;
    }

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(1234);
        while(true) {
            Socket s = ss.accept();
            new Thread(new MainServer(s)).start();
        }
    }


    public void run() {
        try {
            PrintWriter wr = new PrintWriter(s.getOutputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));

            while(true) {
                String input = rd.readLine();
                if(input == null) break;

                String[] cmds = input.split(" ");
                System.out.println("Recebeu " + cmds.length + " palavras.");
                wr.println("Recebeu " + cmds.length + " palavras.");
                wr.flush();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
