package com.eric;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Main extends Game {
    public ShaderProgram shader;

    public static Main INSTANCE;

    @Override
    public void create() {
        INSTANCE = this;

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
        super.dispose();       // disposes the current screen
        if (shader != null) shader.dispose();
        Textures.dispose();
    }
}
