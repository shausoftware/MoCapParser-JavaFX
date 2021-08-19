package com.shau.mocap.parser.util;

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
}
