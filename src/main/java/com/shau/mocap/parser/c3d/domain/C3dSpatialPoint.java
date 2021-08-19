package com.shau.mocap.parser.c3d.domain;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.util.BinaryHelper;

import java.nio.ByteOrder;
import java.util.Arrays;

public class C3dSpatialPoint {

    private float x;
    private float y;
    private float z;
    private byte[] residualData;

    public C3dSpatialPoint(byte[] raw, ByteOrder byteOrder, float scale) throws ParserException {
        if (raw.length == 8) {
            x = BinaryHelper.readInt16(Arrays.copyOfRange(raw, 0, 2), byteOrder) * scale;
            y = BinaryHelper.readInt16(Arrays.copyOfRange(raw, 2,4), byteOrder) * scale;
            z = BinaryHelper.readInt16(Arrays.copyOfRange(raw, 4,6), byteOrder) * scale;
            residualData = Arrays.copyOfRange(raw, 6, 8);
        } else if (raw.length == 16) {
            x = BinaryHelper.readFloat(Arrays.copyOfRange(raw, 0,4), byteOrder) * scale;
            y = BinaryHelper.readFloat(Arrays.copyOfRange(raw, 4,8), byteOrder) * scale;
            z = BinaryHelper.readFloat(Arrays.copyOfRange(raw, 8,12), byteOrder) * scale;
            residualData = Arrays.copyOfRange(raw, 12, 16);
        } else {
            throw new ParserException("Expecting byte[] length of 8 or 16");
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public byte[] getResidualData() {
        return residualData;
    }
}
