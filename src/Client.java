import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main (String[] args) {
        try {
            Socket s = new Socket("localhost", 12345);

            Thread read = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        while (true) {
                            String str = rd.readLine();
                            if (str == null) break;
                            String resultado[] = str.split(" ");
                            switch (resultado[0]) {
                                case "ligado" : System.out.println("-> Está ligado ao servidor"); break; // Sucesso
                                case "comandoNaoExiste" : System.out.println("-> Comando não existe."); break; // Erro
                                case "argumentosInsuficientes" : System.out.println("-> Argumentos insuficientes, use o comando \"ajuda\" para saber os comandos"); break; // Erro
                                case "registarSucesso" : System.out.println("-> Utilizador " +  resultado[1] + " registado com sucesso."); break; // Sucesso
                                case "registarEmailEmUso" : System.out.println("-> Esse email já está a ser usado."); break; // Erro
                                case "entrarSucesso" : System.out.println("-> O utilizador " + resultado[1] + " entrou com sucesso."); break; // Sucesso
                                case "reservaCancelada" : System.out.println("-> A encomenda com o id = " + resultado[1] + " foi cancelada."); break; // Mensagem
                                case "entrarJaExisteConexao" : System.out.println("-> Já existe uma conexão com esse utilizador."); break; // Erro
                                case "entrarDadosIncorretos" : System.out.println("-> Dados incorretos."); break; // Erro
                                case "servidorNaoExiste" : System.out.println("-> Tipo de servidor não existe."); break; // Erro
                                case "standardSucesso" : System.out.println("-> Reserva standard do tipo " + resultado[1] + " iniciada com sucesso.\n" +
                                                                            "   Id da reserva: " + resultado[2]); break; // Sucesso
                                case "standardPedido" : System.out.println("-> Pedido de reserva standard do tipo " + resultado[1] + " criado."); break; // Mensagem
                                case "leilaoSucesso" : System.out.println("-> Reserva de leilão do tipo " + resultado[1] + " com licitação " + (Integer.valueOf(resultado[2]) / 100.0f) +"€ iniciada com sucesso.\n" +
                                                                          "   Id da reserva: " + resultado[3]); break; // Sucesso
                                case "leilaoPedido" : System.out.println("-> Pedido de reserva de leilão do tipo " + resultado[1] + " com licitação " + (Integer.valueOf(resultado[2]) / 100.0f) + "€ criado."); break; // Mensagem
                                case "leilaoValorInvalido" : System.out.println("-> O valor da licitação deve ser um número positivo e diferente de 0."); break; // Erro
                                case "leilaoValorNaoNumerico" : System.out.println("-> O valor da licitação deve ser um número (em cêntimos)."); break; //Erro
                                case "divida" : System.out.println("-> Dívida total: " + (Integer.valueOf(resultado[1]) / 100.0f) + "€."); break; // Resultado
                                case "cancelarSucesso" : System.out.println("-> Reserva com id = " + resultado[1] + " cancelada."); break; // Sucesso
                                case "reservaNaoExiste" : System.out.println("-> Reserva não existe."); break; // Erro
                                case "erro" : System.err.println(str.substring(5)); break; // Erro
                                default: System.out.println(str);
                            }
                        }
                        if(!s.isClosed()) {
                            s.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread write = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        PrintWriter wr = new PrintWriter(s.getOutputStream());
                        Scanner scanner = new Scanner(System.in);

                        while (true) {
                            String input = scanner.nextLine();
                            if (input == null || input.equals("sair")) {
                                System.out.println("Adeus");
                                break;
                            }
                            if(input.equals("ajuda")) {
                                    System.out.println("registar <e-mail> <password>");
                                    System.out.println("entrar <e-mail> <password>");
                                    System.out.println("standard <tipo>");
                                    System.out.println("leilao <tipo> <valor>");
                                    System.out.println("divida");
                                    System.out.println("cancelar <id pedido>");
                                    System.out.println("sair");
                            } else {
                                wr.println(input);
                                wr.flush();
                            }
                        }
                        scanner.close();
                        if(!s.isClosed()) {
                            s.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            read.start();
            write.start();
            try {
                System.out.println("Juntar");
                write.join();
                System.out.println("Escrever");
                read.join();
                System.out.println("Ler");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Não foi possível estabelecer uma ligação com o servidor");
        }
    }
}
