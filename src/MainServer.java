import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MainServer implements Runnable {

    private static final Map<String, Socket> sessions = new HashMap<>(); // utilizadores atualmente ligados
    private static final ManagerInterface manager = Manager.getInstance();
    private final Socket s;
    private String user;


    public MainServer(Socket s) {
        this.s = s;
    }

    public static void main(String[] args) throws Exception {

        ServerSocket ss = new ServerSocket(12345);
        while(true) {
            Socket s = ss.accept();
            new Thread(new MainServer(s)).start();
        }
    }


    public void run() {
        try {
            PrintWriter wr = new PrintWriter(s.getOutputStream(), true);
            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));

            wr.println("-> Está ligado ao servidor.");

            while(true) {
                String input = rd.readLine();
                if(input == null) break;

                String[] cmds = input.split(" ",2);
                try {
                    switch (cmds[0].toLowerCase()) {
                        case "registar": {
                                if(cmds.length < 2) {
                                    wr.println("-> Argumentos insuficientes, uso: registar <email> <password>");
                                    break;
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    wr.println("-> Argumentos insuficientes, uso: registar <email> <password>");
                                    break;
                                }
                                String email = args[0];
                                String password = args[1];
                                try {
                                    manager.registerUser(email, password);
                                    wr.println("-> Utilizador " + email + " registado com sucesso.");
                                } catch (EmailAlreadyUsedException e) {
                                    wr.println("-> Esse email já está a ser usado.");
                                }
                            }
                            break;
                        case "entrar": {
                                if(cmds.length < 2) {
                                    wr.println("-> Argumentos insuficientes, uso: entrar <email> <password>");
                                    break;
                                }
                                String[] args = cmds[1].split(" ", 2);
                                if (args.length < 2) {
                                    wr.println("-> Argumentos insuficientes, uso: entrar <email> <password>");
                                    break;
                                }
                                String email = args[0];
                                String password = args[1];
                                if (manager.checkCredentials(email, password)) {
                                    synchronized (sessions) {
                                        if (sessions.containsKey(email)) {
                                            wr.println("-> Já existe uma conexão com esse utilizador.");
                                            break;
                                        }
                                        sessions.put(email, s);
                                    }
                                    user = email;
                                    wr.println("->" + email + " entrou com sucesso.");
                                    session(wr, rd);
                                }
                                else wr.println("-> Dados incorretos.");
                            }
                            break;
                        default:
                            wr.println("-> Comando não existe.");
                    }
                } catch (Exception e) {
                    wr.println("-> Erro: " + e.getClass().getName() + " " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (sessions) {
                sessions.remove(user);
            }
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

            String[] cmds = input.split(" ");
            try {
                switch (cmds[0].toLowerCase()) {
                    case "standard": //---------------------------------------------------------------------------------
                        if(cmds.length < 2) {
                            wr.println("-> Argumentos insuficientes, uso: standard <tipo>");
                            break;
                        }
                        String type = cmds[1];
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    wr.println("-> Pedido de reserva standard do tipo " + type + " criado.");
                                    int id = manager.createStandardReservation(user, type);
                                    wr.println("-> Reserva standard do tipo " + type + " iniciada com sucesso, id = " + id);
                                } catch (ServerTypeDoesntExistException e) {
                                    wr.println("-> Tipo de servidor não existe.");
                                }
                            }
                        }).start();
                        break;
                    case "leilao": //-----------------------------------------------------------------------------------
                        if(cmds.length < 3) {
                            wr.println("-> Argumentos insuficientes, uso: leilao <tipo> <licitação>");
                            break;
                        }
                        type = cmds[1];
                        int bid = Integer.parseInt(cmds[2]); // TODO: 01/01/2019 parse
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    wr.println("-> Pedido de reserva de leilão do tipo " + type + " com licitação "
                                            + bid + "€ criado.");
                                    int id = manager.createAuctionReservation(user, type, bid);
                                    wr.println("-> Reserva de leilão do tipo " + type
                                            + " com licitação " + bid +"€ iniciada com sucesso, id = " + id);
                                } catch (ServerTypeDoesntExistException e) {
                                    wr.println("-> Tipo de servidor não existe.");
                                }
                            }
                        }).start();
                        break;
                    case "divida": //-----------------------------------------------------------------------------------
                        int total = manager.getTotalDue(user);
                        wr.println("-> Dívida total:" + total + "cêntimos"); // TODO: 01/01/2019 mostrar em euros
                        break;
                    case "cancelar": //---------------------------------------------------------------------------------
                        if(cmds.length < 2) {
                            wr.println("-> Argumentos insuficientes, uso: cancelar <id da reserva>");
                            break;
                        }
                        int id = Integer.parseInt(cmds[1]);
                        try {
                            manager.cancelReservation(user, id);
                        } catch (ReservationDoesntExistException e) {
                            wr.println("-> Reserva não existe.");
                        }
                        break;
                    default:
                        wr.println("-> Comando não existe.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                wr.println("-> Erro: " + e.getClass().getName() + " " + e.getMessage());
            }
        }
    }

    public static Socket getSocket(String email){
        synchronized (sessions) {
            return sessions.get(email);
        }
    }
}
