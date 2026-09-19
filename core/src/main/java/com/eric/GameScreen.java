package com.eric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.eric.level.Level;
import com.eric.level.LevelRenderer;

public class GameScreen implements Screen {
    private static final float FOG_R = 0.5f;
    private static final float FOG_G = 0.8f;
    private static final float FOG_B = 1.0f;

    private final ShaderProgram shader;
    private final Level level;
    private final LevelRenderer levelRenderer;
    private final Player player;
    private HitResult hitResult = null;
    private final Timer timer = new Timer(60.0f);

    public GameScreen(ShaderProgram shader) {
        this.shader = shader;

        Gdx.gl.glClearColor(FOG_R, FOG_G, FOG_B, 1f);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

        this.level = new Level(256, 256, 64);
        this.levelRenderer = new LevelRenderer(level);
        this.player = new Player(level);

        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render(float delta) {
        if (player.y < -64) {
            player.resetPos();
        }

        // --- input ---
        if (Gdx.input.isCursorCatched()) {
            player.turn(-Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && hitResult != null) {
            level.setTile(hitResult.x, hitResult.y, hitResult.z, 0);
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && hitResult != null) {
            int x = hitResult.x, y = hitResult.y, z = hitResult.z;
            if (hitResult.f == 0) y--;
            if (hitResult.f == 1) y++;
            if (hitResult.f == 2) z--;
            if (hitResult.f == 3) z++;
            if (hitResult.f == 4) x--;
            if (hitResult.f == 5) x++;
            level.setTile(x, y, z, 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.INSTANCE.setScreen(new PauseScreen(this));
            return;   // stop rendering this frame; the new screen takes over
        }

        // --- fixed-step physics ---
        timer.advanceTime();
        for (int i = 0; i < timer.ticks; i++) {
            player.tick();
        }

        // --- camera updates once per frame, interpolated ---
        player.updateCamera(timer.a);

        // --- picking uses the fresh camera ---
        hitResult = levelRenderer.pick(player);

        // --- clear ---
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // --- shader uniforms ---
        shader.bind();
        shader.setUniformMatrix("u_projTrans", player.camera.combined);
        shader.setUniformi("u_texture", 0);

        Gdx.gl.glClearColor(0.5f, 0.8f, 1.0f, 1f);   // sky color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // --- draw ---
        levelRenderer.render(player, 0, shader);
        levelRenderer.render(player, 1, shader);
        levelRenderer.renderHit(hitResult, shader);
    }

    @Override public void resize(int w, int h) {
        player.camera.viewportWidth = w;
        player.camera.viewportHeight = h;
        player.camera.update();
    }
    @Override public void show() { Gdx.input.setCursorCatched(true); }
    @Override public void hide() { Gdx.input.setCursorCatched(false); }
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        levelRenderer.dispose();
    }
}
