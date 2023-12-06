package org.almond.webtransport;

import io.netty.buffer.ByteBuf;

public final class CodecUtils {

  public static final long DEFAULT_MAX_HEADER_LIST_SIZE = 256;
  public static final int PRIORITY_UPDATE_FRAME_PUSH_TYPE_ID = 0xF0701;
  public static final int PRIORITY_UPDATE_FRAME_REQUEST_TYPE_ID = 0xF0700;
  public static final int WEBTRANSPORT_FRAME_TYPE_ID = 0x41;
  public static final int WEBTRANSPORT_STREAM_TYPE = 0x54;
  public static final long H3_DATAGRAM = 0x33;
  // public static final long H3_DATAGRAM = 0xffd277;
  // draft-ietf-webtrans-http3-02
  public static final long ENABLE_WEBTRANSPORT = 0x2b603742;
  // public static final long ENABLE_WEBTRANSPORT = 0xc671706a;

  private CodecUtils() {
  }

  static void writeVariableLengthInteger(ByteBuf out, long value) {
    int numBytes = numBytesForVariableLengthInteger(value);
    writeVariableLengthInteger(out, value, numBytes);
  }

  static int numBytesForVariableLengthInteger(long value) {
    if (value <= 63) {
      return 1;
    }
    if (value <= 16383) {
      return 2;
    }
    if (value <= 1073741823) {
      return 4;
    }
    if (value <= 4611686018427387903L) {
      return 8;
    }
    throw new IllegalArgumentException();
  }

  static void writeVariableLengthInteger(ByteBuf out, long value, int numBytes) {
    int writerIndex = out.writerIndex();
    switch (numBytes) {
      case 1:
        out.writeByte((byte) value);
        break;
      case 2:
        out.writeShort((short) value);
        encodeLengthIntoBuffer(out, writerIndex, (byte) 0x40);
        break;
      case 4:
        out.writeInt((int) value);
        encodeLengthIntoBuffer(out, writerIndex, (byte) 0x80);
        break;
      case 8:
        out.writeLong(value);
        encodeLengthIntoBuffer(out, writerIndex, (byte) 0xc0);
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  private static void encodeLengthIntoBuffer(ByteBuf out, int index, byte b) {
    out.setByte(index, out.getByte(index) | b);
  }

  static long readVariableLengthInteger(ByteBuf in, int len) {
    switch (len) {
      case 1:
        return in.readUnsignedByte();
      case 2:
        return in.readUnsignedShort() & 0x3fff;
      case 4:
        return in.readUnsignedInt() & 0x3fffffff;
      case 8:
        return in.readLong() & 0x3fffffffffffffffL;
      default:
        throw new IllegalArgumentException();
    }
  }
}
