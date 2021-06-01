import Constant.Constant;
import Handler.JsonDecoder;
import Handler.JsonEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
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
                new LengthFieldBasedFrameDecoder(Constant.MAX_FRAME_LENGTH.getConstant(), 0, Constant.INITIAL_BYTE_TO_STRIP.getConstant(), 0, Constant.INITIAL_BYTE_TO_STRIP.getConstant()));
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
