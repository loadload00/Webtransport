package org.almond.webtransport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public final class DefaultWebtransportBidStreamFrame extends DefaultByteBufHolder
    implements WebtransportBidStreamFrame {

  public DefaultWebtransportBidStreamFrame(ByteBuf data) {
    super(data);
  }

  @Override
  public WebtransportBidStreamFrame copy() {
    return new DefaultWebtransportBidStreamFrame(content().copy());
  }

  @Override
  public WebtransportBidStreamFrame duplicate() {
    return new DefaultWebtransportBidStreamFrame(content().duplicate());
  }

  @Override
  public WebtransportBidStreamFrame retainedDuplicate() {
    return new DefaultWebtransportBidStreamFrame(content().retainedDuplicate());
  }

  @Override
  public WebtransportBidStreamFrame replace(ByteBuf content) {
    return new DefaultWebtransportBidStreamFrame(content);
  }

  @Override
  public WebtransportBidStreamFrame retain() {
    super.retain();
    return this;
  }

  @Override
  public WebtransportBidStreamFrame retain(int increment) {
    super.retain(increment);
    return this;
  }

  @Override
  public WebtransportBidStreamFrame touch() {
    super.touch();
    return this;
  }

  @Override
  public WebtransportBidStreamFrame touch(Object hint) {
    super.touch(hint);
    return this;
  }

  @Override
  public String toString() {
    return StringUtil.simpleClassName(this) + "(content=" + content() + ')';
  }
}
