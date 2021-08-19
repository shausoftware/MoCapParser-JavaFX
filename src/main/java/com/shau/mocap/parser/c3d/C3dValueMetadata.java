package com.shau.mocap.parser.c3d;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.C3dGroup;

import java.util.Map;

public class C3dValueMetadata {

    private int dataStart;
    private float scale;
    private int frames;
    private int points;
    private float pointRate;
    private int analogChannels = 0;
    private int analogSamples = 0;
    private int size; //bytes
    private int pointsSize;
    private int blocks;

    public C3dValueMetadata(Map<Integer, C3dGroup> groupParameters) throws ParserException {

        //whilst header contains useful info parameters should be used for definitions
        dataStart = (int) C3dParameterHelper
                .getParameter("POINT", "DATA_START", groupParameters)
                .getValues().get(0);
        scale = (float) C3dParameterHelper
                .getParameter("POINT", "SCALE", groupParameters)
                .getValues().get(0);
        frames = (int) C3dParameterHelper
                .getParameter("POINT", "FRAMES", groupParameters)
                .getValues().get(0);
        points = (int) C3dParameterHelper
                .getParameter("POINT", "USED", groupParameters)
                .getValues().get(0);
        pointRate = (float) C3dParameterHelper
                .getParameter("POINT", "RATE", groupParameters)
                .getValues().get(0);

        try {
            analogChannels = (int) C3dParameterHelper
                    .getParameter("ANALOG", "USED", groupParameters)
                    .getValues().get(0);
            float analogRate = (float) C3dParameterHelper
                    .getParameter("ANALOG", "RATE", groupParameters)
                    .getValues().get(0);
            analogSamples = (int) (analogRate / pointRate);
        } catch (Exception e) {
            System.out.println("Unable to initialize Analog Channels. Continuing without them");
        }

        size = scale > 0 ? 2 : 4; //bytes
        pointsSize = size * points * 4;
        int analogsSize = size * analogChannels * analogSamples;
        blocks = (((pointsSize + analogsSize) * frames) / 512) + 1;
    }

    public int getDataStart() {
        return dataStart;
    }

    public float getScale() {
        return scale;
    }

    public int getFrames() {
        return frames;
    }

    public int getPoints() {
        return points;
    }

    public float getPointRate() {
        return pointRate;
    }

    public int getAnalogChannels() {
        return analogChannels;
    }

    public int getAnalogSamples() {
        return analogSamples;
    }

    public int getSize() {
        return size;
    }

    public int getPointsSize() {
        return pointsSize;
    }

    public int getBlocks() {
        return blocks;
    }
}
