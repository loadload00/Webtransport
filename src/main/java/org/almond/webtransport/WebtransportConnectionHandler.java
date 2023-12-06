package org.almond.webtransport;

import java.util.function.LongFunction;
import io.netty.channel.ChannelHandler;
import io.netty.incubator.codec.http3.Http3ServerConnectionHandler;
import io.netty.incubator.codec.http3.Http3SettingsFrame;

public class WebtransportConnectionHandler extends Http3ServerConnectionHandler {

  public WebtransportConnectionHandler(ChannelHandler requestStreamHandler, Http3SettingsFrame localSettings) {
    this(requestStreamHandler, null, null, localSettings, true);
  }

  public WebtransportConnectionHandler(ChannelHandler requestStreamHandler,
      ChannelHandler inboundControlStreamHandler,
      LongFunction<ChannelHandler> unknownInboundStreamHandlerFactory,
      Http3SettingsFrame localSettings, boolean disableQpackDynamicTable) {
    super(requestStreamHandler, inboundControlStreamHandler, unknownInboundStreamHandlerFactory, localSettings,
        disableQpackDynamicTable);
  }
}
