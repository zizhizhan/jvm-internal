package me.jameszhan.io.net.select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 16/3/10
 *         Time: AM12:35
 */
public class UdpEchoClient extends Thread{

    public void run(){
        this.setName("client");
        try{
            DatagramChannel client = DatagramChannel.open();
            client.configureBlocking(false);
            client.connect(new InetSocketAddress("localhost", 6666));

            Selector sel = Selector.open();
            client.register(sel, SelectionKey.OP_READ, new Object());
            client.write(Charset.defaultCharset().encode("Hello World"));

            ByteBuffer buf = ByteBuffer.allocate(1024);

            for(;;){
                int op = sel.select();
                switch(op){
                    case -1:
                        System.out.println("select error!");
                        break;

                    case 0:
                        System.out.print("select timeout");
                        break;

                    default:
                        Set<SelectionKey> keys = sel.selectedKeys();
                        Iterator<SelectionKey> itor = keys.iterator();
                        while(itor.hasNext()){
                            SelectionKey key = itor.next();
                            itor.remove();
                            if(key.isReadable()){
                                DatagramChannel channel = (DatagramChannel) key.channel();
                                SocketAddress addr = channel.receive(buf);
                                buf.flip();
                                CharBuffer text = Charset.defaultCharset().decode(buf);
                                String msg = text.toString();
                                System.out.println("Receive msg: " + msg + " from " + addr.toString());
                                buf.clear();

                                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                                String line = br.readLine();
                                ByteBuffer bb = Charset.defaultCharset().encode(line);
                                channel.write(bb);
                            }
                            //if(key.isWritable()){
                            //System.out.println("is writable");
                            //}
                        }
                        break;
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UdpEchoClient().start();
    }

}