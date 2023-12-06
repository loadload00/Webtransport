package org.almond.webtransport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.StringUtil;

public final class DefaultWebtransportUnidStreamFrame extends DefaultByteBufHolder
    implements WebtransportUnidStreamFrame {

  public DefaultWebtransportUnidStreamFrame(ByteBuf data) {
    super(data);
  }

  @Override
  public WebtransportUnidStreamFrame copy() {
    return new DefaultWebtransportUnidStreamFrame(content().copy());
  }

  @Override
  public WebtransportUnidStreamFrame duplicate() {
    return new DefaultWebtransportUnidStreamFrame(content().duplicate());
  }

  @Override
  public WebtransportUnidStreamFrame retainedDuplicate() {
    return new DefaultWebtransportUnidStreamFrame(content().retainedDuplicate());
  }

  @Override
  public WebtransportUnidStreamFrame replace(ByteBuf content) {
    return new DefaultWebtransportUnidStreamFrame(content);
  }

  @Override
  public WebtransportUnidStreamFrame retain() {
    super.retain();
    return this;
  }

  @Override
  public WebtransportUnidStreamFrame retain(int increment) {
    super.retain(increment);
    return this;
  }

  @Override
  public WebtransportUnidStreamFrame touch() {
    super.touch();
    return this;
  }

  @Override
  public WebtransportUnidStreamFrame touch(Object hint) {
    super.touch(hint);
    return this;
  }

  @Override
  public String toString() {
    return StringUtil.simpleClassName(this) + "(content=" + content() + ')';
  }
}
