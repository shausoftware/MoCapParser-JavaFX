package com.shau.mocap.parser.c3d.domain;

import com.shau.mocap.exception.ParserException;
import org.junit.Test;

import java.nio.ByteOrder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class C3dSpatialPointTest {

    @Test
    public void testSpatialPointConstructorWithInteger() throws Exception {
        byte[] pointData = {1, 0, 100, 0, 44, 1, 0, 0};
        C3dSpatialPoint spatialPoint = new C3dSpatialPoint(pointData, ByteOrder.LITTLE_ENDIAN, 2.0f);
        assertThat(spatialPoint, is(notNullValue()));
        assertThat(spatialPoint.getX(), is(2.0f));
        assertThat(spatialPoint.getY(), is(200.0f));
        assertThat(spatialPoint.getZ(), is(600.0f));
    }

    @Test
    public void testSpatialPointConstructorWithFloat() throws Exception {
        byte[] pointData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        C3dSpatialPoint spatialPoint = new C3dSpatialPoint(pointData, ByteOrder.LITTLE_ENDIAN, 1.0f);
        assertThat(spatialPoint, is(notNullValue()));
        assertThat(spatialPoint.getX(), is(0.0f));
        assertThat(spatialPoint.getY(), is(0.0f));
        assertThat(spatialPoint.getZ(), is(0.0f));
    }

    @Test(expected = ParserException.class)
    public void testSpatialPointConstructorInvalidData() throws Exception {
        byte[] pointData = {0, 0, 0};
        new C3dSpatialPoint(pointData, ByteOrder.LITTLE_ENDIAN, 1.0f);
    }
}