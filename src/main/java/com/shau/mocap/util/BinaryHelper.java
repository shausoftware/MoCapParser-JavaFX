package com.shau.mocap.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryHelper {

    public static ByteOrder getEndian(int processorType) {
        return processorType == 86 ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN; //86 DEC
    }

    public static int readInt16( byte [] word, ByteOrder byteOrder) {
        return ByteOrder.BIG_ENDIAN == byteOrder ? ((word[0]<<8) | (word[1] & 0xFF)) : ((word[1]<<8) | (word[0] & 0xFF));
    }

    public static float readFloat(byte[] dword, ByteOrder byteOrder) {
        return ByteBuffer.wrap(dword).order(byteOrder).getFloat();
    }

    //encode 2 * 16 bit ints in Integer
    public static Integer encode(int a, int b) {
        return (a << 16) | b;
    }

    //encode 4 * 8 bit ints in Integer
    public static Integer encode(int a, int b, int c, int d) {
        byte[] ba = new byte[] {(byte) a, (byte) b, (byte) c, (byte) d};
        return ByteBuffer.wrap(ba).getInt();
    }
}
