import Commands.*;
import Controller.Controller;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.attribute.FileTime.fromMillis;

public class SecureHolderClientHandler extends SimpleChannelInboundHandler<Message> {
    private final Controller controller;

    public SecureHolderClientHandler(Controller controller){
        this.controller=controller;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        //System.out.printf("DEBUG:"+msg);
        //System.out.println(msg.getClass().getName());
        if (msg instanceof SetFile & controller.getGetFile()){
            SetFile msgIn = (SetFile) msg;
            Path path = Paths.get(controller.getFm().getTmpPath().toString() + "//" + controller.getPath().toFile().getName());

            try(RandomAccessFile accessFile = new RandomAccessFile(path.toFile(),"rw")) {
                accessFile.seek(msgIn.getPosition());
                accessFile.write(msgIn.getData());
                if ((msgIn.getPosition()+msgIn.getData().length)>= controller.getGetSize()) {
                    controller.setGetFile(false);
                    System.out.println("Данные сохранены в файле "+path);
                    //System.out.print(controller.getFm().getTmpPath()+">");
                }
            } catch ( IOException e) {
                controller.setGetFile(false);
                path.toFile().delete();
            }


        }
        if (msg instanceof Answer ){

            Answer msgIn = (Answer) msg;
            if ( msgIn.getTypeMessage().equals("Autorization")){
                controller.setAuthorization(msgIn.isRezl());
                controller.setTxt(msgIn.getText());
                controller.setFinishAuthorisation(true);
            }
            if ( msgIn.getTypeMessage().equals("setFile")){
                if (!controller.getStartSetFile().get()){
                    controller.setStartSetFile( new AtomicBoolean(msgIn.isRezl()));
                    controller.setTxt(msgIn.getText());
                    if (!msgIn.isRezl()){
                        controller.setStatusSetFile( new AtomicBoolean(false));
                        controller.setFinchSetFile(true);
                    }

                } else {
                    controller.setTxt(msgIn.getText());
                    controller.setStatusSetFile(new AtomicBoolean(msgIn.isRezl()));
                    controller.setFinchSetFile(true);
                }
            }
            if (msgIn.getTypeMessage().equals("setInfo")){
                printData(msgIn);
            }
            if (msgIn.getTypeMessage().equals("GetFile")){
                controller.setGetFile(msgIn.isRezl());
                controller.setTxt(msgIn.getText());
            }
        }
    }
    public void printData(Answer msg){

        if (msg.getList()!=null & msg.getList().size()!=0) {
            ArrayList<Info> list = msg.getList();
            controller.setList(list);
            int i=0;
            String name;

            for (Info info : list) {
                i++;
                System.out.printf("id:%d; file: %s; last modification: %s; size(MB): %d.%03d %n",i,info.getPath(),fromMillis(Long.parseLong(info.getLastModifyFile())), info.getSize() / (1024L * 1024L), (info.getSize() % (1024L * 1024L))  / 1024L);
            }
        } else {
            controller.setList(null);
            System.out.println("У Вас нет сохранённых данных в облачном хранилище");
            System.out.print(controller.getFm().getTmpPath()+">");
        }
        controller.setStatusInfo(true);

    }

}
