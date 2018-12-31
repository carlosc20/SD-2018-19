import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class MainServer implements Runnable {

    private static Set<String> sessions = new HashSet<>(); // utilizadores atualmente ligados
    private static Manager manager = Manager.getInstance();
    private final Socket s;
    private String user;


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

                String[] cmds = input.split(" ",2);
                try {
                    switch (cmds[0].toLowerCase()) {
                        case "registar": {
                                if(cmds.length < 2) {
                                    wr.println("Argumentos insuficientes, uso: registar <email> <password>");
                                    break;
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    wr.println("Argumentos insuficientes, uso: registar <email> <password>");
                                    break;
                                }
                                String email = args[0];
                                String password = args[1];
                                manager.registerUser(email, password);
                                wr.println(email + " registado com sucesso.");
                            }
                            break;
                        case "entrar": {
                                if(cmds.length < 2) {
                                    wr.println("Argumentos insuficientes, uso: entrar <email> <password>");
                                    break;
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    wr.println("Argumentos insuficientes, uso: entrar <email> <password>");
                                    break;
                                }
                                String email = args[0];
                                String password = args[1];
                                if (manager.checkCredentials(email, password)) {
                                    if(sessions.contains(email)) {
                                        wr.println("Já existe uma conexão com esse utilizador.");
                                        break;
                                    }
                                    user = email;
                                    sessions.add(user);
                                    wr.println(email + " entrou com sucesso.");
                                    wr.flush();
                                    session(wr, rd);
                                }
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
        } finally {
            sessions.remove(user);
        }
    }

    private void session(PrintWriter wr, BufferedReader rd) {
        while (true) {
            String input = null;
            try {
                input = rd.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input == null) break;

            String[] cmds = input.split(" ", 2);
            try {
                switch (cmds[0].toLowerCase()) {
                    case "standard":
                        // TODO: recebe tipo de servidor
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int id = 0;
                                try {
                                    id = manager.createStandardReservation(user, "t3.micro");
                                    wr.println("Reserva de leilão iniciada com sucesso, id = " + id);
                                    wr.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    wr.println("Erro");
                                }
                            }
                        }).start();
                        break;
                    case "leilao":
                        // TODO: recebe tipo de servidor e licitaçao
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int id = 0;
                                try {
                                    id = manager.createAuctionReservation(user, "t3.micro", 100);
                                    wr.println("Reserva standard iniciada com sucesso, id = " + id);
                                    wr.flush();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    wr.println("Erro" + id);
                                }
                            }
                        }).start();
                        break;
                    case "divida":
                        // TODO:
                        int total = manager.getTotalDue(user);
                        char[] totalS = String.valueOf(total).toCharArray();
                        wr.println("Dívida total:" + totalS);
                        wr.flush();
                        break;
                    case "cancelar":
                        // TODO: recebe id da reserva a cancelar
                        manager.cancelReservation(user, 1);
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
    }

}
