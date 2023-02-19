package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InitialScreen implements Screen {
    private final SteinItUpGame steinItUpGame;
    private SpriteBatch batch;
    private AssetsLoader assetsLoader;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Stage stage;
    private Skin skin;

    private Button btnLogin, btnRegister;
    private float bgWidth, bgHeight;
    private final int btnWidth = (int) (430 * GameConstants.screenWidth) / 720;
    private final int btnHeight = (int) (80 * GameConstants.screenHeight) / 1280;
    private final float btnPosX = GameConstants.centerX - btnWidth / 2f;
    private final float btnPosY = GameConstants.screenHeight / 5f;

    InitialScreen (final SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        batch = new SpriteBatch();

        // ----------- stage & skin -----------
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal(GameConstants.skin));

        // ----------- button -----------

        bgWidth = 100 + ((GameConstants.screenHeight * assetsLoader.backTexture.getWidth()) / assetsLoader.backTexture.getHeight());
        bgHeight = 100 + GameConstants.screenHeight;

        btnLogin = new TextButton("Login", skin);
        btnLogin.setPosition(btnPosX, btnPosY);
        btnLogin.setSize(btnWidth, btnHeight);
        btnLogin.setColor(GameConstants.Colors.BUTTON_LOGIN.getColor());

        btnLogin.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetsLoader.playButtonSound();
                steinItUpGame.setScreenLogin();
            }
        });

        stage.addActor(btnLogin);

        btnRegister = new TextButton("Register", skin);
        btnRegister.setPosition(btnPosX, btnPosY - btnHeight - 30);
        btnRegister.setSize(btnWidth, btnHeight);
        btnRegister.setColor(GameConstants.Colors.BUTTON_LOGIN.getColor());

        btnRegister.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetsLoader.playButtonSound();
                steinItUpGame.setScreenRegister();
            }
        });

        stage.addActor(btnRegister);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.b, background.g, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);

        stage.getBatch().begin();

        stage.getBatch().setColor(GameConstants.Colors.STAGE_BATCH_CLEARANCE.getColor());

        // draw background
        stage.getBatch().draw(assetsLoader.logoTexture,
                GameConstants.centerX - bgWidth / 2f,
                GameConstants.screenHeight / 2f - bgHeight / 2f,
                bgWidth,
                bgHeight);

        stage.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        skin.dispose();
    }
}
