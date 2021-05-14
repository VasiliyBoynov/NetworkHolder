import ClientStatus.ClientStatus;
import Commands.Answer;
import Commands.Message;
import Commands.NewUser;
import Commands.User;
import DB.DB;
import DB.MessageDB;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.EventExecutorGroup;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles a server-side channel.
 */
public class SecureHolderServerHandler extends SimpleChannelInboundHandler<Message> {

    private ClientStatus client;

    public SecureHolderServerHandler(ClientStatus client){
        this.client = client;
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        Answer answerHello = new Answer();
        answerHello.setText("Test JSON, Hello!");
        ctx.writeAndFlush(answerHello);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.printf("DEBUG:%s%n",msg);
        System.out.println(msg.getClass().getName());
        if (msg instanceof NewUser | msg instanceof User){
            autorization(ctx,msg);
        }
    }
    private void autorization(ChannelHandlerContext ctx, Message msg){
        DB db = new DB();
        client.setAutorization(false);
        client.setNickName(((NewUser) msg).getNickName());
        System.out.printf("DEBUG: %s%n","Start authorization");
        MessageDB messageDB =(msg instanceof User) ?
                db.checkUser(((NewUser) msg).getNickName(),((NewUser) msg).getPassword()) :
                db.addUser(((NewUser) msg).getNickName(),((NewUser) msg).getPassword());
        client.setAutorization(messageDB.isRezl());
        Answer answerAutorization = new Answer();
        answerAutorization.setTypeMessage("Autorization");
        answerAutorization.setRezl(messageDB.isRezl());
        answerAutorization.setText(messageDB.getTxt());
        System.out.printf("DEBUG: %s %s%n","Finish authorization",answerAutorization.toString());
        ctx.writeAndFlush(answerAutorization);
    }
}