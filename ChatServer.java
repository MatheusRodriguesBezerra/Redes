import java.io.*;
import java.net.*;
// import java.nio.*;
// import java.nio.charset.*;

public class ChatServer{
    private ServerSocket serverSocket;

    ChatServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            System.out.println( "listening port 8000");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println( "Got connection from "+socket );
                ClientData clientData = new ClientData( socket );
                Thread thread = new Thread( clientData );
                
                thread.start();
            }
        } catch ( IOException e ) {

        }
    }

    public void closeServer() {
        try {
            if (serverSocket != null){
                serverSocket.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    static public void main( String args[] ) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java ChatServer <port>");
            return;
        }

        int port = Integer.parseInt( args[0] );

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ChatServer server = new ChatServer(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  
}