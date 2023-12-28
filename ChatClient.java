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

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

        
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

        try{
            this.socket = new Socket(server, port);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch ( IOException e) {
            closeContact(socket, bufferedReader, bufferedWriter);
        }

    }


    // Método invocado sempre que o utilizador insere uma mensagem
    // na caixa de entrada
    public void newMessage(String message) {
        try {
            printMessage(message + "\n");
            message = evaluateMessage(message);
            bufferedWriter.write( message );
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch ( IOException e ) {
            closeContact(socket, bufferedReader, bufferedWriter);
        }
    }


    private String evaluateMessage(String message){
        String[] arrOfMsg = message.split(" ", 2);
    
        if ("/nick".equals(arrOfMsg[0])) {
            return message;
        } else if ("/join".equals(arrOfMsg[0])) {
            return message;
        } else if ("/leave".equals(arrOfMsg[0])) {
            return message;
        } else if ("/bye".equals(arrOfMsg[0])) {
            return message;
        } else if ("/priv".equals(arrOfMsg[0])) {
            return message;
        } else if (arrOfMsg[0].charAt(0) == '/') {
            return "/" + message;
        }
        return message;
    }

        
    // Método principal do objecto
    public void run() {
        try{
            String messageFromServer;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Cria um ciclo infinito que recebe mensagens do servidor
            while (true) {
                messageFromServer = in.readLine();
                printMessage(messageFromServer + "\n");
            }
        } catch (IOException ie){
            System.err.println(ie);
        }
            
    }
    
    public void closeContact(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
        

    // Instancia o ChatClient e arranca-o invocando o seu método run()
    // * NÃO MODIFICAR *
    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0], Integer.parseInt(args[1]));
        client.run();
    }
}