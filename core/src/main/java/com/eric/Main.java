package com.eric;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Main extends Game {
    public ShaderProgram shader;

    public static Main INSTANCE;
    private Version versionManager;

    private final String VER = "v1.0.1";

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);
        INSTANCE = this;
        versionManager = new Version(VER);

        shader = new ShaderProgram(
            Gdx.files.internal("shaders/default.vert"),
            Gdx.files.internal("shaders/default.frag")
        );
        if (!shader.isCompiled()) {
            throw new RuntimeException("Shader compile failed:\n" + shader.getLog());
        }
        setScreen(new GameScreen(shader));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shader != null) shader.dispose();
        Textures.dispose();
    }

    public Version getVersionManager() {
        return versionManager;
    }

    public static class Version {
        private String FULLVERSION;
        private String MAJOR;
        private String MINOR;
        private String PATCH;

        public Version(String VERSION) {
            FULLVERSION = VERSION;

            if (!VERSION.startsWith("v"))
                parseVerNoV(VERSION);
            else
                parseVer(VERSION);
        }

        private void parseVerNoV(String ver) {
            String[] var = ver.split("\\.");
            if (var.length != 3)
                return;

            MAJOR = var[0];
            MINOR = var[1];
            PATCH = var[2];
        }

        private void parseVer(String ver) {
            String var = ver.substring(1);
            parseVerNoV(var);
        }

        public String getVersion() {
            return FULLVERSION;
        }

        public String getMajor() {
            return MAJOR;
        }

        public String getMinor() {
            return MINOR;
        }

        public String getPatch() {
            return PATCH;
        }
    }
}
