package Server;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

public class SecureHolderServerHandlerOut extends ChannelOutboundHandlerAdapter {
    private ClientStatus client;

    public SecureHolderServerHandlerOut(ClientStatus client) {
        this.client = client;
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
    }
}