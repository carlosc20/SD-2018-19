import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost", 1234);

            while(true) {
                try {
                    PrintWriter pw = new PrintWriter(socket.getOutputStream());
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    );

                    pw.println("Boas");
                    pw.flush();
                    pw.close();
                    socket.close();
                } catch (IOException e) { }
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

}
