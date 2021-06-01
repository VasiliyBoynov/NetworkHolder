import ClientStatus.ClientStatus;
import Commands.*;
import Constant.Constant;
import DB.DB;
import DB.MessageDB;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;


import static java.nio.file.Files.setLastModifiedTime;
import static java.nio.file.attribute.FileTime.fromMillis;

/**
 * Handles a server-side channel.
 */
public class SecureHolderServerHandler extends SimpleChannelInboundHandler<Message> {

    private ClientStatus client;
    final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("SendDataToClient")
            .setDaemon(true)
            .build();
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    public SecureHolderServerHandler(ClientStatus client){
        this.client = client;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
        executor.shutdownNow();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        //System.out.printf("DEBUG:%s%n",msg);
        //System.out.println(msg.getClass().getName());
        if (msg instanceof NewUser | msg instanceof User){
            autorization(ctx,msg);
        }
        if (msg instanceof SetFile){
            setFile(ctx,(SetFile) msg);
        }
        if (msg instanceof FileInfo){
            setInfo(ctx);
        }
        if (msg instanceof GetFile){
            GetFile msgIn = (GetFile) msg;
            getFile(msgIn,ctx);
        }
        if (msg instanceof DeleteFile){
            DeleteFile msgIn = (DeleteFile) msg;
            delteFile(msgIn);
        }
    }

    private void delteFile(DeleteFile msgIn){
        DB db = new DB();
        MessageDB messageDB = db.updateStatusDel(client.getIdUser(),msgIn.getPath());
        if (messageDB.isRezl()){
            Paths.get(messageDB.getTxt()).toFile().delete();
        }
        db.updateSizeArray(client.getNickName());
    }
    private void getFile(GetFile msgIn, ChannelHandlerContext ctx){
        DB db = new DB();
        MessageDB messageDB = db.getFile(client.getIdUser(),msgIn.getPath());
        Answer answerGetFile = new Answer();
        answerGetFile.setTypeMessage("GetFile");
        answerGetFile.setRezl(messageDB.isRezl());
        if (messageDB.isRezl()) {
        answerGetFile.setText("start getFile");
        Path pathIn = Paths.get(messageDB.getTxt());


            executor.submit(()->{
                System.out.printf("Start send File : %s to clientId: %s%n",pathIn,client.getIdUser());
                try (RandomAccessFile accessFile = new RandomAccessFile(pathIn.toFile(), "r")){
                    ctx.writeAndFlush(answerGetFile);
                    long length = pathIn.toFile().length();
                    long position = accessFile.getFilePointer();
                    long available = length - position;
                    while (available>0){
                        SetFile setFile = new SetFile();
                        byte[] buffer;
                        if (available > Constant.MAX_PACKAGE_BYTE.getConstant()) {
                            buffer = new byte[Constant.MAX_PACKAGE_BYTE.getConstant()];
                        } else {
                            buffer = new byte[(int) available];
                        }
                        accessFile.read(buffer);
                        setFile.setData(buffer);
                        setFile.setPosition(position);
                        ctx.writeAndFlush(setFile).sync();
                        position =  accessFile.getFilePointer();
                        available = length - position;
                    }

                } catch (IOException  e) {
                    answerGetFile.setRezl(false);
                    answerGetFile.setText("Dont find the File");
                    ctx.writeAndFlush(answerGetFile);
                } catch (InterruptedException e) {
                    System.out.printf("Client userId: %s disconnected%n",client.getIdUser());

                }
            });
        } else {
            answerGetFile.setText(messageDB.getTxt());
            ctx.writeAndFlush(answerGetFile);
        }


    }
    private void setInfo(ChannelHandlerContext ctx){
        DB db = new DB();
        MessageDB messageDB = db.listFile(client.getIdUser());
        Answer answerInfo = new Answer();
        answerInfo.setTypeMessage("setInfo");
        answerInfo.setRezl(messageDB.isRezl());
        answerInfo.setText(messageDB.getTxt());
        answerInfo.setList(messageDB.getList());
        ctx.writeAndFlush(answerInfo);
    }

    private void setFile(ChannelHandlerContext ctx,SetFile msg){
        //System.out.printf("DEBUG: StartSetFile ->%s %n",client.getStartSetFile().get());
        if (client.getStartSetFile().get()){
            executor.submit(()->{
                try(RandomAccessFile accessFile = new RandomAccessFile(Paths.get(client.getNameFileToServer()).toFile(), "rw")){
                    accessFile.seek(msg.getPosition());
                    accessFile.write(msg.getData());
                    if ((msg.getPosition()+msg.getData().length)>= client.getSetFileMetaData().getSizeFile()) {
                        //System.out.printf("DEBUG: %s %s : %s%n",Paths.get(client.getNameFileToServer()),"fileTime",fromMillis(client.getSetFileMetaData().getLastModified()));
                        setLastModifiedTime(Paths.get(client.getNameFileToServer()),fromMillis(client.getSetFileMetaData().getLastModified()));
                        DB db = new DB();
                        Answer answerSetFile = new Answer();
                        answerSetFile.setTypeMessage("setFile");
                        answerSetFile.setRezl(true);
                        answerSetFile.setText("File saved for Server");
                        client.setStartSetFile(new AtomicBoolean(false));
                        db.updateStatus(client.getIdUser(),client.getNameFileToServer(),true,false);
                        db.updateSizeArray(client.getNickName());
                        ctx.writeAndFlush(answerSetFile);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            DB db = new DB();
            MessageDB messageDB = db.setFile(client.getNickName(),msg);
            if (messageDB.isRezl()){
                client.setStartSetFile(new AtomicBoolean(true));
                client.setNameFileToServer(messageDB.getTxt());
                client.setSetFileMetaData(msg);
                Answer answerSetFile = new Answer();
                answerSetFile.setTypeMessage("setFile");
                answerSetFile.setRezl(true);
                answerSetFile.setText("Server is ready");
                ctx.writeAndFlush(answerSetFile);

            } else {
                Answer answerSetFile = new Answer();
                answerSetFile .setTypeMessage("setFile");
                answerSetFile .setRezl(false);
                answerSetFile .setText(messageDB.getTxt());
                client.setStartSetFile(new AtomicBoolean(false));
                ctx.writeAndFlush(answerSetFile);
            }
        }
    }
    private void autorization(ChannelHandlerContext ctx, Message msg){
        DB db = new DB();
        client.setAutorization(false);
        client.setNickName(((NewUser) msg).getNickName());
        //System.out.printf("DEBUG: %s%n","Start authorization");
        MessageDB messageDB =(msg instanceof User) ?
                db.checkUser(((NewUser) msg).getNickName(),((NewUser) msg).getPassword()) :
                db.addUser(((NewUser) msg).getNickName(),((NewUser) msg).getPassword());
        client.setAutorization(messageDB.isRezl());

        Answer answerAutorization = new Answer();
        answerAutorization.setTypeMessage("Autorization");
        answerAutorization.setRezl(messageDB.isRezl());
        answerAutorization.setText(messageDB.getTxt());
        //System.out.printf("DEBUG: %s %s%n","Finish authorization",answerAutorization.toString());
        ctx.writeAndFlush(answerAutorization);
        if (client.getAutorization()) {
            messageDB=db.infoUser(client.getNickName());
            if (messageDB.isRezl()) client.setIdUser(Integer.parseInt(messageDB.getTxt()));
        }
    }
}