import Commands.Answer;
import Commands.Message;
import Commands.NewUser;
import Commands.User;
import Controller.Controller;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class SecureHolderClientHandler extends SimpleChannelInboundHandler<Message> {
    private final Controller controller;

    public SecureHolderClientHandler(Controller controller){
        this.controller=controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        System.out.printf("DEBUG:"+msg);
        System.out.println(msg.getClass().getName());
        if (msg instanceof Answer & ((Answer) msg).getTypeMessage()!=null){
            if (((Answer) msg).getTypeMessage().equals("Autorization")){
                controller.setAuthorization(((Answer) msg).isRezl());
                controller.setTxt(((Answer) msg).getText());
                controller.setFinishAuthorisation(true);
            }


        }


    }
}
