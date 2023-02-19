package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class InitialGameScreen implements Screen {

    private final SteinItUpGame steinItUpGame;
    private DatabaseAccess databaseAccess;
    private RandomGames randomGames;
    private GameConstants.Games game;

    private Color background;

    private SpriteBatch batch;

    private Stage stage;
    private Skin skin;
    private Button btnPlay;
    private final int btnWidth = (int) ((250 * GameConstants.screenWidth) / 720);
    private final int btnHeight = (int) ((100 * GameConstants.screenHeight) / 1280);

    private Texture texture;
    private BitmapFont font;
    private Sprite sprite;
    private String description;
    private ArrayList<String> descriptionLines;

    InitialGameScreen(final SteinItUpGame steinItUpGame, final GameConstants.Games game) {
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;
        this.databaseAccess = steinItUpGame.databaseAccess;
        this.game = game;

        this.databaseAccess.updatePlayerBestScore(MyPreference.getUsername());

        choseBackground();

        randomGames.defineShape((int) (GameConstants.centerX), (int) (GameConstants.screenHeight / 4f),
                (int) (GameConstants.screenWidth - 300), this.game);

        batch = new SpriteBatch();

        // ----------- stage & skin -----------
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        skin = new Skin(Gdx.files.internal(GameConstants.skin));

        // ----------- button -----------
        btnPlay = new TextButton("PLAY", skin);
        btnPlay.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight + 50);
        btnPlay.setSize(btnWidth, btnHeight);
        btnPlay.setColor(GameConstants.Colors.MATH_BG_COLOR.getColor());

        btnPlay.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                steinItUpGame.assetsLoader.playButtonSound();
                int level = databaseAccess.getCurrentLevel(game);
                //level = 4;
                if (level == 0) {
                    steinItUpGame.setInitialGameScreen(game);
                }
                else {
                    steinItUpGame.setLevelPage(game, level);
                }
            }
        });

        stage.addActor(btnPlay);

        // ----------- background title -----------
        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth - 100,
                (int) (GameConstants.centerY - btnHeight - 50),
                Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.RULES_BG.getColor());
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));

        description = GameConstants.getMiniDescription(GameConstants.Languages.ENGLISH, game);
        descriptionLines = new ArrayList<>();

        defineRules();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);
    }

    private void choseBackground() {
        switch (game.getGameTheme()) {
            case MEMORY:
                background = GameConstants.Colors.MEMORY_BG_DARKER.getColor();
                break;
            case MATH:
                background = GameConstants.Colors.MATH_BG_DARKER.getColor();
                break;
            case FOCUS:
                background = GameConstants.Colors.FOCUS_BG_DARKER.getColor();
                break;
            case LOGIC:
                background = GameConstants.Colors.LOGIC_BG_DARKER.getColor();
                break;
        }
    }

    private void defineRules() {

        String[] text = description.split(" ");
        int maxSize = 33;
        int size = 2;
        StringBuilder line = new StringBuilder("  ");
        for (String s : text) {
            size += s.length() + 1;
            if (size > maxSize) {
                descriptionLines.add(line.toString());
                size = s.length();
                line = new StringBuilder(s + " ");
            } else if(s.contains(".") || s.contains("!") || s.contains("?")) {
                line.append(s).append(" ");
                descriptionLines.add(line.toString());
                size = 2;
                line = new StringBuilder("  ");
            } else {
                line.append(s).append(" ");
            }
            if (s.equals(text[text.length - 1]) && size != 0) {
                descriptionLines.add(line.toString());
            }
        }
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        randomGames.displayGame((int) (GameConstants.centerX), (int) (GameConstants.screenHeight * 3 / 4f),
                (int) (GameConstants.screenWidth - 300), game);

        stage.act(delta);
        stage.draw();

        batch.begin();

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, 50);
        sprite.draw(batch);

        for (int i = 0; i < descriptionLines.size(); i++) {
            font.draw(batch, descriptionLines.get(i), 110, 100 + ((GameConstants.centerY - btnHeight - 150) * (7 - i) / 7));
        }

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            steinItUpGame.setHomePage();
        }
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
        texture.dispose();
        font.dispose();
    }
}
