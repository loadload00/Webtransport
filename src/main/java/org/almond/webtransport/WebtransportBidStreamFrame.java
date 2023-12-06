package org.almond.webtransport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.incubator.codec.http3.Http3RequestStreamFrame;

public interface WebtransportBidStreamFrame
    extends Http3RequestStreamFrame, ByteBufHolder {

  @Override
  default long type() {
    return CodecUtils.WEBTRANSPORT_FRAME_TYPE_ID;
  }

  @Override
  WebtransportBidStreamFrame copy();

  @Override
  WebtransportBidStreamFrame duplicate();

  @Override
  WebtransportBidStreamFrame retainedDuplicate();

  @Override
  WebtransportBidStreamFrame replace(ByteBuf content);

  @Override
  WebtransportBidStreamFrame retain();

  @Override
  WebtransportBidStreamFrame retain(int increment);

  @Override
  WebtransportBidStreamFrame touch();

  @Override
  WebtransportBidStreamFrame touch(Object hint);
}
