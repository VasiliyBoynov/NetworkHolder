import ClientStatus.ClientStatus;
import Commands.*;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.Files.setLastModifiedTime;
import static java.nio.file.attribute.FileTime.fromMillis;

/**
 * Handles a server-side channel.
 */
public class SecureHolderServerHandler extends SimpleChannelInboundHandler<Message> {

    private ClientStatus client;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public SecureHolderServerHandler(ClientStatus client){
        this.client = client;
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        //Answer answerHello = new Answer();
        //answerHello.setText("Test JSON, Hello!");
        //ctx.writeAndFlush(answerHello);

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
        if (msg instanceof SetFile){
            SetFile setFile =(SetFile) msg;
            setFile(ctx,setFile);
        }
    }
    private void setFile(ChannelHandlerContext ctx,SetFile msg){
        DB db = new DB();
        MessageDB messageDB = db.setFile(client.getNickName(),msg);
        if (messageDB.isRezl()) {
            executor.submit(()->{
                try(RandomAccessFile accessFile = new RandomAccessFile(Paths.get(messageDB.getTxt()).toFile(), "rw")){
                accessFile.seek(msg.getPosition());
                accessFile.write(msg.getData());
                if ((msg.getPosition()+msg.getData().length)>= msg.getSizeFile()) {
                    System.out.printf("DEBUG: %s %s : %s%n",Paths.get(messageDB.getTxt()),"fileTime",fromMillis(msg.getLastModified()));
                    setLastModifiedTime(Paths.get(messageDB.getTxt()),fromMillis(msg.getLastModified()));
                    Answer answerSetFile = new Answer();
                    answerSetFile .setTypeMessage("setFile");
                    answerSetFile .setRezl(true);
                    answerSetFile .setText("File saved for Server");
                    ctx.writeAndFlush(answerSetFile);

                }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

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