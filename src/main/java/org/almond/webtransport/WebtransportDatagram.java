package org.almond.webtransport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class WebtransportDatagram extends ChannelInboundHandlerAdapter {

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebtransportDatagram.class);

  WebtransportSessionId webtransportSessionId = new WebtransportSessionId();

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf) msg;
      QuicChannel quicChannel = (QuicChannel) ctx.channel();
      long id = (long) buf.readByte();
      if (webtransportSessionId.get(quicChannel.id().toString()) == id) {
        DatagramRead(ctx, buf);
      } else {
        ReferenceCountUtil.release(buf);
      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (cause instanceof QuicException) {
      handleQuicException(ctx, (QuicException) cause);
    } else {
      ctx.fireExceptionCaught(cause);
    }
  }

  protected abstract void DatagramRead(ChannelHandlerContext ctx, ByteBuf msg);

  protected void handleQuicException(@SuppressWarnings("unused") ChannelHandlerContext ctx, QuicException exception) {
    logger.debug("Caught QuicException on channel {}", ctx.channel(), exception);
  }

}
