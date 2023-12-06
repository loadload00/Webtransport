package org.almond.webtransport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.http3.Http3ClientConnectionHandler;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class WebtransportClientHandler extends Http3ClientConnectionHandler {

  private static final InternalLogger logger = InternalLoggerFactory
      .getInstance(WebtransportClientHandler.class);

  public WebtransportClientHandler() {
    super(null, null, null, null, true);
  }

  @Override
  protected void initBidirectionalStream(ChannelHandlerContext ctx, QuicStreamChannel channel) {
  }
}
