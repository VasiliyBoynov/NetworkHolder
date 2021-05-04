package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import io.netty.channel.Channel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller implements AutoCloseable{

    public static final Controller instance = new Controller();

    private AtomicBoolean authorization = new AtomicBoolean();
    private AtomicBoolean startAuthorisation = new AtomicBoolean();
    private AtomicBoolean finishAuthorisation = new AtomicBoolean();
    private static Channel ch;
    private  BufferedReader reader;


    public void setCh(Channel ch) {
        this.ch = ch;
    }

    public void setAuthorization(AtomicBoolean authorization) {
        this.authorization = authorization;
    }

    public void setStartAuthorisation(AtomicBoolean startAuthorisation) {
        this.startAuthorisation = startAuthorisation;
    }

    public void setFinishAuthorisation(AtomicBoolean finishAuthorisation) {
        this.finishAuthorisation = finishAuthorisation;
    }

    public void work() throws Exception {
        reader = new BufferedReader(new InputStreamReader(System.in));
        String commandLine;
        while (true) {
            commandLine = reader.readLine();

            if (commandLine.startsWith("newUser")){
                commandLine=null;
                //autorization(Commands.newUser);
            }

            if (commandLine.startsWith("user")){
                commandLine=null;
                //autorization(Commands.user);
            }




            //ch.writeAndFlush(commandLine.getBytes(StandardCharsets.UTF_8));
            if (commandLine.equals("exit")) {
                this.close();
                break;}
        }
    };

    @Override
    public void close() throws Exception {
        if (reader!=null){reader.close();}
        ch.flush();
        ch.close();

    }

}
