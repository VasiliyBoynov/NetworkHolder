package Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a client-side channel.
 */
public class SecureHolderClientHandler extends SimpleChannelInboundHandler<byte[]> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        System.err.println(new String(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
