package com.shau.mocap.parser;

import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.C3dSpatialPoint;
import com.shau.mocap.parser.c3d.domain.C3dValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MoCapParser {

    public static MoCapScene parse(String inFilePath, int start, int end) throws ParserException, IOException {

        MoCapScene moCapScene = parse(inFilePath);

        List<Frame> condensedFrames = new ArrayList<>();
        for (Frame frame : moCapScene.getFrames()) {
            List<Joint> condensedJoints = new ArrayList<>();
            int jointId = 1;
            for (Joint joint : frame.getJoints().subList(start, end)) {
                condensedJoints.add(new Joint(jointId++, joint.getX(), joint.getY(), joint.getZ()));
            }
            condensedFrames.add(new Frame(frame.getId(), condensedJoints));
        }

        return new MoCapScene(moCapScene.getFilename(), condensedFrames);
    }

    public static MoCapScene parse(String filePath) throws ParserException, IOException {

        String fileExtension = filePath.substring(filePath.lastIndexOf(".") + 1);

        MoCapScene moCapScene = null;
        if (fileExtension.equalsIgnoreCase("trc")) {
            moCapScene = parseTrcFile(filePath);
        } else if (fileExtension.equalsIgnoreCase("c3d")) {
            moCapScene = parseC3dFile(filePath);
        } else {
            throw new ParserException("Unable to load file " + fileExtension + ". Expecting .trc OR .c3d file types");
        }

        return moCapScene;
    }

    public static MoCapScene parseTrcFile(String inFilePath) throws IOException, ParserException {

        System.out.println("Loading trc file:" + inFilePath);

        File file = new File(inFilePath);
        List<String> lines = Files.readAllLines(file.toPath());
        List<String> data = TrcParser.validateHeaderAndReturnMocapData(lines);

        List<Frame> frames = new ArrayList<>();
        for (String frame : data) {
            try {
                List<Double> frameData = TrcParser.parseFrameData(frame);
                int frameId = TrcParser.parseFrameId(frameData);
                List<Joint> frameJoints = TrcParser.parseJoints(frameData);
                frames.add(new Frame(frameId, frameJoints));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String fileName = inFilePath.substring(inFilePath.lastIndexOf("/") + 1, inFilePath.lastIndexOf("."));
        return new MoCapScene(fileName, frames);
    }

    public static MoCapScene parseC3dFile(String inFilePath) throws IOException, ParserException {

        System.out.println("Loading c3d file:" + inFilePath);

        File file = new File(inFilePath);
        FileInputStream fis = new FileInputStream(file);

        List<C3dValue> dataValues = C3dParser.parseData(fis);

        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < dataValues.size(); i++) {
            C3dValue dataValue = dataValues.get(i);
            List<Joint> joints = new ArrayList<>();
            for (int j = 0; j < dataValue.getSpatialPoints().size(); j++) {
                C3dSpatialPoint spatialPoint = dataValue.getSpatialPoints().get(j);
                joints.add(new Joint(j + 1,
                        Double.valueOf(spatialPoint.getX()),
                        Double.valueOf(spatialPoint.getY()),
                        Double.valueOf(spatialPoint.getZ())));
            }
            frames.add(new Frame(i + 1, joints));
        }

        String fileName = inFilePath.substring(inFilePath.lastIndexOf("/") + 1, inFilePath.lastIndexOf("."));
        return new MoCapScene(fileName, frames);
    }
}
