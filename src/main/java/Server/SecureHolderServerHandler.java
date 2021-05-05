package Server;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles a server-side channel.
 */
public class SecureHolderServerHandler extends SimpleChannelInboundHandler<byte[]> {

    private ClientStatus client;

    public SecureHolderServerHandler(ClientStatus client){
        this.client = client;
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.writeAndFlush((byte[])
                new String("Welcome to secure NetworkHolder service!\n").getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush((byte[])
                new String ("Please start procedure autorization use comand \"user\" or \"newUser\"").getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        byte[] in = msg;
        //System.out.println("DEBUG" + new String(in));

        if (msgToObj(in) instanceof ArrayList){
            System.out.println("DEBUG");
            System.out.println(ClientStatus.isComandUser((ArrayList<String>) msgToObj(in)));
            for (String s : (ArrayList<String>) msgToObj(in)) {
                System.out.println(s);

            }
            String cmd = (ClientStatus.isComandUser((ArrayList<String>) msgToObj(in))) ? "\"Instruction\": \"user\"":"\"Instruction\": \"newUser\"";
            String str = String.format("1{%n" +
                            "%s,%n" +
                            "%s" +
                            "%n}",
                    cmd,
                    "\"rezl\": \"true\"");
            System.out.println(str);
            ctx.writeAndFlush(str.getBytes(StandardCharsets.UTF_8));

        }






        // Close the connection if the client has sent 'exit'.
        if ("exit".equals(msg.toString().toLowerCase())) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private Object msgToObj(byte[] msg){
        if (msg[0] == (byte) 49) {
            List<String> list = new ArrayList<String>();
            String str = new String(msg, 1, msg.length - 1);
            for (int i = 1; i < str.split("\n").length - 1; i++) {
                list.add(str.split("\n")[i]);
            }
            return list;
        }

        if (msg[0] == (byte) 48){}

        return null;

    };

/*
    private void doListCommand(byte[] msg) {




        if (msg[0] == (byte) 49) {
            List<String> list = new ArrayList<>();

            String str = new String(msg, 1, msg.length - 1);
            for (int i = 1; i < str.split("\n").length - 1; i++) {
                list.add(str.split("\n")[i]);
            }
            listCommandIn.add(list);
        }
    }
    private void autorization(ChannelHandlerContext ctx){

        if (!doAutorization & listCommandIn.size()>0){
            List<String> list = new ArrayList<>();
            for (int i = 0; i < listCommandIn.size(); i++) {
                list.addAll(listCommandIn.get(0));


                if (list.get(0).indexOf("newUser")!=-1 | list.get(0).indexOf("user")!=-1){
                    doAutorization=true;


                    String cmd = (list.get(0).indexOf("newUser")!=-1) ? "\"Instruction\": \"newUser\"":"\"Instruction\": \"user\"";
                    System.out.println("DEBUG data _________");
                    System.out.println(cmd);
                    String str = String.format("{%n" +
                                    "%s,%n" +
                                    "%s" +
                                    "%n}",
                            cmd,
                            "\"rezl\": \"successful\"");
                    System.out.println("DEBUG data" + str);
                    ctx.writeAndFlush(str.getBytes(StandardCharsets.UTF_8));
                    System.out.println("DEBUG data" + str);
                    listCommandIn.remove(0);
                    break;
                } else {
                    listCommandIn.remove(0);
                }
            }


        }
    }
    */


}
