import Handler.JsonDecoder;
import Handler.JsonEncoder;
import io.netty.channel.ChannelHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslContext;
import Controller.Controller;


public class SecureHolderClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SecureHolderClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(sslCtx.newHandler(ch.alloc(), Client.HOST, Client.PORT));
        pipeline.addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(65536, 0, 2, 0, 2));
        pipeline.addLast("frameEncoder",
                new LengthFieldPrepender(2));
        pipeline.addLast("bytesDecoder",
                new ByteArrayDecoder());
        pipeline.addLast("bytesEncoder",
                new ByteArrayEncoder());
        pipeline.addLast("JSONDecoder",
                new JsonDecoder());
        pipeline.addLast("JSONEncoder",
                new JsonEncoder());


        // and then business logic.
        pipeline.addLast(new SecureHolderClientHandler(Controller.instance));
        //pipeline.addLast(new SecureHolderClientHandlerOut());
    }
}
