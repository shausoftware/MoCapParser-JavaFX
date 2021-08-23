package com.shau.mocap.parser.util;

import com.shau.mocap.util.BinaryHelper;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BinaryHelperTest {

    @Test
    public void testEndianForProcessorType() {
        int processorType = 85; //intel and others
        ByteOrder byteOrder = BinaryHelper.getEndian(processorType);
        assertThat(byteOrder, is(ByteOrder.LITTLE_ENDIAN));

        processorType = 86; //DEC exception
        byteOrder = BinaryHelper.getEndian(processorType);
        assertThat(byteOrder, is(ByteOrder.BIG_ENDIAN));
    }

    @Test
    public void testReadInt16() {
        //intel and others
        byte[] word = {44, 1};
        int res = BinaryHelper.readInt16(word, ByteOrder.LITTLE_ENDIAN);
        assertThat(res, is(300));
        //DEC
        word = new byte[] {1, 44};
        res = BinaryHelper.readInt16(word, ByteOrder.BIG_ENDIAN);
        assertThat(res, is(300));
    }

    @Test
    public void test16BitEncoding() {
        int x = 100;
        int y = 2;

        Integer res = BinaryHelper.encode(x, y);

        byte[] bRes = ByteBuffer.allocate(4).putInt(res).array();
        int xRes = ByteBuffer.wrap(Arrays.copyOfRange(bRes, 0, 2)).getShort();
        int yRes = ByteBuffer.wrap(Arrays.copyOfRange(bRes, 2, 4)).getShort();

        assertThat(xRes, is(x));
        assertThat(yRes, is(y));

        x = 5000;
        y = 276;

        res = BinaryHelper.encode(x, y);

        bRes = ByteBuffer.allocate(4).putInt(res).array();
        xRes = ByteBuffer.wrap(Arrays.copyOfRange(bRes, 0, 2)).getShort();
        yRes = ByteBuffer.wrap(Arrays.copyOfRange(bRes, 2, 4)).getShort();

        assertThat(xRes, is(x));
        assertThat(yRes, is(y));
    }

    @Test
    public void test8BitEncoding() {
        int x = 0;
        int y = 91;
        int z = 129;
        int w = 255;

        Integer res = BinaryHelper.encode(x, y, z, w);

        byte[] bRes = ByteBuffer.allocate(4).putInt(res).array();

        int xRes = (0xFF) & bRes[0];
        int yRes = (0xFF) & bRes[1];
        int zRes = (0xFF) & bRes[2];
        int wRes = (0xFF) & bRes[3];

        assertThat(xRes, is(x));
        assertThat(yRes, is(y));
        assertThat(zRes, is(z));
        assertThat(wRes, is(w));
    }
}