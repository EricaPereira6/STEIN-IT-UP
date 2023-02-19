package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;

class StatsPage implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private DatabaseAccess databaseAccess;
    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private SpriteBatch batch;

    private RandomGames randomGames;

    private ShapeRenderer shapeRenderer;
    private PolygonSprite[] poly;
    private ArrayList<Integer> positions;
    private ArrayList<Float> textSizes, levelTextSizes;
    private int size, height;

    private Texture texture;
    private BitmapFont font, fontLevel, errorFont;
    private boolean displayErrorMessage;
    private float errorWidth;

    private Sprite completed, incomplete;

    private HashMap<GameConstants.Themes, String>  themeLevels;
    private HashMap<GameConstants.Themes, Integer> themeLevel;

    private boolean displayGames;
    private GameConstants.Themes gamesTheme;
    private ArrayList<GameConstants.Games> games;
    private PolygonSprite[] gamePoly;
    private ArrayList<Float> gameTextSize;
    private ArrayList<Integer> gamePositions;
    private Sprite gameCompleted, gameIncomplete;
    private HashMap<GameConstants.Games, String> gameLevels;
    private HashMap<GameConstants.Games, Integer> gameLevel, gameScore;

    StatsPage(SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.databaseAccess = steinItUpGame.databaseAccess;
        this.randomGames = steinItUpGame.randomGames;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        databaseAccess.updateGameLevels();
        databaseAccess.updatePlayerLevels(MyPreference.getUsername());

        poly = new PolygonSprite[4];
        shapeRenderer = new ShapeRenderer();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        scale = (0.9f * GameConstants.screenWidth) / 720;
        fontLevel = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        fontLevel.getData().setScale(scale);
        fontLevel.setColor(Color.BLACK);

        scale = (0.8f * GameConstants.screenWidth) / 720;
        errorFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        errorFont.getData().setScale(scale);
        errorFont.setColor(Color.RED);

        defineThemes();

        // ----------- games page -----------
        displayGames = false;
        gamesTheme = null;

        // ----------- error message -----------
        displayErrorMessage = false;
        GlyphLayout layout = new GlyphLayout();
        layout.setText(errorFont, "Become Premium to get access");
        errorWidth = layout.width;
    }

    private void defineShapes(ArrayList<Integer> positions){

        Color color;

        for (int i = 0; i < 3; i++) {

            int x = (int) (positions.get(i * 2) - (size / 2f));
            int y = positions.get((i * 2) + 1);

            switch (GameConstants.Themes.values()[i]) {
                case MEMORY:
                    poly[i] = null;
                    break;
                case MATH:
                    color = GameConstants.Colors.MATH_BG_DARKER.getColor();
                    poly[i] = randomGames.defineHexagon(x, y, size, color);
                    break;
                case FOCUS:
                    color = GameConstants.Colors.FOCUS_BG_DARKER.getColor();
                    poly[i] = randomGames.defineLozenge(x, y, size, color);
                    break;
                case LOGIC:
                    color = GameConstants.Colors.LOGIC_BG_DARKER.getColor();
                    poly[i] = randomGames.defineTriangle(x, y, size, color);
                    break;
            }
        }
    }

    private void drawFigures(int x, int y, int size, Color color) {
        int j = 0;
        for (GameConstants.Themes theme : GameConstants.Themes.values()) {
            if (poly[j] == null) {
                randomGames.drawCircle(x, y, size, color);
            } else {
                randomGames.drawPoly(poly[j]);
            }
            j++;
        }
    }

    private void setTextColor(GameConstants.Themes theme) {
        switch(theme) {
            case LOGIC:
                font.setColor(GameConstants.Colors.LOGIC_BG_COLOR.getColor());
                break;
            case FOCUS:
                font.setColor(GameConstants.Colors.FOCUS_BG_COLOR.getColor());
                break;
            case MATH:
                font.setColor(GameConstants.Colors.MATH_BG_COLOR.getColor());
                break;
            case MEMORY:
                font.setColor(GameConstants.Colors.MEMORY_BG_COLOR.getColor());
                break;
        }
    }

    private void defineThemes() {
        // ----------- define shapes -----------
        size = 150;
        height = 200;
        positions = new ArrayList<>();
        positions.add((int) (GameConstants.screenWidth * 1 / 4f - size / 2f - 10));
        positions.add(height);
        positions.add((int) (GameConstants.screenWidth * 2 / 4f - size / 2f - 10));
        positions.add(height);
        positions.add((int) (GameConstants.screenWidth * 3 / 4f - size / 2f - 10));
        positions.add(height);
        positions.add((int) (GameConstants.screenWidth * 4 / 4f - size / 2f - 10));
        positions.add(height);

        defineShapes(positions);

        // ----------- text size -----------
        textSizes = new ArrayList<>();
        GlyphLayout layout = new GlyphLayout();
        for (GameConstants.Themes theme : GameConstants.Themes.values()) {
            layout.setText(font, theme.getThemeName());
            textSizes.add(layout.width);
        }

        // ----------- levels -----------
        int w = (int) ((30 * GameConstants.screenWidth) / 720);
        int h = (int) ((140 * GameConstants.screenWidth) / 720);
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.LOGIC_BG_COLOR.getColor());
        pixmap.fill();

        completed = new Sprite(new Texture(pixmap));

        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        incomplete = new Sprite(new Texture(pixmap));

        themeLevel = new HashMap<>();
        themeLevels = this.databaseAccess.getUserThemeLevels();

        for (int i = 0; i < themeLevels.size(); i++) {
            GameConstants.Themes theme = GameConstants.Themes.values()[i];
            if (themeLevels.get(theme).equals("Master")) {
                themeLevel.put(theme, 5);
            }
            else {
                for (GameConstants.Levels level : GameConstants.Levels.values()) {
                    if (themeLevels.get(theme).equals(level.getLevelName())) {
                        themeLevel.put(theme, level.getLevel());
                        break;
                    }
                }
            }
        }

        levelTextSizes = new ArrayList<>();
        for (GameConstants.Themes theme : GameConstants.Themes.values()) {
            layout.setText(fontLevel, themeLevels.get(theme));
            levelTextSizes.add(layout.width);
        }
    }

    private void displayThemes() {
        drawFigures(positions.get(6), (int) (positions.get(7) + (size / 2f)), size,
                GameConstants.Colors.MEMORY_BG_DARKER.getColor());

        batch.begin();

        int index = 0;
        for (GameConstants.Themes theme : GameConstants.Themes.values()) {

            setTextColor(theme);
            font.draw(batch,
                    theme.getThemeName(),
                    positions.get(index * 2) - textSizes.get(index) / 2f,
                    positions.get((index * 2) + 1) - 10);

            for (int l = 0; l < 5; l++) {
                if (l < themeLevel.get(theme)) {
                    completed.setPosition(
                            positions.get(index * 2) - completed.getWidth() / 2f,
                            positions.get((index * 2) + 1) + size + 20 + (incomplete.getHeight() + 10) * l);
                    completed.draw(batch);
                } else {
                    incomplete.setPosition(
                            positions.get(index * 2) - incomplete.getWidth() / 2f,
                            positions.get((index * 2) + 1) + size + 20 + (incomplete.getHeight() + 10) * l);
                    incomplete.draw(batch);
                }
            }

            fontLevel.draw(batch,
                    themeLevels.get(theme),
                    positions.get(index * 2) - levelTextSizes.get(index) / 2f,
                    positions.get((index * 2) + 1) + size + 20 + (incomplete.getHeight() + 10) * 5 + 45);

            index++;
        }

        batch.end();
    }

    private void defineGames() {
        games = databaseAccess.getThemeGames(gamesTheme);
        gamePoly = new PolygonSprite[games.size()];

        int width = 25 + size / 2;
        gamePositions = new ArrayList<>();
        gameTextSize = new ArrayList<>();
        GlyphLayout layout = new GlyphLayout();

        gameLevel = new HashMap<>();
        gameLevels = databaseAccess.getPlayerLevel();
        gameScore = databaseAccess.getScores();

        for (int i = 0; i < games.size(); i++) {

            gamePositions.add(width);
            gamePositions.add((int) (GameConstants.screenHeight - 120 - (size / 2) - (size * i) - (20 * i)));

            switch (gamesTheme) {
                case MATH:
                    gamePoly[i] = randomGames.defineHexagon(
                            gamePositions.get(i * 2) - size / 2, gamePositions.get((i * 2) + 1) - size / 2,
                            size, GameConstants.Colors.MATH_BG_DARKER.getColor());
                    break;
                case LOGIC:
                    gamePoly[i] = randomGames.defineTriangle(
                            gamePositions.get(i * 2) - size / 2, gamePositions.get((i * 2) + 1) - size / 2,
                            size, GameConstants.Colors.LOGIC_BG_DARKER.getColor());
                    break;
                case FOCUS:
                    gamePoly[i] = randomGames.defineLozenge(
                            gamePositions.get(i * 2) - size / 2, gamePositions.get((i * 2) + 1) - size / 2,
                            size, GameConstants.Colors.FOCUS_BG_DARKER.getColor());
                    break;
            }
            // ----------- text size -----------
            layout.setText(fontLevel, games.get(i).getGameName());
            gameTextSize.add(layout.width);

            // ----------- levels -----------
            int w = (int) ((60 * GameConstants.screenWidth) / 720);
            int h = (int) ((20 * GameConstants.screenWidth) / 720);
            Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
            pixmap.setColor(GameConstants.Colors.LOGIC_BG_COLOR.getColor());
            pixmap.fill();
            gameCompleted = new Sprite(new Texture(pixmap));

            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            gameIncomplete = new Sprite(new Texture(pixmap));

            if (gameLevels.get(games.get(i)).equals("Master")) {
                gameLevel.put(games.get(i), 5);
            }
            else {
                for (GameConstants.Levels level : GameConstants.Levels.values()) {
                    if (gameLevels.get(games.get(i)).equals(level.getLevelName())) {
                        gameLevel.put(games.get(i), level.getLevel());
                        break;
                    }
                }
            }
        }
    }

    private void displayGames() {
        int index = 0;
        for (GameConstants.Games game : games) {

            if (gamesTheme.equals(GameConstants.Themes.MEMORY)) {
                randomGames.drawCircle(gamePositions.get(index * 2), gamePositions.get((index * 2) + 1), size,
                        GameConstants.Colors.MEMORY_BG_DARKER.getColor());
            }
            else {
                randomGames.drawPoly(gamePoly[index]);
            }

            batch.begin();

            fontLevel.draw(batch,
                    game.getGameName(),
                    GameConstants.centerX - gameTextSize.get(index) / 2f,
                    gamePositions.get((index * 2) + 1) + size / 2f - 10);

            for (int l = 0; l < 5; l++) {
                if (l < gameLevel.get(game)) {
                    gameCompleted.setPosition(
                            gamePositions.get(index * 2) + size * 3 / 4f + (gameIncomplete.getWidth() + 10) * l,
                            gamePositions.get((index * 2) + 1) - gameIncomplete.getHeight() / 2f);
                    gameCompleted.draw(batch);
                } else {
                    gameIncomplete.setPosition(
                            gamePositions.get(index * 2) + size * 3 / 4f + (gameIncomplete.getWidth() + 10) * l,
                            gamePositions.get((index * 2) + 1) - gameIncomplete.getHeight() / 2f);
                    gameIncomplete.draw(batch);
                }
            }

            fontLevel.draw(batch,
                    "" + gameScore.get(game),
                    GameConstants.screenWidth - 120,
                    gamePositions.get((index * 2) + 1) + 20);

            batch.end();

            index++;
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (displayGames) {
            displayGames();
        } else {
            displayThemes();
        }

        batch.begin();

        if (displayErrorMessage) {
            errorFont.draw(batch,
                    "Become Premium to get access",
                    GameConstants.centerX - errorWidth / 2f,
                    80);
        }

        batch.end();
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
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        errorFont.dispose();
        fontLevel.dispose();
        texture.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK && !displayGames) {
            steinItUpGame.setHomePage();
        }
        else {
            if (keycode == Input.Keys.BACK && displayGames) {
                displayGames = false;
            }
        }
        return false;
    }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = (int) (GameConstants.screenHeight - screenY);

        Rectangle bounds = new Rectangle(positions.get(0) - (size / 2f), positions.get(1), size, size);
        if (bounds.contains(screenX, screenY)) {
            steinItUpGame.assetsLoader.playButtonSound();
            if (!MyPreference.isPremium()) {
                displayErrorMessage = true;
            } else {
                gamesTheme = GameConstants.Themes.MATH;
                defineGames();
                displayGames = true;
            }
        } else {
            bounds = new Rectangle(positions.get(2) - (size / 2f), positions.get(3), size, size);
            if (bounds.contains(screenX, screenY)) {
                steinItUpGame.assetsLoader.playButtonSound();
                if (!MyPreference.isPremium()) {
                    displayErrorMessage = true;
                } else {
                    gamesTheme = GameConstants.Themes.FOCUS;
                    defineGames();
                    displayGames = true;
                }
            } else {
                bounds = new Rectangle(positions.get(4) - (size / 2f), positions.get(5), size, size);
                if (bounds.contains(screenX, screenY)) {
                    steinItUpGame.assetsLoader.playButtonSound();
                    if (!MyPreference.isPremium()) {
                        displayErrorMessage = true;
                    } else {
                        gamesTheme = GameConstants.Themes.LOGIC;
                        defineGames();
                        displayGames = true;
                    }
                } else {
                    bounds = new Rectangle(positions.get(6) - (size / 2f), positions.get(7), size, size);
                    if (bounds.contains(screenX, screenY)) {
                        steinItUpGame.assetsLoader.playButtonSound();
                        if (!MyPreference.isPremium()) {
                            displayErrorMessage = true;
                        } else {
                            gamesTheme = GameConstants.Themes.MEMORY;
                            defineGames();
                            displayGames = true;
                        }
                    }
                }
            }
        }
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override
    public boolean scrolled(int amount) { return false; }
}
