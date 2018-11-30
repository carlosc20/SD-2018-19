import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    public Client() {

    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
        new Thread(new Client()).start();
    }

    public void run() {
        try {
            Socket s = new Socket("localhost", 1234);

            PrintWriter wr = new PrintWriter(s.getOutputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));

            Scanner scanner = new Scanner(System.in);
            while(true) {
                String input = scanner.nextLine();
                if(input == null) break;
                wr.println(input);
                wr.flush();
            }
            scanner.close();
            //System.out.println(rd.readLine());

            s.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


}
