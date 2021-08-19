package com.shau.mocap.fourier;

import com.shau.mocap.MoCapFileHandler;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FourierGenerator {

    public static void generateFourier(MoCapScene moCapScene,
                                            int startFrame,
                                            int endFrame,
                                            int fourierFrames,
                                            boolean loop) {

        int frames = endFrame - startFrame;
        int allJoints = moCapScene.getFrames().get(startFrame).getJoints().size();
        //displayed joints
        int joints = moCapScene.getFrames().get(startFrame).getJoints().stream()
                .filter(j -> j.isDisplay())
                .collect(Collectors.toList()).size();

        Double[][][] transform = new Double[joints][fourierFrames][6];
        int jointCount = 0;
        for (int i = 0;  i < allJoints; i++) {
            int index = i;
            List<Joint> fourierJoints = moCapScene.getFrames().stream()
                    .skip(startFrame)
                    .limit(frames)
                    .map(f -> f.getJoints().get(index))
                    .collect(Collectors.toList());

            if (fourierJoints.get(0).isDisplay()) {
                transform[jointCount++] = calculateFourier(fourierJoints,
                        fourierFrames,
                        startFrame,
                        loop,
                        moCapScene.getSpatialOffset().getOffsetJointId());
            }
        }

        StringBuffer shaderBuffer = generateFourierOutput(transform, frames, fourierFrames, loop);
        MoCapFileHandler.saveShaderOutput(moCapScene.getFilename(), shaderBuffer);
    }

    private static Double[][] calculateFourier(List<Joint> joints,
                                               int fourierFrames,
                                               int startFrame,
                                               boolean loop,
                                               Integer centerJointId) {

        Double[][] fcs = new Double[fourierFrames][6];
        int frames = joints.size();
        Joint centerJoint = centerJointId == null ? null : joints.get(centerJointId - 1);

        for (int k = 0;  k < fourierFrames; k++) {
            Double[] fc = new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < frames; i++) {
                Joint joint = joints.get(i);
                Double[] bin = generateBin(joint, centerJoint, i, k, frames + (loop ? 1 : 0));
                fc[0] += bin[0];
                fc[1] += bin[1];
                fc[2] += bin[2];
                fc[3] += bin[3];
                fc[4] += bin[4];
                fc[5] += bin[5];
            }
            if (loop) {
                Joint joint = joints.get(0);
                Double[] bin = generateBin(joint, centerJoint, frames, k, frames + 1);
                fc[0] += bin[0];
                fc[1] += bin[1];
                fc[2] += bin[2];
                fc[3] += bin[3];
                fc[4] += bin[4];
                fc[5] += bin[5];
            }
            fcs[k] = fc;
        }

        return fcs;
    }

    private static Double[] generateBin(Joint joint, Joint centerJoint, int i, int k, int f) {

        Double xPos = joint.getX();
        Double yPos = joint.getY();
        Double zPos = joint.getZ();

        if (centerJoint != null) {
            xPos -= centerJoint.getX();
            //yPos -= centerJoint.getY();
            zPos -= centerJoint.getZ();
        }

        Double an = -6.283185 * Double.valueOf(k) * Double.valueOf(i) / Double.valueOf(f);
        Double[] ex = new Double[] {Math.cos(an), Math.sin(an)};

        return new Double[] {xPos * ex[0], xPos * ex[1], yPos * ex[0], yPos * ex[1], zPos * ex[0], zPos * ex[1]};

    }

    private static Integer encode(Integer a, Integer b) {
        return (a << 16) | b;
    }

    private static  StringBuffer generateFourierOutput(Double[][][] transform,
                                          int frames,
                                          //int joints,
                                          int fourierFrames,
                                          boolean loop) {

        /* OUTPUT */
        /* Change to suit your needs */

        String LS = System.lineSeparator();

        Integer minX = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[0], d[1]}))
                .map(dv -> dv.intValue())
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        Integer maxX = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[0], d[1]}))
                .map(dv -> dv.intValue())
                .max(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        Integer minY = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[2], d[3]}))
                .map(dv -> dv.intValue())
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        Integer maxY = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[2], d[3]}))
                .map(dv -> dv.intValue())
                .max(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        Integer minZ = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[4], d[5]}))
                .map(dv -> dv.intValue())
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);
        Integer maxZ = Arrays.stream(transform).flatMap(dd ->  Arrays.stream(dd))
                .flatMap(d ->  Arrays.stream(new Double[] {d[4], d[5]}))
                .map(dv -> dv.intValue())
                .max(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        Integer xOffs = minX < 0 ? Math.abs(minX) : 0;
        Integer yOffs = minY < 0 ? Math.abs(minY) : 0;
        Integer zOffs = minZ < 0 ? Math.abs(minZ) : 0;

        StringBuffer commonBuffer = new StringBuffer();
        commonBuffer.append("/* MOVE TO COMMON - START */" + LS);
        StringBuffer shaderCode = new StringBuffer();

        shaderCode.append("//fourier frames" + LS);
        shaderCode.append("#define FFRAMES " + fourierFrames + LS);
        shaderCode.append("//original number of frames sampled" + LS);
        shaderCode.append("#define OFS " + (frames + (loop ? 1 : 0)) + ".0" + LS);
        shaderCode.append("#define xOffs " + xOffs + ".0" + LS);
        shaderCode.append("#define yOffs " + yOffs + ".0" + LS);
        shaderCode.append("#define zOffs " + zOffs + ".0" + LS);
        shaderCode.append(LS);
        shaderCode.append("vec2 decode(uint d) {" + LS);
        shaderCode.append("    return vec2(d >> 16U, d & 0x0000FFFFU);" + LS);
        shaderCode.append("}" + LS);
        shaderCode.append(LS);
        shaderCode.append("vec3 posD(uint[FFRAMES] eX, uint[FFRAMES] eY, uint[FFRAMES] eZ, float h, vec2 U) {" + LS);
        shaderCode.append("    vec3 q = vec3(0.0);" + LS);
        shaderCode.append("    for (int k=0; k<FFRAMES; k++) {" +  LS);
        shaderCode.append("        float w = (k==0||k==(FFRAMES - 1)) ? 1.0 : 2.0;" + LS);
        shaderCode.append("        float an = -6.283185*float(k)*h;" + LS);
        shaderCode.append("        vec2 ex = vec2(cos(an), sin(an));" + LS);
        shaderCode.append("        q.x += w*dot(decode(eX[k]) - xOffs,ex)/OFS;" + LS);
        shaderCode.append("        q.y += w*dot(decode(eY[k]) - yOffs,ex)/OFS;" + LS);
        shaderCode.append("        q.z += w*dot(decode(eZ[k]) - zOffs,ex)/OFS;" + LS);
        shaderCode.append("    }" + LS);
        shaderCode.append("    if (iFrame>0) {" + LS);
        shaderCode.append("        q = mix(q,texelFetch(iChannel0, ivec2(U),0).xyz,0.7);" + LS);
        shaderCode.append("    }" + LS);
        shaderCode.append("    return q;" + LS);
        shaderCode.append("}" + LS);
        shaderCode.append(LS);
        shaderCode.append("void mainImage(out vec4 C, vec2 U) {" + LS);
        shaderCode.append(LS);
        shaderCode.append("    float h = mod(floor(T*50.), 100.) / 100.;" + LS);
        shaderCode.append("    uint eX[FFRAMES], eY[FFRAMES], eZ[FFRAMES];" + LS);
        shaderCode.append("    vec3 p = vec3(0);" + LS);
        shaderCode.append(LS);

        for (int i = 0; i < transform.length;  i++) {

            commonBuffer.append("#define J" + (i + 1) + " vec2(" + i + ".5, 0.5)" + System.lineSeparator());

            if (i == 0) {
                shaderCode.append("    if (U==J" + (i + 1) + ") {" + LS);
            } else {
                shaderCode.append("    else if (U==J" + (i + 1) + ") {" + LS);
            }

            Double[][] joint = transform[i];

            StringBuffer encBufferX = new StringBuffer();
            encBufferX.append("        eX = uint[" + joint.length + "] (");
            StringBuffer encBufferY = new StringBuffer();
            encBufferY.append("        eY = uint[" + joint.length + "] (");
            StringBuffer encBufferZ = new StringBuffer();
            encBufferZ.append("        eZ = uint[" + joint.length + "] (");

            for (int j = 0; j < joint.length; j++) {

                Double[] frame = joint[j];

                Integer x1 = frame[0].intValue() + xOffs;
                Integer x2 = frame[1].intValue() + xOffs;
                Integer y1 = frame[2].intValue() + yOffs;
                Integer y2 = frame[3].intValue() + yOffs;
                Integer z1 = frame[4].intValue() + zOffs;
                Integer z2 = frame[5].intValue() + zOffs;

                encBufferX.append("0x" + Integer.toHexString(encode(x1, x2)) + "U");
                encBufferY.append("0x" + Integer.toHexString(encode(y1, y2)) + "U");
                encBufferZ.append("0x" + Integer.toHexString(encode(z1, z2)) + "U");

                if (j < joint.length - 1) {
                    encBufferX.append(",");
                    encBufferY.append(",");
                    encBufferZ.append(",");
                }
            }
            encBufferX.append(");" + LS);
            encBufferY.append(");" + LS);
            encBufferZ.append(");" + LS);

            shaderCode.append(encBufferX.toString());
            shaderCode.append(encBufferY.toString());
            shaderCode.append(encBufferZ.toString());

            shaderCode.append("        vec3 q = posD(eX,eY,eZ,h,U);" + LS);
            shaderCode.append("        C = vec4(q.x,q.y,q.z,1.0);" + LS);
            shaderCode.append("    }" + LS);
        }

        shaderCode.append("}" + LS);

        commonBuffer.append("/* MOVE TO COMMON - END */" + System.lineSeparator());

        //*
        System.out.println("Generated Fourier");
        System.out.println("minX:" + minX);
        System.out.println("maxX:" + maxX);
        System.out.println("minY:" + minY);
        System.out.println("maxY:" + maxY);
        System.out.println("minZ:" + minZ);
        System.out.println("maxZ:" + maxZ);
        //*/

        return commonBuffer.append(shaderCode);
    }
}
