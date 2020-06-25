/**
 *
 *  @author Karpenko Oleksandr S16934
 *
 */

package zad1;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class cSession {

    private String nickname;

    private SocketChannel socketChannel;

    private SelectionKey selectionKey;

    private ByteBuffer buf = ByteBuffer.allocate(BUFF_SIZE);

    private static  final  int BUFF_SIZE = 256;

    public cSession(String nickname,SelectionKey selectionKey, SocketChannel socketChannel){

        this.socketChannel = socketChannel;

        this.selectionKey = selectionKey;

        this.nickname = nickname;

    }

    public String read(){

        StringBuilder str = new StringBuilder();

        try{
            buf.clear();
        int read = 0;
        while( (read = socketChannel.read(buf)) > 0 ) {
            buf.flip();
            byte[] bytes = new byte[buf.limit()];
            buf.get(bytes);
            str.append(new String(bytes));
            buf.clear();
        }

        }catch (Exception ex){ex.printStackTrace();}

        return str.toString();
    }

    public void write(String message){

        ByteBuffer buf = ByteBuffer.wrap(message.getBytes());

        try {

            socketChannel.write(buf);


        }catch (Exception ex){ex.printStackTrace();}
    }


    public void disconnect() {

        try{

            if (selectionKey != null) selectionKey.cancel();

            if (socketChannel == null) return;

            System.out.println("Client " + socketChannel.getRemoteAddress() + " disconnected");

            socketChannel.close();

        }catch (Exception ex){ex.printStackTrace();}

    }

    public String getNickname() {
        return nickname;
    }
}
