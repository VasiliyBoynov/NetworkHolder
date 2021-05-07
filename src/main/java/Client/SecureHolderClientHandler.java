package Client;

import Client.Commands.Answer;
import Client.Commands.Commands;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles a client-side channel.
 */
public class SecureHolderClientHandler extends SimpleChannelInboundHandler<byte[]> {

    private final Controller controller;

    public SecureHolderClientHandler(Controller controller){
        this.controller=controller;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        System.err.println(new String(msg));

        if (!controller.getAuthorization().get() &
        controller.getStartAuthorisation().get()){
            autorization(msg);
        };






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
            return new Answer(list);
        }

        if (msg[0] == (byte) 48){

        }

        return null;

    };

    private void autorization(byte[] msg){
        

        if (msgToObj(msg) instanceof Answer){
            Answer answer = (Answer) msgToObj(msg);
             if (answer.getInstruction().equals(Commands.user) |
                     answer.getInstruction().equals(Commands.newUser)){

                 controller.setAuthorization(answer.getRezl().get());
                 controller.setFinishAuthorisation(true);

             }
            

            
        }



    };
}
