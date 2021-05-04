package Client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutorGroup;

public class SecureHolderClientHandlerOut extends ChannelOutboundHandlerAdapter {


    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
        //System.out.println(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        ctx.pipeline().channel();
        System.err.println("DEBUG: "+new String((byte[]) msg));

    }
}
