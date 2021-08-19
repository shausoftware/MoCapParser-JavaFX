package com.shau.mocap.parser.c3d;

import com.shau.mocap.exception.ParserException;
import com.shau.mocap.parser.c3d.domain.C3dGroup;
import com.shau.mocap.parser.c3d.domain.C3dParameter;
import com.shau.mocap.parser.util.BinaryHelper;

import java.nio.ByteOrder;
import java.util.Map;

public class C3dParameterHelper {

    public static Object create(int type, byte[] value, ByteOrder byteOrder) throws ParserException {
        try {
            if (type == -1) {
                return (char) value[0];
            } else  if (type == 1) {
                return (int) value[0];
            } else if (type == 2) {
                return BinaryHelper.readInt16(value, byteOrder);
            } else if (type == 4) {
                return BinaryHelper.readFloat(value, byteOrder);
            } else {
                throw new ParserException("Parameter type not recognised:" + type);
            }
        } catch (Exception e) {
            throw new ParserException("Unexpected error creating parameter value: " + e.getMessage());
        }
    }

    public static int parameterSize(int type) throws ParserException {
        if (type == -1) {
            return 1;
        } else if (type == 1) {
            return 1;
        } else if (type == 2) {
            return 2;
        } else if (type == 4) {
            return 4;
        } else {
            throw new ParserException("Parameter type not recognised:" + type);
        }
    }

    public static C3dParameter getParameter(String groupName, String parameterName, Map<Integer, C3dGroup> groups)
            throws ParserException {

        C3dGroup group = groups.entrySet().stream()
                .map(v -> v.getValue())
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElse(null);

        if (group != null) {
            C3dParameter parameter =  group.getParameters().stream()
                    .filter(p  -> p.getName().equals(parameterName))
                    .filter(p -> !p.isLocked())
                    .findFirst()
                    .orElse(null);
            if (parameter != null)
                return parameter;
            throw new ParserException("Unable to Parameter: " + parameterName);
        }
        throw new ParserException("Unable to find Group: " + groupName);
    }
}
