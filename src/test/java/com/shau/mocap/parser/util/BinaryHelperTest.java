package com.shau.mocap.parser.util;

import org.junit.Test;

import java.nio.ByteOrder;

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
}