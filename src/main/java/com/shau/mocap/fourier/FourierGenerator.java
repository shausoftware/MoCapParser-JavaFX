package com.shau.mocap.fourier;

import com.shau.mocap.MoCapFileHandler;
import com.shau.mocap.domain.Frame;
import com.shau.mocap.domain.SpatialOffset;
import com.shau.mocap.util.BinaryHelper;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FourierGenerator {

    public static void generateFourier(MoCapScene moCapScene,
                                            int startFrame,
                                            int endFrame,
                                            double scale,
                                            int fourierFrames,
                                            boolean loop,
                                            int easingFrames,
                                            boolean useLowResolution,
                                            int cutoff) {

        //pre-processing
        List<Frame> processFrames = preProcess(moCapScene, scale);
        //loop easing

        int dataFramesSize = endFrame - startFrame;
        int allJointsSize = processFrames.get(startFrame).getJoints().size();
        //displayed joints
        int displayedJointsSize = processFrames.get(startFrame).getJoints().stream()
                .filter(j -> j.isDisplay())
                .collect(Collectors.toList()).size();

        //fourier transform
        Double[][][] transform = new Double[displayedJointsSize][fourierFrames][6];
        int jointCount = 0;
        for (int i = 0;  i < allJointsSize; i++) {
            int index = i;
            List<Joint> fourierJoints = processFrames.stream()
                    .skip(startFrame)
                    .limit(dataFramesSize)
                    .map(f -> f.getJoints().get(index))
                    .collect(Collectors.toList());
            //if (loop) {
            //    fourierJoints.add(fourierJoints.get(0)); //add first joint of frame to end of list when looping
            //}

            if (fourierJoints.get(0).isDisplay()) {
                transform[jointCount++] = calculateFourier(fourierJoints, fourierFrames);
            }
        }

        StringBuffer shaderBuffer = generateFourierOutput(transform,
                dataFramesSize,
                fourierFrames,
                useLowResolution,
                cutoff);
        MoCapFileHandler.saveShaderOutput(moCapScene.getFilename(), shaderBuffer);
    }

    private static List<Frame> preProcess(MoCapScene moCapScene, double scale) {
        List<Frame> processFrames = new ArrayList<>();
        SpatialOffset spatialOffset = moCapScene.getSpatialOffset();
        for (Frame frame : moCapScene.getFrames()) {
            double offsetX = 0.0;
            double offsetY = 0.0;
            double offsetZ = 0.0;
            if (spatialOffset.getOffsetMode() == SpatialOffset.OFFSET_JOINT) {
                Joint centerJoint = frame.getJoints().get(spatialOffset.getOffsetJointId() - 1);
                offsetX = centerJoint.getX();
                offsetY = centerJoint.getY();
                offsetZ = centerJoint.getZ();
            } else if (spatialOffset.getOffsetMode() == SpatialOffset.OFFSET_XYZ) {
                offsetX = spatialOffset.getOffsetPointX();
                offsetY = spatialOffset.getOffsetPointY();
                offsetZ = spatialOffset.getOffsetPointZ();
            }

            List<Joint> processJoints = new ArrayList<>();
            for (Joint joint : frame.getJoints()) {
                Joint processJoint = new Joint(joint.getId(),
                        (joint.getX() - offsetX) * scale,
                        (joint.getY() - offsetY) * scale,
                        (joint.getZ() - offsetZ) * scale);
                processJoint.updateDisplayState(joint.getColour(), joint.isDisplay());
                processJoints.add(processJoint);
            }
            processFrames.add(new Frame(frame.getId(), processJoints));
        }
        return processFrames;
    }

    private static Double[][] calculateFourier(List<Joint> joints, int fourierFrames) {

        Double[][] fcs = new Double[fourierFrames][6];
        int frames = joints.size();

        for (int k = 0;  k < fourierFrames; k++) {
            Double[] fc = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < frames; i++) {
                Joint joint = joints.get(i);
                Double xPos = joint.getX();
                Double yPos = joint.getY();
                Double zPos = joint.getZ();

                Double an = -6.283185 * Double.valueOf(k) * Double.valueOf(i) / Double.valueOf(frames);
                Double[] ex = new Double[] {Math.cos(an), Math.sin(an)};
                fc[0] += xPos * ex[0];
                fc[1] += xPos * ex[1];
                fc[2] += yPos * ex[0];
                fc[3] += yPos * ex[1];
                fc[4] += zPos * ex[0];
                fc[5] += zPos * ex[1];
            }
            fcs[k] = fc;
        }

        return fcs;
    }

    private static  StringBuffer generateFourierOutput(Double[][][] transform,
                                          int frames,
                                          int fourierFrames,
                                          boolean useLowResolution,
                                          int cutoff) {

        /* OUTPUT */
        /* Change to suit your needs - this implementation is about as bad as it gets */

        String LS = System.lineSeparator();

        int minX = Integer.MAX_VALUE;
        int maxX = -Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = -Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = -Integer.MAX_VALUE;

        for (int i = 0; i < transform.length; i++) {
            Double[][] dd = transform[i];
            for (int j = 0; j < dd.length; j++) {
                Double[] d = dd[j];
                minX = lower(minX, d[0]);
                minX = lower(minX, d[1]);
                maxX = higher(maxX, d[0]);
                maxX = higher(maxX, d[1]);
                minY = lower(minY, d[2]);
                minY = lower(minY, d[3]);
                maxY = higher(maxY, d[2]);
                maxY = higher(maxY, d[3]);
                minZ = lower(minZ, d[4]);
                minZ = lower(minZ, d[5]);
                maxZ = higher(maxZ, d[4]);
                maxZ = higher(maxZ, d[5]);
            }
        }

        //push negative fourier values to zero
        Integer xOffs = minX < 0 ? Math.abs(minX) : 0;
        Integer yOffs = minY < 0 ? Math.abs(minY) : 0;
        Integer zOffs = minZ < 0 ? Math.abs(minZ) : 0;

        int lowResMinX = Integer.MAX_VALUE;
        int lowResMaxX = -Integer.MAX_VALUE;
        int lowResMinY = Integer.MAX_VALUE;
        int lowResMaxY = -Integer.MAX_VALUE;
        int lowResMinZ = Integer.MAX_VALUE;
        int lowResMaxZ = -Integer.MAX_VALUE;

        for (int i = 0; i < transform.length; i++) {
            Double[][] dd = transform[i];
            for (int j = cutoff; j < dd.length; j++) {
                Double[] d = dd[j];
                lowResMinX = lower(lowResMinX, d[0] + xOffs);
                lowResMinX = lower(lowResMinX, d[1] + xOffs);
                lowResMaxX = higher(lowResMaxX, d[0] + xOffs);
                lowResMaxX = higher(lowResMaxX, d[1] + xOffs);
                lowResMinY = lower(lowResMinY, d[2] + yOffs);
                lowResMinY = lower(lowResMinY, d[3] + yOffs);
                lowResMaxY = higher(lowResMaxY, d[2] + yOffs);
                lowResMaxY = higher(lowResMaxY, d[3] + yOffs);
                lowResMinZ = lower(lowResMinZ, d[4] + zOffs);
                lowResMinZ = lower(lowResMinZ, d[5] + zOffs);
                lowResMaxZ = higher(lowResMaxZ, d[4] + zOffs);
                lowResMaxZ = higher(lowResMaxZ, d[5] + zOffs);
            }
        }

        //maximum deviation in fourier data required for low resolution data
        int maxLowResDevX = lowResMaxX - lowResMinX;
        int maxLowResDevY = lowResMaxY - lowResMinY;
        int maxLowResDevZ = lowResMaxZ - lowResMinZ;

        //low res scaling
        float lowResScaleEncodeX = maxLowResDevX < 255 ? 1.0f : 255.0f / (float) maxLowResDevX;
        float lowResScaleDecodeX = maxLowResDevX < 255 ? 1.0f : (float) maxLowResDevX / 255.0f;
        float lowResScaleEncodeY = maxLowResDevY < 255 ? 1.0f : 255.0f / (float) maxLowResDevY;
        float lowResScaleDecodeY = maxLowResDevY < 255 ? 1.0f : (float) maxLowResDevY / 255.0f;
        float lowResScaleEncodeZ = maxLowResDevZ < 255 ? 1.0f : 255.0f / (float) maxLowResDevZ;
        float lowResScaleDecodeZ = maxLowResDevZ < 255 ? 1.0f : (float) maxLowResDevZ / 255.0f;

        StringBuffer commonBuffer = new StringBuffer();
        commonBuffer.append("/* MOVE TO COMMON - START */" + LS);
        StringBuffer shaderCode = new StringBuffer();
        shaderCode.append("//fourier frames" + LS);
        if (useLowResolution) {
            shaderCode.append("#define FFRAMES " + cutoff + LS);
            shaderCode.append("#define FFRAMES_LOW_RES " + ((fourierFrames - cutoff) / 2) + LS);
        } else {
            shaderCode.append("#define FFRAMES " + fourierFrames + LS);
        }
        shaderCode.append("//original number of frames sampled" + LS);
        shaderCode.append("#define OFS " + frames + ".0" + LS);
        shaderCode.append("#define xOffs " + xOffs + ".0" + LS);
        shaderCode.append("#define yOffs " + yOffs + ".0" + LS);
        shaderCode.append("#define zOffs " + zOffs + ".0" + LS);
        if (useLowResolution) {
            shaderCode.append("#define lowResXOffs " + lowResMinX + ".0" + LS);
            shaderCode.append("#define lowResYOffs " + lowResMinY + ".0" + LS);
            shaderCode.append("#define lowResZOffs " + lowResMinZ + ".0" + LS);
            shaderCode.append("#define lowResScaleDecodeX " + lowResScaleDecodeX + LS);
            shaderCode.append("#define lowResScaleDecodeY " + lowResScaleDecodeY + LS);
            shaderCode.append("#define lowResScaleDecodeZ " + lowResScaleDecodeZ + LS);
        }
        shaderCode.append(LS);
        shaderCode.append("vec2 decode16bit(uint d) {" + LS);
        shaderCode.append("    return vec2(d >> 16U, d & 0x0000FFFFU);" + LS);
        shaderCode.append("}" + LS);
        shaderCode.append(LS);
        if (useLowResolution) {
            shaderCode.append("vec2 decode8bit(uint d, uint offset) {" + LS);
            shaderCode.append("    return vec2((d >> offset) & 0xFFU, (d >> offset + 8U) & 0xFFU);" + LS);
            shaderCode.append("}" + LS);
            shaderCode.append(LS);
        }
        shaderCode.append("vec3 posD(uint[FFRAMES] eX, uint[FFRAMES] eY, uint[FFRAMES] eZ, float h, vec2 U) {" + LS);
        shaderCode.append("    vec3 q = vec3(0.0);" + LS);
        shaderCode.append("    for (int k=0; k<FFRAMES; k++) {" +  LS);
        if (useLowResolution) {
            shaderCode.append("        float w = (k==0) ? 1.0 : 2.0;" + LS);
        } else {
            shaderCode.append("        float w = (k==0||k==(FFRAMES - 1)) ? 1.0 : 2.0;" + LS);
        }
        shaderCode.append("        float an = -6.283185*float(k)*h;" + LS);
        shaderCode.append("        vec2 ex = vec2(cos(an), sin(an));" + LS);
        shaderCode.append("        q.x += w*dot(decode16bit(eX[k]) - xOffs,ex)/OFS;" + LS);
        shaderCode.append("        q.y += w*dot(decode16bit(eY[k]) - yOffs,ex)/OFS;" + LS);
        shaderCode.append("        q.z += w*dot(decode16bit(eZ[k]) - zOffs,ex)/OFS;" + LS);
        shaderCode.append("    }" + LS);
        shaderCode.append("    return q;" + LS);
        shaderCode.append("}" + LS);
        shaderCode.append(LS);
        if (useLowResolution) {
            shaderCode.append("vec3 posD_LowRes(uint[FFRAMES_LOW_RES] eXLowRes, uint[FFRAMES_LOW_RES] eYLowRes, uint[FFRAMES_LOW_RES] eZLowRes, float h, vec2 U) {" + LS);
            shaderCode.append("    vec3 q = vec3(0.0);" + LS);
            shaderCode.append("    int fourierIndex = FFRAMES;" + LS);
            shaderCode.append("    for (int k=0; k<FFRAMES_LOW_RES; k++) {" +  LS);
            shaderCode.append("        float w = 2.0;" + LS);
            shaderCode.append("        float an = -6.283185*float(fourierIndex)*h;" + LS);
            shaderCode.append("        vec2 ex = vec2(cos(an), sin(an));" + LS);
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],0U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],0U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],0U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;" + LS);
            shaderCode.append("        fourierIndex++;" + LS);
            shaderCode.append(LS);
            shaderCode.append("        w = fourierIndex < (FFRAMES + FFRAMES_LOW_RES*2) - 1 ? 1.0 : 2.0;" + LS);
            shaderCode.append("        an = -6.283185*float(fourierIndex)*h;" + LS);
            shaderCode.append("        ex = vec2(cos(an), sin(an));" + LS);
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],16U)*lowResScaleDecodeX + lowResXOffs - xOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],16U)*lowResScaleDecodeY + lowResYOffs - yOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],16U)*lowResScaleDecodeZ + lowResZOffs - zOffs,ex)/OFS;" + LS);
            shaderCode.append("        fourierIndex++;" + LS);
            shaderCode.append("    }" + LS);
            shaderCode.append("    return q;" + LS);
            shaderCode.append("}" + LS);
            shaderCode.append(LS);
        }
        shaderCode.append("void mainImage(out vec4 C, vec2 U) {" + LS);
        shaderCode.append(LS);
        shaderCode.append("    float h = mod(floor(T*8.), 100.) / 100.;" + LS);
        shaderCode.append("    uint eX[FFRAMES], eY[FFRAMES], eZ[FFRAMES];" + LS);
        if (useLowResolution) {
            shaderCode.append("    uint eXLowRes[FFRAMES_LOW_RES], eYLowRes[FFRAMES_LOW_RES], eZLowRes[FFRAMES_LOW_RES];" + LS);
        }
        shaderCode.append(LS);

        for (int i = 0; i < transform.length;  i++) {

            commonBuffer.append("#define J" + (i + 1) + " vec2(" + i + ".5, 0.5)" + System.lineSeparator());

            if (i == 0) {
                shaderCode.append("    if (U==J" + (i + 1) + ") {" + LS);
            } else {
                shaderCode.append("    else if (U==J" + (i + 1) + ") {" + LS);
            }

            Double[][] joint = transform[i];
            int hiResJointLength = useLowResolution ? cutoff : joint.length;
            int lowResJointLength = useLowResolution ? joint.length - cutoff : 0;

            StringBuffer encBufferX = new StringBuffer();
            encBufferX.append("        eX = uint[" + hiResJointLength + "] (");
            StringBuffer encBufferY = new StringBuffer();
            encBufferY.append("        eY = uint[" + hiResJointLength + "] (");
            StringBuffer encBufferZ = new StringBuffer();
            encBufferZ.append("        eZ = uint[" + hiResJointLength + "] (");

            //hi resolution joint data
            for (int j = 0; j < hiResJointLength; j++) {

                Double[] frame = joint[j];

                //offset to zero base data
                Integer x1 = frame[0].intValue() + xOffs;
                Integer x2 = frame[1].intValue() + xOffs;
                Integer y1 = frame[2].intValue() + yOffs;
                Integer y2 = frame[3].intValue() + yOffs;
                Integer z1 = frame[4].intValue() + zOffs;
                Integer z2 = frame[5].intValue() + zOffs;

                encBufferX.append("0x" + Integer.toHexString(BinaryHelper.encode(x1, x2)) + "U");
                encBufferY.append("0x" + Integer.toHexString(BinaryHelper.encode(y1, y2)) + "U");
                encBufferZ.append("0x" + Integer.toHexString(BinaryHelper.encode(z1, z2)) + "U");

                if (j < hiResJointLength - 1) {
                    encBufferX.append(",");
                    encBufferY.append(",");
                    encBufferZ.append(",");
                }
            }

            encBufferX.append(");" + LS);
            encBufferY.append(");" + LS);
            encBufferZ.append(");" + LS);

            //low resolution joint data
            if (useLowResolution) {
                encBufferX.append("        eXLowRes = uint[" + (lowResJointLength / 2) + "] (");
                encBufferY.append("        eYLowRes = uint[" + (lowResJointLength / 2) + "] (");
                encBufferZ.append("        eZLowRes = uint[" + (lowResJointLength / 2) + "] (");

                for (int k = 0; k < lowResJointLength; k++) {
                    if (k % 2 == 1) {

                        Double[] frame1 = joint[k - 1 + cutoff];
                        Double[] frame2 = joint[k + cutoff];

                        Integer f1x1 = (int) ((frame1[0].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        Integer f1x2 = (int) ((frame1[1].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        Integer f1y1 = (int) ((frame1[2].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        Integer f1y2 = (int) ((frame1[3].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        Integer f1z1 = (int) ((frame1[4].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        Integer f1z2 = (int) ((frame1[5].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        Integer f2x1 = (int) ((frame2[0].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        Integer f2x2 = (int) ((frame2[1].intValue() + xOffs - lowResMinX) * lowResScaleEncodeX);
                        Integer f2y1 = (int) ((frame2[2].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        Integer f2y2 = (int) ((frame2[3].intValue() + yOffs - lowResMinY) * lowResScaleEncodeY);
                        Integer f2z1 = (int) ((frame2[4].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);
                        Integer f2z2 = (int) ((frame2[5].intValue() + zOffs - lowResMinZ) * lowResScaleEncodeZ);

                        encBufferX.append("0x" + Integer.toHexString(BinaryHelper.encode(f1x1, f1x2, f2x1, f2x2)) + "U");
                        encBufferY.append("0x" + Integer.toHexString(BinaryHelper.encode(f1y1, f1y2, f2y1, f2y2)) + "U");
                        encBufferZ.append("0x" + Integer.toHexString(BinaryHelper.encode(f1z1, f1z2, f2z1, f2z2)) + "U");

                        if (k < lowResJointLength - 1) {
                            encBufferX.append(",");
                            encBufferY.append(",");
                            encBufferZ.append(",");
                        }
                    }
                }

                encBufferX.append(");" + LS);
                encBufferY.append(");" + LS);
                encBufferZ.append(");" + LS);
            }

            shaderCode.append(encBufferX);
            shaderCode.append(encBufferY);
            shaderCode.append(encBufferZ);
            shaderCode.append("    }" + LS);
        }

        shaderCode.append("    vec3 q = posD(eX,eY,eZ,h,U);" + LS);
        if (useLowResolution) {
            shaderCode.append("    q += posD_LowRes(eXLowRes,eYLowRes,eZLowRes,h,U);" + LS);
        }
        shaderCode.append("    if (iFrame>0) {" + LS);
        shaderCode.append("        q = mix(q,texelFetch(iChannel0, ivec2(U),0).xyz,0.7);" + LS);
        shaderCode.append("    }" + LS);

        shaderCode.append("    C = vec4(q.x,q.y,q.z,1.0);" + LS);

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
        System.out.println("max low res x:" + maxLowResDevX);
        System.out.println("max low res y:" + maxLowResDevY);
        System.out.println("max low res z:" + maxLowResDevZ);
        System.out.println("lowResMinX:" + lowResMinX);
        System.out.println("lowResMaxX:" + lowResMaxX);
        System.out.println("lowResMinY:" + lowResMinY);
        System.out.println("lowResMaxY:" + lowResMaxY);
        System.out.println("lowResMinZ:" + lowResMinZ);
        System.out.println("lowResMaxZ:" + lowResMaxZ);
        //*/

        return commonBuffer.append(shaderCode);
    }

    private static int higher(int cMax, Double val) {
        if (val > cMax)
            return val.intValue();
        return cMax;
    }

    private static int lower(int cMin, Double val) {
        if (val < cMin)
            return val.intValue();
        return cMin;
    }
}
