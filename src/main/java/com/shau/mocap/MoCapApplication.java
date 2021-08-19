package com.shau.mocap;

import com.shau.mocap.domain.MoCapScene;
import com.shau.mocap.parser.MoCapParser;
import com.shau.mocap.ui.MoCapUIApp;

public class MoCapApplication {

    public static void main(String[] args) {

        try {
            if (args[0].equals("View") && args.length == 2) {
                MoCapUIApp app = new MoCapUIApp();
                app.openScene(args);
            } else if (args[0].equals("Parse")) {
                if (args.length == 4) {
                    int start = Integer.parseInt(args[2]);
                    int end = Integer.parseInt(args[3]);
                    MoCapScene moCapScene = MoCapParser.parse(args[1], start,  end);
                    MoCapFileHandler.saveSceneData(moCapScene);
                } else if (args.length == 2) {
                    MoCapScene moCapScene = MoCapParser.parse(args[1]);
                    MoCapFileHandler.saveSceneData(moCapScene);
                } else {
                    throw new IllegalArgumentException("Invalid parse arguments. Exiting.");
                }
            } else if (args[0].equals("Analyse")) {
                MoCapScene moCapScene = MoCapParser.parse(args[1]);
                moCapScene.displayProperties();
            } else {
                throw new IllegalArgumentException("Invalid application arguments. Exiting.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
