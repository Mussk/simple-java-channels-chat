/**
 *
 *  @author Karpenko Oleksandr S16934
 *
 */

package zad1;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

  private static HashMap<SelectionKey,cSession> clients_read = new HashMap<>();

  String message = "";

   static InetSocketAddress server_my_adress;

  private ServerSocketChannel server_socket;

  private Selector selector;

  private final static int SERVER_PORT = 51900;

  private final static int BUFF_SIZE = 256;

  public Server() {

    try {


      this.server_my_adress = new InetSocketAddress(InetAddress.getLocalHost(), SERVER_PORT);
      this.server_socket = ServerSocketChannel.open();
      this.selector = Selector.open();

    }catch (Exception ex){ex.printStackTrace();}

  }

  public void loop() throws Exception{

    selector.selectNow();

    for (SelectionKey key:
         selector.keys()) {
      if(!key.isValid())continue;

      if (key.isAcceptable()){

        SocketChannel socketChannel =  server_socket.accept();

        if (socketChannel == null) continue;

        socketChannel.configureBlocking(false);

        SelectionKey client_key_read = socketChannel.register(selector,SelectionKey.OP_READ);

        System.out.println("Connection from " + socketChannel.getRemoteAddress()
                + "\nWaiting for client information...");

        ByteBuffer buf = ByteBuffer.allocateDirect(BUFF_SIZE);

        socketChannel.read(buf);

        buf.flip();

        CharBuffer cb = Charset.forName("UTF-8").decode(buf);

        buf.clear();

        String nickname = cb.toString();

        clients_read.put(client_key_read,new cSession(nickname,client_key_read,socketChannel));

        System.out.println("Nickname: " + nickname);


      }
      if(key.isReadable()) {

        System.out.println("Reading");

        message = clients_read.get(key).read();

        System.out.println("server maessage: " + message);

        for (Map.Entry<SelectionKey,cSession> entry:
                clients_read.entrySet())
          entry.getValue().write(message);


        message = null;
      }

    }

   selector.selectedKeys().clear();
  }


  public static void main(String[] args) {

    Server server = new Server();

    System.out.println("Starting server on "
                    + server.server_my_adress.getAddress().getHostAddress()
                    + " : " + server.server_my_adress.getPort());



    try {

      server.server_socket.configureBlocking(false);
      server.server_socket.register(server.selector, SelectionKey.OP_ACCEPT);
      server.server_socket.bind(server.server_my_adress);

      Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->{

        try {

          server.loop();

        }catch (Exception ex){ex.printStackTrace();}

      },0,500, TimeUnit.MILLISECONDS);



    }catch (Exception ex){ex.printStackTrace();}



  }
}
