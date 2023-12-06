package org.almond.webtransport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.incubator.codec.http3.Http3;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3Exception;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.http3.Http3UnknownFrame;
import io.netty.incubator.codec.quic.QuicException;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public abstract class H3RequestStreamInboundHandler extends ChannelInboundHandlerAdapter {

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(H3RequestStreamInboundHandler.class);

  @Override
  public final void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof WebtransportBidStreamFrame) {
      channelRead(ctx, (WebtransportBidStreamFrame) msg);
    } else if (msg instanceof WebtransportUnidStreamFrame) {
      channelRead(ctx, (WebtransportUnidStreamFrame) msg);
    } else if (msg instanceof Http3HeadersFrame) {
      channelRead(ctx, (Http3HeadersFrame) msg);
    } else if (msg instanceof Http3DataFrame) {
      channelRead(ctx, (Http3DataFrame) msg);
    } else if (msg instanceof Http3UnknownFrame) {
      channelRead(ctx, (Http3UnknownFrame) msg);
    } else {
      super.channelRead(ctx, msg);
    }
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt == ChannelInputShutdownEvent.INSTANCE) {
      channelInputClosed(ctx);
    }
    ctx.fireUserEventTriggered(evt);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (cause instanceof QuicException) {
      handleQuicException(ctx, (QuicException) cause);
    } else if (cause instanceof Http3Exception) {
      handleHttp3Exception(ctx, (Http3Exception) cause);
    } else {
      ctx.fireExceptionCaught(cause);
    }
  }

  protected abstract void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame)
      throws Exception;

  protected abstract void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame)
      throws Exception;

  protected abstract void channelRead(ChannelHandlerContext ctx, WebtransportBidStreamFrame frame)
      throws Exception;

  protected abstract void channelRead(ChannelHandlerContext ctx, WebtransportUnidStreamFrame frame)
      throws Exception;

  protected abstract void channelInputClosed(ChannelHandlerContext ctx) throws Exception;

  protected void channelRead(@SuppressWarnings("unused") ChannelHandlerContext ctx, Http3UnknownFrame frame) {
    frame.release();
  }

  protected void handleQuicException(@SuppressWarnings("unused") ChannelHandlerContext ctx, QuicException exception) {
    logger.debug("Caught QuicException on channel {}", ctx.channel(), exception);
  }

  protected void handleHttp3Exception(@SuppressWarnings("unused") ChannelHandlerContext ctx,
      Http3Exception exception) {
    logger.error("Caught Http3Exception on channel {}", ctx.channel(), exception);
  }

  protected final QuicStreamChannel controlStream(ChannelHandlerContext ctx) {
    return Http3.getLocalControlStream(ctx.channel().parent());
  }
}
