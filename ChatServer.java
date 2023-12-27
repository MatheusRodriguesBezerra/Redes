import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class ChatServer{
    static private final ByteBuffer buffer = ByteBuffer.allocate( 16384 );

    static private final Charset charset = Charset.forName("UTF8");
    static private final CharsetDecoder decoder = charset.newDecoder();
    private static final Data data = new Data();

    static public void main( String args[] ) throws Exception {
        int port = Integer.parseInt( args[0] );
        
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();

            ssc.configureBlocking( false );

            ServerSocket ss = ssc.socket();
            InetSocketAddress isa = new InetSocketAddress( port );
            ss.bind( isa );

            Selector selector = Selector.open();

            ssc.register( selector, SelectionKey.OP_ACCEPT );
            System.out.println( "Listening on port "+port );

            while (true) {
                int num = selector.select();

                if (num == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();

                    if (key.isAcceptable()) {

                        Socket s = ss.accept();
                        System.out.println( "Got connection from "+s );

                        SocketChannel sc = s.getChannel();
                        sc.configureBlocking( false );

                        sc.register( selector, SelectionKey.OP_READ );

                    } else if (key.isReadable()) {

                        SocketChannel sc = null;

                        try {

                            sc = (SocketChannel)key.channel();
                            boolean ok = processInput( sc );
                            

                            if (!ok) {
                                key.cancel();

                                Socket s = null;
                                try {
                                    s = sc.socket();
                                    System.out.println( "Closing connection to "+s );

                                    s.close();
                                } catch( IOException ie ) {
                                    System.err.println( "Error closing socket "+s+": "+ie );
                                }
                            }

                        } catch( IOException ie ) {

                            key.cancel();

                            try {
                                sc.close();
                            } catch( IOException ie2 ) { 
                                System.out.println( ie2 ); 
                            }

                            System.out.println( "Closed "+sc );
                        }
                    }
                }

                keys.clear();
            }
        } catch( IOException ie ) {
            System.err.println( ie );
        }
    }

    static private boolean processInput( SocketChannel sc ) throws IOException {
        buffer.clear();
        sc.read( buffer );
        buffer.flip();

        String clientAddress = sc.socket().getInetAddress().getHostAddress();
        int clientPort = sc.socket().getPort();
        System.out.println(clientAddress);
        System.out.println(clientPort);

        if (buffer.limit()==0) {
            return false;
        }

        String message = decoder.decode(buffer).toString();
        data.event(message, clientAddress, clientPort);
        // System.out.println(data.event(message, clientAddress, clientPort));

        return true;
    }
}