import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static void main(String[] args) {

        try {
            ServerSocket ss = new ServerSocket(1234); //tem de ser maior de 1024

            while(true) {
                try {
                    Socket cs = ss.accept();
                    System.out.println("Cliente ligado");

                    PrintWriter pw = new PrintWriter(cs.getOutputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                    while(true) {
                        String line = br.readLine();
                        if (line == null) break;
                        System.out.println("Recebeu: " + line);
                    }
                    pw.print("Olá");
                    pw.flush();
                    pw.close();
                    cs.close();
                } catch (IOException e) { }
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

}
