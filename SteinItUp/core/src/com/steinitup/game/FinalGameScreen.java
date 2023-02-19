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

public class FinalGameScreen implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private GameConstants.Games game;
    private boolean gameOver;
    private int finalScore, bestScore;

    private SpriteBatch batch;
    private RandomGames randomGames;
    private PolygonSprite poly;

    private int x, y ,size;
    private Color color, themeColor, darkColor, background;

    private Texture texture;
    private BitmapFont titleFont, font, scoreFont;
    private float titleWidth, scoreTextWidth, scoreWidth, bestScoreWidth;
    private Sprite sprite;

    private final String scoreStr = "score:";
    private final String bestScoreStr = "Best Score:    ";

    FinalGameScreen(SteinItUpGame steinItUpGame, GameConstants.Games game, boolean gameOver, int finalScore) {
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;
        this.game = game;
        this.gameOver = gameOver;
        this.finalScore = finalScore;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        bestScore = steinItUpGame.databaseAccess.getBestScore(game);

        if (finalScore > bestScore) {
            bestScore = finalScore;
        }

        size = 600;
        x = (int) (GameConstants.centerX - size / 2f);
        y = (int) (GameConstants.screenHeight * 1 / 2f - size / 2f);

        defineShapeAndColor();

        background = darkColor;

        // ---------------- fonts ----------------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.7f * GameConstants.screenWidth) / 720;
        titleFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        titleFont.getData().setScale(scale);
        titleFont.setColor(Color.WHITE);

        scale = (3f * GameConstants.screenWidth) / 720;
        scoreFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        scoreFont.getData().setScale(scale);
        scoreFont.setColor(Color.BLACK);

        scale = (1.1f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);
        if (game.getGameTheme().equals(GameConstants.Themes.MEMORY)) {
            font.setColor(Color.WHITE);
        }

        GlyphLayout layout = new GlyphLayout();
        layout.setText(titleFont, game.getGameName());
        titleWidth = layout.width;
        if (gameOver) {
            layout.setText(scoreFont, "Game Over");
            scoreWidth = layout.width;
        } else {
            layout.setText(font, scoreStr);
            scoreTextWidth = layout.width;
            layout.setText(scoreFont, "" + finalScore);
            scoreWidth = layout.width;
            layout.setText(font, bestScoreStr + 2207);
            bestScoreWidth = layout.width;
        }

        // ----------- background title -----------
        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, 120, Pixmap.Format.RGBA8888);
        //pixmap.setColor(new Color(50f/255f,120f/255f,180f/255f,255f/255f));
        pixmap.setColor(GameConstants.Colors.RULES_BG.getColor());
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));
    }

    private void defineShapeAndColor() {
        switch (game.getGameTheme()) {
            case MATH:
                themeColor = GameConstants.Colors.MATH_BG_COLOR.getColor();
                color = themeColor;
                poly = randomGames.defineHexagon(x, y, size, color);
                darkColor = GameConstants.Colors.MATH_BG_DARKER.getColor();
                break;
            case FOCUS:
                themeColor = GameConstants.Colors.FOCUS_BG_COLOR.getColor();
                color = themeColor;
                poly = randomGames.defineLozenge(x, y, size, color);
                darkColor = GameConstants.Colors.FOCUS_BG_DARKER.getColor();
                break;
            case LOGIC:
                themeColor = GameConstants.Colors.LOGIC_BG_COLOR.getColor();
                color = themeColor;
                poly = randomGames.defineTriangle(x, y, size, color);
                darkColor = GameConstants.Colors.LOGIC_BG_DARKER.getColor();
                break;
            case MEMORY:
                themeColor = GameConstants.Colors.MEMORY_BG_COLOR.getColor();
                color = themeColor;
                darkColor = GameConstants.Colors.MEMORY_BG_DARKER.getColor();
                break;
        }
    }

    private void drawShape() {
        switch (game.getGameTheme()) {
            case MATH:
            case FOCUS:
            case LOGIC:
                randomGames.drawPoly(poly);
                break;
            case MEMORY:
                randomGames.drawCircle((int) (x + size / 2f), (int) (y + size / 2f), size, color);
                break;
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        drawShape();

        batch.begin();

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, GameConstants.screenHeight - 230);
        sprite.draw(batch);

        titleFont.draw(batch, game.getGameName(), GameConstants.centerX - titleWidth / 2f, GameConstants.screenHeight - 150);

        if (gameOver) {
            scoreFont.draw(batch, "Game Over", GameConstants.centerX - scoreWidth / 2f, y + size / 2f + 20);
        }
        else {
            font.draw(batch, scoreStr, GameConstants.centerX - scoreTextWidth / 2f, y + size * 3 / 4f);
            scoreFont.draw(batch, "" + finalScore, GameConstants.centerX - scoreWidth / 2f, y + size / 2f + 20);
            font.draw(batch, bestScoreStr + bestScore, GameConstants.centerX - bestScoreWidth / 2f, y - 50);
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
        batch.dispose();
        texture.dispose();
        titleFont.dispose();
        scoreFont.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            steinItUpGame.setHomePage();
        }
        return false;
    }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override
    public boolean scrolled(int amount) { return false; }
}
