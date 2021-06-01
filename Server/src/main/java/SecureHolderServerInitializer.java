import ClientStatus.ClientStatus;
import Constant.Constant;
import Handler.JsonDecoder;
import Handler.JsonEncoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

public class SecureHolderServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private ClientStatus client;


    public SecureHolderServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
        this.client = new ClientStatus();

    }
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast("frameDecoder",
                new LengthFieldBasedFrameDecoder(Constant.MAX_FRAME_LENGTH.getConstant(), 0, Constant.INITIAL_BYTE_TO_STRIP.getConstant(), 0, Constant.INITIAL_BYTE_TO_STRIP.getConstant()));
        pipeline.addLast("frameEncoder",
                new LengthFieldPrepender(2));
        pipeline.addLast("bytesDecoder",
                new ByteArrayDecoder());
        pipeline.addLast("bytesEncoder", new ByteArrayEncoder());
        pipeline.addLast("JSONDecoder",
                new JsonDecoder());
        pipeline.addLast("JSONEncoder",
                new JsonEncoder());

        pipeline.addLast(new SecureHolderServerHandler(client));
        //pipeline.addLast(new SecureHolderServerHandlerOut(client));
    }

}