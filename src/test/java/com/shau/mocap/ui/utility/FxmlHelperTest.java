package com.shau.mocap.ui.utility;

import javafx.scene.paint.Color;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FxmlHelperTest {

    @Test
    public void testSupportedColourLookup() {
        Color testColour = FxmlHelper.getColourByName("White");
        assertThat(testColour, is(Color.WHITE));

        testColour = FxmlHelper.getColourByName("Red");
        assertThat(testColour, is(Color.RED));

        testColour = FxmlHelper.getColourByName("Green");
        assertThat(testColour, is(Color.GREEN));

        testColour = FxmlHelper.getColourByName("Blue");
        assertThat(testColour, is(Color.BLUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedColourLookup() {
        FxmlHelper.getColourByName("Yellow");
    }
}