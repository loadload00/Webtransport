package org.almond.webtransport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.incubator.codec.http3.Http3RequestStreamFrame;

public interface WebtransportUnidStreamFrame
    extends Http3RequestStreamFrame, ByteBufHolder {

  @Override
  default long type() {
    return CodecUtils.WEBTRANSPORT_STREAM_TYPE;
  }

  @Override
  WebtransportUnidStreamFrame copy();

  @Override
  WebtransportUnidStreamFrame duplicate();

  @Override
  WebtransportUnidStreamFrame retainedDuplicate();

  @Override
  WebtransportUnidStreamFrame replace(ByteBuf content);

  @Override
  WebtransportUnidStreamFrame retain();

  @Override
  WebtransportUnidStreamFrame retain(int increment);

  @Override
  WebtransportUnidStreamFrame touch();

  @Override
  WebtransportUnidStreamFrame touch(Object hint);
}
