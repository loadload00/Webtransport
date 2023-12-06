package org.almond.webtransport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.DefaultHttp3HeadersFrame;
import io.netty.incubator.codec.http3.Http3DataFrame;
import io.netty.incubator.codec.http3.Http3HeadersFrame;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.ReferenceCountUtil;

public abstract class WebtransportStreamHandler extends H3RequestStreamInboundHandler {

  WebtransportSessionId webtransportSessionId = new WebtransportSessionId();

  @Override
  protected void channelRead(ChannelHandlerContext ctx, Http3HeadersFrame frame) throws Exception {
    if (frame.headers().contains(":protocol", "webtransport")) {
      QuicStreamChannel streamChannel = (QuicStreamChannel) ctx.channel();
      acceptWebtransportOrNot(ctx, frame, streamChannel.streamId());
    } else {
      Http3HeadersFrame headersFrame = new DefaultHttp3HeadersFrame();
      headersFrame.headers().status("403");
      ctx.writeAndFlush(headersFrame).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
    }
    ReferenceCountUtil.release(frame);
  }

  @Override
  protected abstract void channelRead(ChannelHandlerContext ctx, WebtransportBidStreamFrame frame) throws Exception;

  @Override
  protected abstract void channelRead(ChannelHandlerContext ctx, WebtransportUnidStreamFrame frame) throws Exception;

  @Override
  protected void channelRead(ChannelHandlerContext ctx, Http3DataFrame frame) throws Exception {
    ReferenceCountUtil.release(frame);
  }

  @Override
  protected abstract void channelInputClosed(ChannelHandlerContext ctx) throws Exception;

  protected abstract void webtransportConnected(ChannelHandlerContext ctx);

  private void acceptWebtransportOrNot(ChannelHandlerContext ctx, Http3HeadersFrame frame, long id) throws Exception {
    Http3HeadersFrame headersFrame = new DefaultHttp3HeadersFrame();
    headersFrame.headers().status("200");
    headersFrame.headers().add("sec-webtransport-http3-draft", "draft02");
    ctx.writeAndFlush(headersFrame).addListener(f -> {
      if (!f.isSuccess()) {
        ctx.fireExceptionCaught(f.cause());
        ctx.close();
      }
    });
    QuicChannel quicChannel = (QuicChannel) ctx.channel().parent();
    QuicStreamChannel quicStreamChannel = (QuicStreamChannel) ctx.channel();
    String quicChannelId = quicChannel.id().toString();
    webtransportSessionId.set(quicChannelId, quicStreamChannel.streamId());
    webtransportConnected(ctx);
    quicStreamChannel.closeFuture().addListener(f -> {
      quicChannel.close();
    });
    quicChannel.closeFuture().addListener(f -> {
      webtransportSessionId.remove(quicChannelId);
    });
  }
}
