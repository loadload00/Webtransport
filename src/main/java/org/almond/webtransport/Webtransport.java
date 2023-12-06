package org.almond.webtransport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.incubator.codec.quic.QuicStreamType;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class Webtransport {

  private static final InternalLogger logger = InternalLoggerFactory
      .getInstance(Webtransport.class);

  private static final ConcurrentMap<String, QuicStreamChannel> channels = new ConcurrentHashMap<>();

  private WebtransportSessionId webtransportSessionId = new WebtransportSessionId();

  public Webtransport() {

  }

  public void SendUnidStream(ChannelHandlerContext ctx, ByteBuf msg, boolean finsh) {
    QuicStreamChannel streamChannel = null;
    QuicChannel quicChannel = (QuicChannel) ctx.channel().parent();
    String id = quicChannel.id().toString();
    try {
      if (!channels.isEmpty()) {
        streamChannel = channels.get(id);
        channels.remove(id);
      }
      if (streamChannel == null) {
        streamChannel = CreateUnidStream(quicChannel, null);
      }
      if (finsh) {
        streamChannel.writeAndFlush(msg).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
      } else {
        streamChannel.writeAndFlush(msg);
        channels.put(id, streamChannel);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void SendUnidH3(QuicChannel quicChannel, ByteBuf msg, boolean finsh) {
    try {
      QuicStreamChannel streamChannel = CreateUnidStream(quicChannel, null);
      if (finsh) {
        streamChannel.writeAndFlush(msg).addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);
      } else {
        streamChannel.writeAndFlush(msg);
      }
    } catch (Exception e) {
      msg.release();
      e.printStackTrace();
    }
  }

  public QuicStreamChannel CreateBidiStream(ChannelHandlerContext ctx, ChannelHandler handler) throws Exception {
    QuicChannel quicChannel = (QuicChannel) ctx.channel().parent();
    String channelId = quicChannel.id().toString();
    QuicStreamChannel streamChannel = quicChannel.createStream(QuicStreamType.BIDIRECTIONAL, handler)
        .addListener(f -> {
          if (!f.isSuccess()) {
            ctx.fireExceptionCaught(f.cause());
            ctx.close();
          }
        }).sync().getNow();
    ByteBuf TypeBuf = ctx.alloc().directBuffer(2);
    CodecUtils.writeVariableLengthInteger(TypeBuf, CodecUtils.WEBTRANSPORT_FRAME_TYPE_ID);
    streamChannel.writeAndFlush(TypeBuf);
    ByteBuf buf = ctx.alloc().directBuffer(1);
    buf.writeByte((byte) webtransportSessionId.get(channelId));
    streamChannel.writeAndFlush(buf);
    return streamChannel;
  }

  public QuicStreamChannel CreateUnidStream(QuicChannel quicChannel, ChannelHandler handler) throws Exception {
    String channelId = quicChannel.id().toString();
    QuicStreamChannel streamChannel = quicChannel.createStream(QuicStreamType.UNIDIRECTIONAL, handler).sync()
        .getNow();
    ByteBuf TypeBuf = streamChannel.alloc().directBuffer(2);
    CodecUtils.writeVariableLengthInteger(TypeBuf, CodecUtils.WEBTRANSPORT_STREAM_TYPE);
    streamChannel.writeAndFlush(TypeBuf);
    ByteBuf buf = streamChannel.alloc().directBuffer(1);
    buf.writeByte((byte) webtransportSessionId.get(channelId));
    streamChannel.writeAndFlush(buf);
    return streamChannel;
  }

  public void DatagramSendAll(ChannelHandlerContext ctx, ByteBuf msg) {
    int size = msg.readableBytes();
    int sendSize = 1350;
    QuicChannel quicChannel = (QuicChannel) ctx.channel().parent();
    String channelId = quicChannel.id().toString();
    byte sessionId = (byte) webtransportSessionId.get(channelId);
    int count = 0;
    ByteBuf buffer = quicChannel.alloc().directBuffer(1);
    buffer.writeByte(sessionId);
    try {
      while (count < size) {
        sendSize = Math.min(sendSize, msg.readableBytes());
        quicChannel
            .writeAndFlush(
                Unpooled.copiedBuffer(buffer, msg.readRetainedSlice(sendSize)))
            .sync();
        count += sendSize;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      buffer.release();
      msg.release();
    }
  }

  public void DatagramSend(QuicChannel quicChannel, ByteBuf msg) {
    String channelId = quicChannel.id().toString();
    byte sessionId = (byte) webtransportSessionId.get(channelId);
    ByteBuf buffer = quicChannel.alloc().directBuffer();
    buffer.writeByte(sessionId);
    buffer.writeBytes(msg);
    quicChannel.writeAndFlush(buffer);
    msg.release();
  }

}
