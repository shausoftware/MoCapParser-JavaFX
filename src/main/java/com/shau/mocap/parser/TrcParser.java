package com.shau.mocap.parser;

import com.shau.mocap.domain.Joint;
import com.shau.mocap.exception.ParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrcParser {

    public static List<String> validateHeaderAndReturnMocapData(List<String> lines) throws ParserException {
        if (lines.size() < 6) {
            throw new ParserException("Invalid TRC header size");
        }
        if (!lines.get(0).startsWith("PathFileType")) {
            throw new ParserException("Expecting PathFileType in TRC header");
        }
        String line = lines.get(5);
        if (lines.get(5).length() > 0) {
            throw new ParserException("Expecting empty line 5 in TRC header");
        }

        return lines.subList(6, lines.size());
    }

    public static List<Double> parseFrameData(String frame) throws ParserException {
        try {
            return Arrays.asList(frame.split("\\s+")).stream()
                    .filter(s -> s != null)
                    .mapToDouble(s ->  Double.valueOf(s.trim()))
                    .boxed()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ParserException("Error parsing frame data: " + e.getMessage());
        }
    }

    public static Integer parseFrameId(List<Double> values) throws ParserException {
        try {
            return values.get(0).intValue();
        } catch (Exception e) {
            throw new ParserException("Error parsing frame id: " + e.getMessage());
        }
    }

    public static List<Joint> parseJoints(List<Double> values) {

        List<Joint> frameJoints = new ArrayList<>();
        int jointId = 1;

        for (int j = 0; j < values.size(); j++) {
            int dataIndex = (j - 2) % 3;

            if (j > 1) {

                if (dataIndex == 0 && (j + 2) < values.size()) {
                    Double value1 = values.get(j);
                    Double value2 = values.get(j + 1);
                    Double value3 = values.get(j + 2);
                    frameJoints.add(new Joint(jointId++, value1, value2, value3));
                }
            }
        }

        return frameJoints;
    }
}