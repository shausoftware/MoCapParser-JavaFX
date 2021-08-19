package com.shau.mocap;

import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.exception.MoCapFileHandlerException;

import java.io.*;

public class MoCapFileHandler {

    public static MoCapScene loadSceneData(String fileName) throws MoCapFileHandlerException {
        try {
            FileInputStream fi = new FileInputStream(new File(fileName));
            ObjectInputStream oi = new ObjectInputStream(fi);
            return (MoCapScene) oi.readObject();
        } catch (Exception e) {
            throw new MoCapFileHandlerException("Load scene data error: " + e.getMessage());
        }
    }

    public static void saveSceneData(MoCapScene moCapScene) throws MoCapFileHandlerException {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(moCapScene.getFilePath()));
            objectOutputStream.writeObject(moCapScene);
            objectOutputStream.flush();
            objectOutputStream.close();
            System.out.println("Saved Scene Data");
        } catch (Exception e) {
            throw new MoCapFileHandlerException("Save scene data error: " + e.getMessage());
        }
    }

    public static void saveShaderOutput(String filename, StringBuffer shaderBuffer) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename + ".shader")));
            bw.write(shaderBuffer.toString());
            bw.flush();
            bw.close();
            System.out.println("Saved Shader Output");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
