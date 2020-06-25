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
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Client {

    private String nickname;

    private SocketChannel socketChannel;

    private InetSocketAddress client_my_adress;

    private static  final  int BUFF_SIZE = 256;

   // private static  final  InetSocketAddress server_adres = new InetSocketAddress("localhost",51900);

    private Selector selector;

    private  GUI_Client gui_client;

    public Client(){

        try {

            gui_client = new GUI_Client();

            this.nickname = gui_client.loadLoginWindow();

        ByteBuffer buf = ByteBuffer.allocateDirect(BUFF_SIZE);

        this.client_my_adress = new InetSocketAddress(InetAddress.getLocalHost(), 50000 + (int)(Math.random() * 10001));

            System.out.println("Running client on " + client_my_adress.toString());

            socketChannel = SocketChannel.open();

            selector = Selector.open();

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.bind(client_my_adress);

            System.out.println("Starting client");

            socketChannel.connect(Server.server_my_adress);

            while(!socketChannel.finishConnect()) {

                continue;
            }

            System.out.println("Connected! Sending nickname...");

            socketChannel.write(ByteBuffer.wrap(nickname.getBytes()));

            System.out.println("Sent");

            gui_client.loadChatWindow();

            socketChannel.register(selector,SelectionKey.OP_WRITE);


        } catch (Exception ex){ex.printStackTrace();}

    }

    public void loop() throws Exception{

        selector.selectNow();

        try {
            for (SelectionKey key :
                    selector.keys()) {


                if (key.isWritable()){

                    System.out.println("Writing");

                    System.out.println("message to send: " + gui_client.getMessage());

                    write(ByteBuffer.wrap(gui_client.getMessage().getBytes()));

                    socketChannel.register(selector,SelectionKey.OP_READ);

                    gui_client.setMessage("");
                }

                if (key.isReadable()){

                    System.out.println("Reading");

                    String mes = read();

                    System.out.println("Message before posting: " + mes);

                    if (mes.equals("")){break;}

                    else gui_client.getTextArea().append("\n" + mes);

                    socketChannel.register(selector,SelectionKey.OP_WRITE);

                }



            }

        }catch (Exception ex){ex.printStackTrace();}

        selector.selectedKeys().clear();

    }

    public static void main(String[] args) {

        try {

            Client client = new Client();

            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() ->{

                try {

                   client.loop();

                }catch (Exception ex){ex.printStackTrace();}

            },0,500, TimeUnit.MILLISECONDS);



        }catch (Exception ex){ex.printStackTrace();}
    }

    public String read(){

        CharBuffer charBuffer = null;

        String recieved_message = null;

            try{

                int bytesRead = -1;

                ByteBuffer buf = ByteBuffer.allocateDirect(BUFF_SIZE);

                bytesRead = socketChannel.read(buf);

                buf.flip();

              //  buf.rewind();

                CharBuffer cb = Charset.forName("UTF-8").decode(buf);

             recieved_message = cb.toString();

             buf.clear();

            }catch (Exception ex){ex.printStackTrace();}

        System.out.println("REs mes: " + recieved_message);

            return recieved_message;
    }

    public void write(ByteBuffer buf){

        try {

            socketChannel.write(buf);

          //  buf.flip();

          //  buf.rewind();

            buf.clear();

        }catch (Exception ex){ex.printStackTrace();}
    }



}
