import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
// import java.nio.ByteBuffer;
// import java.nio.charset.Charset;
// import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

public class ClientData implements Runnable {
    public static ArrayList<ClientData> clients = new ArrayList<>();
    private String name, state, room;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    // static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );
    // static private final Charset charset = Charset.forName("UTF8");
    // static private final CharsetDecoder decoder = charset.newDecoder();

    ClientData(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.state = "init";
            this.name = null;
            this.room = null;
            clients.add(this);
        } catch ( IOException e ){
            closeContact(socket, bufferedReader, bufferedWriter);
        }
    }

    public String getName(){ return name; }
    public String getState(){ return state; }
    public String getRoom(){ return room; }

    public void setName(String newName){ name = newName; }
    public void setState(String newState){ state = newState; }
    public void setRoom(String newRoom){ room = newRoom; }

    @Override
    public void run(){
        String messageFromClient;
        while (!socket.isClosed()) {
            try{
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    System.out.println("Message from client " + name + ": " + messageFromClient);
                    event(messageFromClient);
                }
            } catch ( IOException e ) {
                if("inside".equals(state)){
                    sendmessage("LEFT " + name, room);
                }
                clients.remove(this);
                closeContact(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // mensagem de retorno para o utilizador
    public void sendmessage(String message){
        try{
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch ( IOException e ) {
            closeContact(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendmessage(String message, String room){
        for(ClientData client : clients){
            try{
                if(client.name != null && client.room != null){
                    if(!client.name.equals(name) && client.room.equals(room)){
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                    }
                }
            } catch ( IOException e ) {
                closeContact(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void sendprivmessage(String message, String dest){
        for(ClientData client : clients){
            try{
                if(client.name != null){
                    if(client.name.equals(dest)){
                        client.bufferedWriter.write(message);
                        client.bufferedWriter.newLine();
                        client.bufferedWriter.flush();
                        break;
                    }
                }
            } catch ( IOException e ) {
                closeContact(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void event(String message){
        String command, resto;
        String dest = "";
        String[] arrOfMsg = message.split(" ", 2);
    
        if ("/nick".equals(arrOfMsg[0])) {
            command = "/nick";
            resto = arrOfMsg[1];
        } else if ("/join".equals(arrOfMsg[0])) {
            command = "/join";
            resto = arrOfMsg[1];
        } else if ("/leave".equals(arrOfMsg[0])) {
            command = "/leave";
            resto = "";
        } else if ("/bye".equals(arrOfMsg[0])) {
            command = "/bye";
            resto = "";
        } else if ("/priv".equals(arrOfMsg[0])) {
            String[] privMsg = arrOfMsg[1].split(" ", 2);
            dest = privMsg[0];
            resto = privMsg[1];
            command = "/priv";
        } else if (arrOfMsg[0].charAt(0) == '/' && arrOfMsg[0].charAt(1) == '/') {
            command = "message";
            resto = message.substring(1);
        } else {
            command = "message";
            resto = message;
        }

        if ("init".equals(state) && "/nick".equals(command) && containsByName(resto)){
            sendmessage("ERROR");
            return;
        }

        if ("init".equals(state) && "/nick".equals(command)){
            System.out.println("new username: " + resto);
            state = "outside";
            name = resto;
            sendmessage("OK");
            return;
        }

        if ("outside".equals(state) && "/join".equals(command)){
            System.out.println("username " + name + " entered in room " + resto);
            room = resto;
            state = "inside";
            sendmessage("OK");
            sendmessage("JOINED " + name, room);
            return;
        }

        if ("outside".equals(state) && "/nick".equals(command) && containsByName(resto)){
            sendmessage("ERROR");
            return;
        }

        if ("outside".equals(state) && "/nick".equals(command)){
            System.out.println("the user " + name + " changed name to: " + resto);
            name = resto;
            sendmessage("OK");
            return;
        }

        if ("inside".equals(state) && "message".equals(command)){
            sendmessage("MESSAGE " + name + " " + resto, room);
            return; 
        }

        if ("inside".equals(state) && "/nick".equals(command) && containsByName(resto)){
            sendmessage("ERROR");
            return;
        }
        
        if ("inside".equals(state) && "/nick".equals(command)){
            System.out.println("the user " + name + " changed name to: " + resto);
            sendmessage("OK");
            sendmessage("NEWNICK " + name + " " + resto, room);
            name = resto;
            return;
        }

        if ("inside".equals(state) && "/join".equals(command)){
            System.out.println("the user " + name + " entered in room: " + room);
            sendmessage("OK");
            sendmessage("LEFT " + name, room);
            room = resto;
            sendmessage("JOINED " + name, room);
            return;
        }

        if ("inside".equals(state) && "/leave".equals(command)){
            System.out.println("the user " + name + " leaved room: " + room);
            sendmessage("OK");
            sendmessage("LEFT " + name, room);
            room = null;
            state = "outside"; 
            return;
        }

        if ("inside".equals(state) && "/bye".equals(command)){
            System.out.println("the user " + name + " leaved room: " + room);
            System.out.println("the user " + name + " quited");
            sendmessage("BYE");
            sendmessage("LEFT " + name, room);
            clients.remove(this);
            closeContact(socket, bufferedReader, bufferedWriter);
            return;
        }

        if (!"init".equals(state) && "/priv".equals(command) && containsByName(dest)){
            sendmessage("OK");
            sendprivmessage("PRIVATE " + name + " " + resto, dest);
            return;
        }

        if (!"init".equals(state) && "/priv".equals(command)){
            sendmessage("ERROR");
            return;
        }

        if (!"inside".equals(state) && "/bye".equals(command)){
            sendmessage("BYE");
            System.out.println("the user " + name + " quited");
            clients.remove(this);
            closeContact(socket, bufferedReader, bufferedWriter);
            return;
        }

        if (!"inside".equals(state) && "message".equals(command)){
            sendmessage("ERROR");
            return;
        }

        sendmessage("ERROR");
    }

    public void removeClient(){
        clients.remove(this);
    }

    public void closeContact(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClient();
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

    public static ClientData getClientByName(String name) {
        for (ClientData client : clients) {
            if (client.getName() != null && client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    public static boolean containsByName(String name) {
        for (ClientData client : clients) {
            if (client.getName() != null && client.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
