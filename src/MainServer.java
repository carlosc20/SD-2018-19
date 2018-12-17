import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer implements Runnable {

    private final Socket s;
    private static Manager man;

    public MainServer(Socket s) {
        this.s = s;
    }

    public static void main(String[] args) throws Exception {

        man = new Manager();

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

                String[] cmds = input.split(" ",2);
                try {
                    switch (cmds[0].toLowerCase()) {
                        case "registar": {
                                if(cmds.length < 2) {
                                    throw new NumeroArgumentosInsuficiente(2);
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    throw new NumeroArgumentosInsuficiente(2);
                                }
                                String email = args[0];
                                String password = args[1];
                                Manager.getInstance().registerUser(email, password);
                                wr.println("Sucesso: Registado " + email);
                            }
                            break;
                        case "entrar": {
                                if(cmds.length < 2) {
                                    throw new NumeroArgumentosInsuficiente(2);
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    throw new NumeroArgumentosInsuficiente(2);
                                }
                                String email = args[0];
                                String password = args[1];
                                if (Manager.getInstance().checkCredentials(email, password))
                                    wr.println("Sucesso: Entrou " + email);
                                else wr.println("Credenciais incorretas.");
                            }
                            break;
                        default:
                            wr.println("Comando não existe.");
                    }
                } catch (Exception e) {
                    wr.println("Erro: " + e.getClass().getName() + " " + e.getMessage());
                } finally {
                    wr.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
