package com.shau.mocap.domain;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SpatialOffsetTest {

    private static final Integer jointId = 1;
    private static final Double pointX = 1.0;
    private static final Double pointY = 2.0;
    private static final Double pointZ = 3.0;

    @Test
    public void testInitialisationStates() {

        SpatialOffset spatialOffset = new SpatialOffset();
        assertThat(spatialOffset.getOffsetMode(), is(SpatialOffset.OFFSET_NONE));
        assertThat(spatialOffset.getOffsetPointX(), is(nullValue()));
        assertThat(spatialOffset.getOffsetPointY(), is(nullValue()));
        assertThat(spatialOffset.getOffsetPointZ(), is(nullValue()));

        spatialOffset = new SpatialOffset(jointId);
        assertThat(spatialOffset.getOffsetMode(), is(SpatialOffset.OFFSET_JOINT));
        assertThat(spatialOffset.getOffsetPointX(), is(nullValue()));
        assertThat(spatialOffset.getOffsetPointY(), is(nullValue()));
        assertThat(spatialOffset.getOffsetPointZ(), is(nullValue()));

        spatialOffset = new SpatialOffset(pointX, pointY, pointZ);
        assertThat(spatialOffset.getOffsetMode(), is(SpatialOffset.OFFSET_XYZ));
        assertThat(spatialOffset.getOffsetPointX(), is(closeTo(pointX, 0.001)));
        assertThat(spatialOffset.getOffsetPointY(), is(closeTo(pointY, 0.001)));
        assertThat(spatialOffset.getOffsetPointZ(), is(closeTo(pointZ, 0.001)));
    }
}