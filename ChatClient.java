import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ChatClient {
    // Variáveis relacionadas com a interface gráfica --- * NÃO MODIFICAR *
    JFrame frame = new JFrame("Chat Client");
    private JTextField chatBox = new JTextField();
    private JTextArea chatArea = new JTextArea();
    // --- Fim das variáveis relacionadas coma interface gráfica

    // Se for necessário adicionar variáveis ao objecto ChatClient, devem
    // ser colocadas aqui

    String server;
    int port;

        
    // Método a usar para acrescentar uma string à caixa de texto
    // * NÃO MODIFICAR *
    public void printMessage(final String message) {
        chatArea.append(message);
    }

        
    // Construtor
    public ChatClient(String server, int port) throws IOException {

        // Inicialização da interface gráfica --- * NÃO MODIFICAR *
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatBox);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
        chatArea.setEditable(false);
        chatBox.setEditable(true);
        chatBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                newMessage(chatBox.getText());
                } catch (IOException ex) {
                } finally {
                    chatBox.setText("");
                }
                }
            });
            frame.addWindowListener(new WindowAdapter() {
                public void windowOpened(WindowEvent e) {
                chatBox.requestFocusInWindow();
                }
            });
            // --- Fim da inicialização da interface gráfica

            // Se for necessário adicionar código de inicialização ao
            // construtor, deve ser colocado aqui

            this.server = server;
            this.port = port;

    }


    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) throws IOException {
        // Cria uma conexão TCP com o servidor
        Socket socket = new Socket(server, port);
        InetAddress localAddress = InetAddress.getLocalHost();
        int localPort = socket.getLocalPort();
        System.out.println("Endereço IP local: " + localAddress.getHostAddress());
        System.out.println("Porta local: " + localPort);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        socket.close();
    }

        
    // Método principal do objecto
    public void run() throws IOException {
        try{
            // Cria uma conexão TCP com o servidor
            Socket socket = new Socket(server, port);

            // Cria um fluxo de entrada para receber mensagens do servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Cria um ciclo infinito que recebe mensagens do servidor
            while (true) {
                // Lê a próxima mensagem do servidor
                String message = in.readLine();

                // Imprime a mensagem na caixa de texto
                printMessage(message);
            }
        } catch (IOException ie){
            System.err.println(ie);
        }
            
    }
    
        

    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}