import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Data {
    private ListUsers users;

    Data(){
        users = new ListUsers();
    }

    public String event(String message, String server, int port) throws IOException{
        NodeUser user = getUserByServer(server, port);
    
        String command;
        String[] arrOfMsg = message.split(" ", 2);
    
        if ("/nick".equals(arrOfMsg[0])) {
            command = "/nick";
        } else if ("/join".equals(arrOfMsg[0])) {
            command = "/join";
        } else if ("/leave".equals(arrOfMsg[0])) {
            command = "/leave";
        } else if ("/bye".equals(arrOfMsg[0])) {
            command = "/bye";
        } else {
            command = message;
        }
    
        if ("init".equals(user.getState()) && "/nick".equals(command) && users.alreadyExists(message)){
            return "ERROR\n";
        }
    
        if ("init".equals(user.getState()) && "/nick".equals(command)){
            try {
                message("OK\n", user);
                user.setState("outside");
            } catch (IOException e) {
                // Trate a exceção aqui, se necessário
                e.printStackTrace();
            }
            return "OK\n";
        }
    
        if ("outside".equals(user.getState()) && "/join".equals(command)){
            // Enviar para os outros utilizadores da sala JOINED nome
            user.setRoom(message);
            user.setState("inside");
            return "OK\nJOINED " + user.getName() + "\n";
        }
    
        if ("outside".equals(user.getState()) && "/nick".equals(command) && users.alreadyExists(message)){
            return "ERROR\n";
        }
    
        if ("outside".equals(user.getState()) && "/nick".equals(command) && !users.alreadyExists(message)){
            user.setName(user.getName());
            return "OK\n";
        }
    
        if ("inside".equals(user.getState()) && "message".equals(command)){
            // Enviar para todos os utilizadores da sala MESSAGE nome mensagem
            return "MESSAGE " + user.getName() + " " + message + "\n"; 
        }
    
        if ("inside".equals(user.getState()) && "/nick".equals(command) && users.alreadyExists(message)){
            return "ERROR\n";
        }
    
        if ("inside".equals(user.getState()) && "/nick".equals(command) && !users.alreadyExists(message)){
            // Enviar para o utilizador OK
            // Enviar para os outros utilizadores da sala NEWNICK nome_antigo nome
            user.setName(message);
            return "NEWNICK " + user.getName() + " " + message + " \n";
        }
    
        if ("inside".equals(user.getState()) && "/join".equals(command)){
            // Enviar para o utilizador OK
            // Enviar para os usuários da sala atual LEFT nome
            user.setRoom(message);
            // Enviar para os usuários da nova sala JOINED nome
            user.setState("inside");
            return "OK\nJOINED " + user.getName() + "\n";
        }
    
        if ("inside".equals(user.getState()) && "/leave".equals(command)){
            // Enviar para o utilizador OK
            // Enviar para os outros utilizadores da sala LEFT nome
            user.setRoom(null);
            user.setState("outside"); 
            return "OK\nLEFT " + user.getName();
        }
    
        if ("inside".equals(user.getState()) && "/bye".equals(command)){
            // Enviar para o utilizador BYE
            // Enviar para os outros utilizadores da sala LEFT nome
            users.remove(user.getName());
            return "BYE\nLEFT " + user.getName();
        }
    
        if ("/priv".equals(command)){
            String[] msg = message.split(" ", 2);
            if (!users.contains(msg[0])){
                return "ERROR\n";
            }
    
            return "MESSAGE " + user.getName() + " " + msg[1] + "\n";
        }
    
        if ("inside".equals(user.getState())){
            // Enviar para os outros utilizadores da sala LEFT nome
            users.remove(user.getName());
        }
    
        if (!"inside".equals(user.getState()) && "/bye".equals(command)){
            // Enviar para o utilizador BYE
            users.remove(user.getName());
        }
    
        if (!"inside".equals(user.getState()) && "message".equals(command)){
            // ERROR
        }
    
        if (!"inside".equals(user.getState())){
            return "ERROR";
        }
    
        // ERROR
        return "";
    }
    

    public NodeUser getUserByServer(String server, int port){
        if(!users.contains(server, port)){
            NodeUser user = new NodeUser(server, port);
            users.add(user);
            return user;
        }

        NodeUser cur = users.getRoot();
        while (!cur.getServer().equals(server) && (cur.getPort() != port)) {
            cur =cur.getNext();
        }
        return cur;
    }

    public void message(String message, NodeUser user) throws IOException {
        // // Cria uma conexão TCP com o servidor
        // System.out.println(user.getServer());
        // System.out.println(user.getPort());
        Socket socket = new Socket("localhost", user.getPort());
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        socket.close();
    }
}
