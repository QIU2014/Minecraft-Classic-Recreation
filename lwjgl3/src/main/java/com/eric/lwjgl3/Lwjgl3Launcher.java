package com.eric.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.eric.Main;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setWindowIcon("icon_16x16.png", "icon_32x32.png", "icon_48x48.png", "icon_128x128.png", "icon_256x256.png");
        cfg.setTitle("Minecraft");
        cfg.setWindowedMode(1024, 768);
        cfg.useVsync(true);
        cfg.setForegroundFPS(60);
        new Lwjgl3Application(new Main(), cfg);
    }
}
