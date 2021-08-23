package com.shau.mocap.fourier;

import com.shau.mocap.MoCapFileHandler;
import com.shau.mocap.util.BinaryHelper;
import com.shau.mocap.domain.Joint;
import com.shau.mocap.domain.MoCapScene;

import java.util.List;
import java.util.stream.Collectors;

public class FourierGenerator {

    public static void generateFourier(MoCapScene moCapScene,
                                            int startFrame,
                                            int endFrame,
                                            int fourierFrames,
                                            boolean loop,
                                            boolean useLowResolution,
                                            int cutoff) {

        int frames = endFrame - startFrame;
        int allJoints = moCapScene.getFrames().get(startFrame).getJoints().size();
        //displayed joints
        int displayedJoints = moCapScene.getFrames().get(startFrame).getJoints().stream()
                .filter(j -> j.isDisplay())
                .collect(Collectors.toList()).size();

        //fourier transform
        Double[][][] transform = new Double[displayedJoints][fourierFrames][6];
        int jointCount = 0;
        for (int i = 0;  i < allJoints; i++) {
            int index = i;
            List<Joint> fourierJoints = moCapScene.getFrames().stream()
                    .skip(startFrame)
                    .limit(frames)
                    .map(f -> f.getJoints().get(index))
                    .collect(Collectors.toList());
            if (loop) {
                fourierJoints.add(fourierJoints.get(0)); //add first joint of frame to end of list when looping
            }

            if (fourierJoints.get(0).isDisplay()) {
                transform[jointCount++] = calculateFourier(fourierJoints,
                        fourierFrames,
                        moCapScene.getSpatialOffset().getOffsetJointId());
            }
        }

        StringBuffer shaderBuffer = generateFourierOutput(transform, frames, fourierFrames, useLowResolution, cutoff);
        MoCapFileHandler.saveShaderOutput(moCapScene.getFilename(), shaderBuffer);
    }

    private static Double[][] calculateFourier(List<Joint> joints,
                                               int fourierFrames,
                                               Integer centerJointId) {

        Double[][] fcs = new Double[fourierFrames][6];
        int frames = joints.size();
        Joint centerJoint = centerJointId == null ? null : joints.get(centerJointId - 1);

        for (int k = 0;  k < fourierFrames; k++) {
            Double[] fc = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
            for (int i = 0; i < frames; i++) {
                Joint joint = joints.get(i);

                Double xPos = joint.getX();
                Double yPos = joint.getY();
                Double zPos = joint.getZ();
                if (centerJoint != null) {
                    xPos -= centerJoint.getX();
                    //yPos -= centerJoint.getY(); //walking?
                    zPos -= centerJoint.getZ();
                }

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
                //min x
                if (d[0] < minX)
                    minX = d[0].intValue();
                if (d[1] < minX)
                    minX = d[1].intValue();
                //max x
                if (d[0] >  maxX)
                    maxX = d[0].intValue();
                if (d[1]  > maxX)
                    maxX = d[1].intValue();
                //min y
                if (d[2] < minY)
                    minY = d[2].intValue();
                if (d[3] < minY)
                    minY = d[3].intValue();
                //max y
                if (d[2] > maxY)
                   maxY = d[2].intValue();
                if (d[3]  > maxY)
                    maxY = d[3].intValue();
                //min z
                if (d[4] < minZ)
                    minZ = d[4].intValue();
                if (d[5] < minZ)
                    minZ = d[5].intValue();
                //max z
                if (d[4] > maxZ)
                    maxZ = d[4].intValue();
                if (d[5] > maxZ)
                    maxZ = d[5].intValue();
            }
        }

        //push negative fourier values to zero
        Integer xOffs = minX < 0 ? Math.abs(minX) : 0;
        Integer yOffs = minY < 0 ? Math.abs(minY) : 0;
        Integer zOffs = minZ < 0 ? Math.abs(minZ) : 0;

        //maximum deviation in fourier data required for low resolution data
        int maxDeviationX = maxX - minX;
        int maxDeviationY = maxY - minY;
        int maxDeviationZ = maxZ - minZ;

        //low res scaling
        float lowResScaleEncodeX = 255.0f / (float) maxDeviationX;
        float lowResScaleDecodeX = (float) maxDeviationX / 255.0f;
        float lowResScaleEncodeY = 255.0f / (float) maxDeviationY;
        float lowResScaleDecodeY = (float) maxDeviationY / 255.0f;
        float lowResScaleEncodeZ = 255.0f / (float) maxDeviationZ;
        float lowResScaleDecodeZ = (float) maxDeviationZ / 255.0f;

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
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],0U)*lowResScaleDecodeX - xOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],0U)*lowResScaleDecodeY - yOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],0U)*lowResScaleDecodeZ - zOffs,ex)/OFS;" + LS);
            shaderCode.append("        fourierIndex++;" + LS);
            shaderCode.append(LS);
            shaderCode.append("        w = fourierIndex < (FFRAMES + FFRAMES_LOW_RES*2) - 1 ? 1.0 : 2.0;" + LS);
            shaderCode.append("        an = -6.283185*float(fourierIndex)*h;" + LS);
            shaderCode.append("        ex = vec2(cos(an), sin(an));" + LS);
            shaderCode.append("        q.x += w*dot(decode8bit(eXLowRes[k],16U)*lowResScaleDecodeX - xOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.y += w*dot(decode8bit(eYLowRes[k],16U)*lowResScaleDecodeY - yOffs,ex)/OFS;" + LS);
            shaderCode.append("        q.z += w*dot(decode8bit(eZLowRes[k],16U)*lowResScaleDecodeZ - zOffs,ex)/OFS;" + LS);
            shaderCode.append("        fourierIndex++;" + LS);
            shaderCode.append("    }" + LS);
            shaderCode.append("    return q;" + LS);
            shaderCode.append("}" + LS);
            shaderCode.append(LS);
        }
        shaderCode.append("void mainImage(out vec4 C, vec2 U) {" + LS);
        shaderCode.append(LS);
        shaderCode.append("    float h = mod(floor(T*50.), 100.) / 100.;" + LS);
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

                for (int j = 0; j < lowResJointLength; j++) {
                    if (j % 2 == 0) {
                        Double[] frame1 = joint[j + cutoff];
                        Double[] frame2 = joint[j + 1 + cutoff];

                        //offset to zero base data
                        //scale low res data
                        Integer f1x1 = (int) ((frame1[0] + xOffs) * lowResScaleEncodeX);
                        Integer f1x2 = (int) ((frame1[1] + xOffs) * lowResScaleEncodeX);
                        Integer f1y1 = (int) ((frame1[2] + yOffs) * lowResScaleEncodeY);
                        Integer f1y2 = (int) ((frame1[3] + yOffs) * lowResScaleEncodeY);
                        Integer f1z1 = (int) ((frame1[4] + zOffs) * lowResScaleEncodeZ);
                        Integer f1z2 = (int) ((frame1[5] + zOffs) * lowResScaleEncodeZ);
                        Integer f2x1 = (int) ((frame2[0] + xOffs) * lowResScaleEncodeX);
                        Integer f2x2 = (int) ((frame2[1] + xOffs) * lowResScaleEncodeX);
                        Integer f2y1 = (int) ((frame2[2] + yOffs) * lowResScaleEncodeY);
                        Integer f2y2 = (int) ((frame2[3] + yOffs) * lowResScaleEncodeY);
                        Integer f2z1 = (int) ((frame2[4] + zOffs) * lowResScaleEncodeZ);
                        Integer f2z2 = (int) ((frame2[5] + zOffs) * lowResScaleEncodeZ);

                        encBufferX.append("0x" + Integer.toHexString(BinaryHelper.encode(f1x1, f1x2, f2x1, f2x2)) + "U");
                        encBufferY.append("0x" + Integer.toHexString(BinaryHelper.encode(f1y1, f1y2, f2y1, f2y2)) + "U");
                        encBufferZ.append("0x" + Integer.toHexString(BinaryHelper.encode(f1z1, f1z2, f2z1, f2z2)) + "U");

                        if (j < lowResJointLength - 2) {
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
        //*/

        return commonBuffer.append(shaderCode);
    }
}
