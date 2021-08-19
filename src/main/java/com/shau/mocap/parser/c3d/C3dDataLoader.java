package com.shau.mocap.parser.c3d;

import java.io.FileInputStream;
import java.io.IOException;

public class C3dDataLoader {

    public static byte[] loadHeaderBlock(FileInputStream fis) throws IOException {
        byte[] headerBlock = new byte[512];
        fis.getChannel().position(0);
        fis.read(headerBlock);
        return headerBlock;
    }

    public static byte[] loadParameterMetadata(FileInputStream fis, int startPosition) throws IOException {
        byte[] parameterMetadataBlock = new byte[4];
        fis.getChannel().position(512 * (startPosition - 1));
        fis.read(parameterMetadataBlock);
        return parameterMetadataBlock;
    }

    public static byte[] loadParameters(FileInputStream fis, int startPosition, int numberOfParameters)
            throws IOException {

        byte[] parametersBlock = new byte[512 * numberOfParameters];
        fis.getChannel().position(512 * (startPosition - 1));
        fis.read(parametersBlock);
        return parametersBlock;
    }

    public static byte[] loadData(FileInputStream fis, C3dValueMetadata metadata) throws IOException {
        byte[] dataBlock = new byte[512 * metadata.getBlocks()];
        fis.getChannel().position(512 * (metadata.getDataStart() - 1));
        fis.read(dataBlock);
        return dataBlock;
    }
}
