package com.shau.mocap.domain;

import com.shau.mocap.exception.PlayStateException;

import java.io.Serializable;
import java.util.Arrays;

public class PlayState implements Serializable {

    public static final int MIN_FRAME_DURATION = 10;
    public static final int MAX_FRAME_DURATION = 1000;
    public static final String[] views = new String[] {"XY", "ZY"};
    public static final Float[] scales =  new Float[] {10.0f, 5.0f, 2.0f, 1.0f, 0.5f, 0.25f, 0.1f};

    private int frameDuration = 100;
    private int currentFrame = 0;
    private int startFrame = 0;
    private int endFrame = 0;
    private int maxFrames = 0;
    private transient boolean play;
    private String view = views[0];
    private Float scale = scales[3];

    public PlayState(int maxFrames) {
        this.endFrame = maxFrames - 1;
        this.maxFrames = maxFrames;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public void incrementCurrentFrame() {
        if (++currentFrame > endFrame)
            currentFrame = startFrame;
    }

    public void updatePlayState(int newFrameDuration,
                                int newStartFrame,
                                int newEndFrame,
                                int newCurrentFrame,
                                String newView,
                                Float newScale) throws PlayStateException {

        //order is important
        if (newFrameDuration < MIN_FRAME_DURATION || newFrameDuration > MAX_FRAME_DURATION)
            throw new PlayStateException("frame duration", newFrameDuration, MIN_FRAME_DURATION, MAX_FRAME_DURATION);
        if (newStartFrame < 0 || newStartFrame >= newEndFrame - 1)
            throw new PlayStateException("start frame", newStartFrame, 0, newEndFrame - 1);
        if (newEndFrame < newStartFrame + 1 || newEndFrame > maxFrames - 1)
            throw new PlayStateException("end frame", newEndFrame, newStartFrame + 1, maxFrames - 1);
        if (newCurrentFrame < newStartFrame || newCurrentFrame > newEndFrame)
            throw new PlayStateException("current frame", newCurrentFrame, newStartFrame, newEndFrame);
        if (!Arrays.asList(views).contains(newView))
            throw new PlayStateException("invalid view (" + newView + "). Expecting XY, ZY");
        if (!Arrays.asList(scales).contains(newScale))
            throw new PlayStateException("invalid scale (" + newScale + "). Expecting 10.0, 5.0, 2.0, 1.0, 0.5, 0.25, 0.1");

        this.frameDuration = newFrameDuration;
        this.startFrame = newStartFrame;
        this.endFrame = newEndFrame;
        this.currentFrame = newCurrentFrame;
        this.view = newView;
        this.scale = newScale;
    }

    public int getFrameDuration() {
        return frameDuration;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getStartFrame() {
        return startFrame;
    }

    public int getEndFrame() {
        return endFrame;
    }

    public int getMaxFrames() {
        return maxFrames;
    }

    public boolean isPlay() {
        return play;
    }

    public String getView() {
        return view;
    }

    public Float getScale() {
        return scale;
    }
}
