package com.eric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen implements Screen {

    private final Main game;
    private final GameScreen parent;

    private Stage stage;
    private Skin skin;
    private Texture whitePixel;

    public PauseScreen(GameScreen parent) {
        this.game = Main.INSTANCE;
        this.parent = parent;
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();

        Label title = new Label("Game Menu", skin);
        title.setFontScale(1.5f);

        TextButton quit = new TextButton("Save and Quit", skin);
        TextButton returnToGame = new TextButton("Back to Game", skin);

        quit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { onQuit(); }
        });
        returnToGame.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { onReturn(); }
        });

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(title).padBottom(40).row();
        root.add(quit).width(200).padBottom(20).row();
        root.add(returnToGame).width(200).padBottom(20).row();
    }

    private void onQuit() {
        Gdx.app.exit();
    }

    private void onReturn() {
        // Switch back to the SAME GameScreen instance — preserves level, player, camera.
        game.setScreen(parent);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().setColor(0f, 0f, 0f, 0.6f);
        stage.getBatch().draw(whitePixel, 0, 0,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight());
        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        whitePixel.dispose();
    }
}
