package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class HomePage implements Screen {
    private final SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;
    private SpriteBatch batch;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private final int headerHeight = (int) ((GameConstants.screenHeight * 100) / 1280);
    private final int figureHeight = (int) ((GameConstants.screenHeight * 150) / 1280);
    private final int betweenFigures = (int) (GameConstants.screenWidth - 300);
    private final float screenHeight = GameConstants.screenHeight - headerHeight;

    private RandomGames randomGames;

    private Texture texture;
    private BitmapFont font, menuFont, errorFont;
    private float titleWidth, titleHeight, errorWidth;
    private Sprite sprite;

    private final String titleStr = "Today's Games";

    private ShapeRenderer shapeRenderer;
    private ArrayList<Integer> positions;

    private Stage stage;
    private Skin skin;
    private Button btnGames;
    private boolean addListener;
    private final int btnWidth = (int) ((GameConstants.screenWidth * 500) / 720);
    private final int btnHeight = (int) ((GameConstants.screenHeight * 100) / 1280);

    private boolean menuActivated;
    private boolean displayErrorMessage;

    HomePage(final SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.randomGames = steinItUpGame.randomGames;

        steinItUpGame.databaseAccess.setEmailAndPhotoByUsername(MyPreference.getUsername());
        steinItUpGame.databaseAccess.updatePlayerLevels(MyPreference.getUsername());

        batch = new SpriteBatch();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);

        scale = (1f * GameConstants.screenWidth) / 720;
        menuFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        menuFont.getData().setScale(scale);
        menuFont.setColor(Color.BLACK);

        scale = (0.8f * GameConstants.screenWidth) / 720;
        errorFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        errorFont.getData().setScale(scale);
        errorFont.setColor(Color.RED);

        // ----------- title -----------
        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, titleStr);
        titleWidth = layout.width;// contains the width of the current set text
        titleHeight = layout.height; // contains the height of the current set text

        // ----------- background title -----------
        int border = (int) ((60 * GameConstants.screenWidth) / 720);
        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, (int) titleHeight + border, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.TITLE_LOGIN_BG.getColor());
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));

        // ----------- three games -----------

        randomGames.changeOfDay();

        positions = new ArrayList<>();
        positions.add(150);
        positions.add((int) (screenHeight * 28 / 40f));
        positions.add(150);
        positions.add((int) (screenHeight * 19 / 40f));
        positions.add(150);
        positions.add((int) (screenHeight * 10 / 40f));

        randomGames.defineShapes(positions, betweenFigures, figureHeight);

        shapeRenderer = new ShapeRenderer();

        // ----------- stage & skin -----------
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        skin = new Skin(Gdx.files.internal(GameConstants.skin));

        // ----------- button -----------
        btnGames = new TextButton("More Games", skin);
        btnGames.setPosition(GameConstants.centerX - btnWidth / 2f,
                90);
        btnGames.setSize(btnWidth, btnHeight);
        btnGames.setColor(GameConstants.Colors.MATH_BG_COLOR.getColor());

        addListener = false;

        btnGames.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetsLoader.playButtonSound();
                if (MyPreference.isPremium()) {
                    steinItUpGame.setMoreGamesPage();
                }
                else {
                    displayErrorMessage = true;
                }
                addListener = true;
            }
        });

        stage.addActor(btnGames);

        // ----------- menu -----------
        menuActivated = false;

        // ----------- error message -----------
        displayErrorMessage = false;
        layout.setText(errorFont, "Become Premium to get access");
        errorWidth = layout.width;
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw background and header
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.rect(0, 0,
                GameConstants.screenWidth, screenHeight);

        shapeRenderer.end();

        batch.begin();

        // draw title
        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f,
                GameConstants.screenHeight - headerHeight - 30 - sprite.getHeight());
        sprite.draw(batch);

        font.draw(batch,
                titleStr,
                GameConstants.centerX - titleWidth / 2f,
                GameConstants.screenHeight - headerHeight - 20 - titleHeight);

        if (displayErrorMessage) {
            errorFont.draw(batch,
                    "Become Premium to get access",
                    GameConstants.centerX - errorWidth / 2f,
                    80);
        }

        batch.end();

        stage.act(delta);

        stage.getBatch().begin();

        stage.getBatch().setColor(GameConstants.Colors.STAGE_BATCH_CLEARANCE.getColor());

        // draw background
        stage.getBatch().draw(assetsLoader.userTexture,
                GameConstants.screenWidth - headerHeight,
                screenHeight + 5,
                headerHeight,
                headerHeight - 10);

        stage.getBatch().end();

        stage.draw();

        if (randomGames.changeOfDay()) {
            randomGames.defineShapes(positions, betweenFigures, figureHeight);
        }
        randomGames.displayThreeGames(positions, betweenFigures, figureHeight);


        // detects if user chose one of the three games
        if (!menuActivated) {
            touchDownGames();
        }
        else {
            // draw background
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.WHITE);
            menuOptions();
            shapeRenderer.end();

            // draw outline
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            menuOptions();
            shapeRenderer.end();

            // draw text
            batch.begin();
            menuTextOptions();
            batch.end();
        }

        // detects if user open the menu
        touchDownMenu();
    }

    private void touchDownMenu(){

        if (Gdx.input.isTouched()) {

            int screenX = Gdx.input.getX();
            int screenY = (int) (GameConstants.screenHeight - Gdx.input.getY());

            Rectangle bounds = new Rectangle(GameConstants.screenWidth - headerHeight, screenHeight,
                    headerHeight, headerHeight);
            if (bounds.contains(screenX, screenY)) {
                assetsLoader.button.stop();
                assetsLoader.playButtonSound();
                menuActivated = true;
                displayErrorMessage = false;
            }
            else {
                bounds = new Rectangle(GameConstants.centerX + 80, screenHeight - 80,
                        GameConstants.centerX, 80);
                if (bounds.contains(screenX, screenY) && menuActivated) {   // menu : option Profile
                    assetsLoader.playButtonSound();
                    steinItUpGame.setProfilePage();
                }
                else {
                    bounds = new Rectangle(GameConstants.centerX + 80, screenHeight - 80 * 2,
                            GameConstants.centerX, 80);
                    if (bounds.contains(screenX, screenY) && menuActivated) {   // menu : option Stats
                        assetsLoader.playButtonSound();
                        steinItUpGame.setStatsPage();
                    } else {
                        bounds = new Rectangle(GameConstants.centerX + 80, screenHeight - 80 * 3,
                                GameConstants.centerX, 80);
                        if (bounds.contains(screenX, screenY) && menuActivated) {   // menu : option Premium
                            assetsLoader.playButtonSound();
                            MyPreference.setPremium(true);
                            menuActivated = false;
                        } else {
                            bounds = new Rectangle(GameConstants.centerX + 80, screenHeight - 80 * 4,
                                    GameConstants.centerX, 80);
                            if (bounds.contains(screenX, screenY) && menuActivated) {   // menu : option About
                                assetsLoader.playButtonSound();
                                steinItUpGame.setAboutPage();
                            } else {
                                bounds = new Rectangle(GameConstants.centerX + 80, screenHeight - 80 * 5,
                                        GameConstants.centerX, 80);
                                if (bounds.contains(screenX, screenY) && menuActivated) {   // menu : option Help
                                    assetsLoader.playButtonSound();
                                    steinItUpGame.setHelpPage();
                                } else {
                                    menuActivated = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void menuOptions() {
        shapeRenderer.rect(GameConstants.centerX + 80, screenHeight - 80,
                GameConstants.centerX, 80);
        shapeRenderer.rect(GameConstants.centerX + 80, screenHeight - 80 * 2,
                GameConstants.centerX, 80);
        shapeRenderer.rect(GameConstants.centerX + 80, screenHeight - 80 * 3,
                GameConstants.centerX, 80);
        shapeRenderer.rect(GameConstants.centerX + 80, screenHeight - 80 * 4,
                GameConstants.centerX, 80);
        shapeRenderer.rect(GameConstants.centerX + 80, screenHeight - 80 * 5,
                GameConstants.centerX, 80);
    }

    private void menuTextOptions() {
        float offset =  (80 / 2f) + 10;
        menuFont.draw(batch, "Profile", GameConstants.centerX + 80 + 15, screenHeight - 80 + offset);
        menuFont.draw(batch, "Stats", GameConstants.centerX + 80 + 15, screenHeight - 80 * 2 + offset);
        menuFont.draw(batch, "Premium", GameConstants.centerX + 80 + 15, screenHeight - 80 * 3 + offset);
        menuFont.draw(batch, "About", GameConstants.centerX + 80 + 15, screenHeight - 80 * 4 + offset);
        menuFont.draw(batch, "Help", GameConstants.centerX + 80 + 15, screenHeight - 80 * 5 + offset);
    }

    private void touchDownGames(){

        if (addListener) {
            btnGames.addListener(new ClickListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    assetsLoader.playButtonSound();
                    if (MyPreference.isPremium()) {
                        steinItUpGame.setMoreGamesPage();
                    }
                    else {
                        displayErrorMessage = true;
                    }
                    addListener = true;
                }
            });
            addListener = false;
        }

        if (Gdx.input.isTouched()) {

            int screenX = Gdx.input.getX();
            int screenY = (int) (GameConstants.screenHeight - Gdx.input.getY());

            Rectangle bounds = new Rectangle(positions.get(0) - (figureHeight / 2f), positions.get(1),
                    betweenFigures + (figureHeight / 2f), figureHeight);
            if (bounds.contains(screenX, screenY)) {
                assetsLoader.playButtonSound();
                steinItUpGame.setInitialGameScreen(MyPreference.getGame1());
            }
            else {
                bounds = new Rectangle(positions.get(2) - (figureHeight / 2f), positions.get(3),
                        betweenFigures + (figureHeight / 2f), figureHeight);
                if (bounds.contains(screenX, screenY)) {
                    assetsLoader.playButtonSound();
                    steinItUpGame.setInitialGameScreen(MyPreference.getGame2());
                }
                else {
                    bounds = new Rectangle(positions.get(4) - (figureHeight / 2f), positions.get(5),
                            betweenFigures + (figureHeight / 2f), figureHeight);
                    if (bounds.contains(screenX, screenY)) {
                        assetsLoader.playButtonSound();
                        steinItUpGame.setInitialGameScreen(MyPreference.getGame3());
                    }
                }
            }
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
        font.dispose();
        errorFont.dispose();
        texture.dispose();
        shapeRenderer.dispose();
    }
}

