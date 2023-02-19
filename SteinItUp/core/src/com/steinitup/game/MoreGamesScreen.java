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
import java.util.Arrays;

public class MoreGamesScreen implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;

    private SpriteBatch batch;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private RandomGames randomGames;

    private ShapeRenderer shapeRenderer;
    private PolygonSprite[] poly;
    private ArrayList<Integer> positions;

    private Texture texture;
    private BitmapFont font, titleFont;
    private final String titleStr = "All the Games";
    private float titleWidth, titleHeight;
    private Sprite sprite, titleSprite;
    private int size, height1, height2, height3, height4;

    private ArrayList<Integer> titlePositions;
    private ArrayList<Float> titleSizes;
    private ArrayList<String> words;

    // TODO system to get minigames by themes
    ArrayList<GameConstants.Games> games;

    MoreGamesScreen(SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        shapeRenderer = new ShapeRenderer();
        poly = new PolygonSprite[6];

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        scale = (2f * GameConstants.screenWidth) / 720;
        titleFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        titleFont.getData().setScale(scale);
        titleFont.setColor(Color.WHITE);

        // ----------- title -----------
        GlyphLayout layout = new GlyphLayout();
        layout.setText(titleFont, titleStr);
        titleWidth = layout.width;// contains the width of the current set text
        titleHeight = layout.height; // contains the height of the current set text

        // ----------- background title -----------
        int border = (int) ((60 * GameConstants.screenWidth) / 720);
        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, (int) titleHeight + border, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.TITLE_LOGIN_BG.getColor());
        pixmap.fill();

        titleSprite = new Sprite(new Texture(pixmap));

        // ----------- define games -----------
        size = (int) ((GameConstants.screenHeight * 175) / 1280);

        sortGames();
        definePositions();
        defineShapes();
        defineGamesTitles();

        // ----------- backgrounds -----------
        pixmap = new Pixmap((int) GameConstants.screenWidth, size + 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1,1,1,0.9f));
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));
    }

    private void sortGames() {
        games = new ArrayList<>();
        games.add(GameConstants.Games.WORLDS_PERCENTAGE);
        games.add(GameConstants.Games.OPERATE_THE_VIRUS);
        games.add(GameConstants.Games.FRACTIONARY);
        games.add(GameConstants.Games.FAST_FORMS);
        games.add(GameConstants.Games.PAINTING_COLORS);
        games.add(GameConstants.Games.JUMPING_TO_CONCLUSIONS);
        games.add(GameConstants.Games.TIME_TO_LISTEN);
        games.add(GameConstants.Games.SQUARES_OF_MEMORY);
        games.add(GameConstants.Games.TURTLES_SEQUENCE);
    }

    private void definePositions() {

        positions = new ArrayList<>();

        //float screenHeight = GameConstants.screenHeight;
        float screenHeight = GameConstants.screenHeight * 8 / 10f;

        height1 = (int) (screenHeight * 4 / 5f);
        // Math minigames positions
        positions.add((int) (GameConstants.screenWidth * 1 / 4f - size / 2f + 10));
        positions.add(height1);
        positions.add((int) (GameConstants.centerX));
        positions.add(height1);
        positions.add((int) (GameConstants.screenWidth * 3 / 4f + size / 2f - 10));
        positions.add(height1);

        height2 = (int) (screenHeight * 3 / 5f - 50);
        // Focus
        positions.add((int) (GameConstants.screenWidth * 1 / 4f - size / 2f + 10));
        positions.add(height2);
        positions.add((int) (GameConstants.centerX));
        positions.add(height2);

        height3 = (int) (screenHeight * 2 / 5f - 100);
        // logic
        positions.add((int) (GameConstants.screenWidth * 1 / 4f - size / 2f + 10));
        positions.add(height3);


        height4 = (int) (screenHeight  * 1 / 5f - 150);
        // memory
        positions.add((int) (GameConstants.screenWidth * 1 / 4f - size / 2f + 10));
        positions.add(height4);
        positions.add((int) (GameConstants.centerX));
        positions.add(height4);
        positions.add((int) (GameConstants.screenWidth * 3 / 4f + size / 2f - 10));
        positions.add(height4);

    }

    private void defineShapes(){

        Color color;

        for (int i = 0; i < 6; i++) {

            int x = (int) (positions.get(i * 2) - (size / 2f));
            int y = positions.get((i * 2) + 1);

            if (i < 3) {
                color = GameConstants.Colors.MATH_BG_COLOR.getColor();
                poly[i] = randomGames.defineHexagon(x, y, size, color);
            } else if (i < 5) {
                color = GameConstants.Colors.FOCUS_BG_COLOR.getColor();
                poly[i] = randomGames.defineLozenge(x, y, size, color);
            } else {
                color = GameConstants.Colors.LOGIC_BG_COLOR.getColor();
                poly[i] = randomGames.defineTriangle(x, y, size, color);
            }
        }
    }

    private void defineGamesTitles() {

        titlePositions = new ArrayList<>();

        String[] title = games.get(0).getGameName().split(" ");
        words = new ArrayList<>(Arrays.asList(title));
        titlePositions.add(positions.get(0));
        titlePositions.add((int) (positions.get(1) + size / 2f  + 20));
        titlePositions.add(positions.get(0));
        titlePositions.add((int) (positions.get(1) + size / 2f  - 20));

        title = games.get(1).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(2));
        titlePositions.add((int) (positions.get(3) + size / 2f  + 40));
        titlePositions.add(positions.get(2));
        titlePositions.add((int) (positions.get(3) + size / 2f));
        titlePositions.add(positions.get(2));
        titlePositions.add((int) (positions.get(3) + size / 2f  - 40));

        title = games.get(2).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(4));
        titlePositions.add((int) (positions.get(5) + size / 2f));

        title = games.get(3).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(6));
        titlePositions.add((int) (positions.get(7) + size / 2f  + 20));
        titlePositions.add(positions.get(6));
        titlePositions.add((int) (positions.get(7) + size / 2f  - 20));

        title = games.get(4).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(8));
        titlePositions.add((int) (positions.get(9) + size / 2f  + 20));
        titlePositions.add(positions.get(8));
        titlePositions.add((int) (positions.get(9) + size / 2f  - 20));

        title = games.get(5).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(10));
        titlePositions.add((int) (positions.get(11) + size / 2f  + 40));
        titlePositions.add(positions.get(10));
        titlePositions.add((int) (positions.get(11) + size / 2f));
        titlePositions.add(positions.get(10));
        titlePositions.add((int) (positions.get(11) + size / 2f  - 40));

        title = games.get(6).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(12));
        titlePositions.add((int) (positions.get(13) + size / 2f  + 40));
        titlePositions.add(positions.get(12));
        titlePositions.add((int) (positions.get(13) + size / 2f));
        titlePositions.add(positions.get(12));
        titlePositions.add((int) (positions.get(13) + size / 2f  - 40));

        title = games.get(7).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(14));
        titlePositions.add((int) (positions.get(15) + size / 2f  + 40));
        titlePositions.add(positions.get(14));
        titlePositions.add((int) (positions.get(15) + size / 2f));
        titlePositions.add(positions.get(14));
        titlePositions.add((int) (positions.get(15) + size / 2f  - 40));

        title = games.get(8).getGameName().split(" ");
        words.addAll(Arrays.asList(title));
        titlePositions.add(positions.get(16));
        titlePositions.add((int) (positions.get(17) + size / 2f + 20));
        titlePositions.add(positions.get(16));
        titlePositions.add((int) (positions.get(17) + size / 2f - 20));

        // ----------- sizes -----------
        GlyphLayout layout = new GlyphLayout();
        titleSizes = new ArrayList<>();

        for (String s : words) {
            layout.setText(font, s);
            titleSizes.add(layout.width / 2f);
            titleSizes.add(layout.height / 2f);
        }
    }

    private void drawCircle(int x, int y, int size, Color color) {

        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x, y, size / 2f);
        shapeRenderer.end();
    }

    private void drawFigures() {

        randomGames.drawPoly(poly[0]);
        randomGames.drawPoly(poly[1]);
        randomGames.drawPoly(poly[2]);
        randomGames.drawPoly(poly[3]);
        randomGames.drawPoly(poly[4]);
        randomGames.drawPoly(poly[5]);
    }

    private void displayGamesTitles() {

        batch.begin();

        for (int i = 0; i < words.size(); i++) {
            font.draw(batch, words.get(i),
                    titlePositions.get(i * 2) - titleSizes.get(i * 2),
                    titlePositions.get((i * 2) + 1) + titleSizes.get((i * 2) + 1));
        }

        batch.end();
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // draw title
        titleSprite.setPosition(GameConstants.centerX - titleSprite.getWidth() / 2f, GameConstants.screenHeight - 160);
        titleSprite.draw(batch);

        titleFont.draw(batch,
                titleStr,
                GameConstants.centerX - titleWidth / 2f,
                GameConstants.screenHeight - 30 - titleSprite.getHeight() / 2f);

        // draw backgrounds
        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, height1 - 20);
        sprite.draw(batch);

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, height2 - 20);
        sprite.draw(batch);

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, height3 - 20);
        sprite.draw(batch);

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, height4 - 20);
        sprite.draw(batch);

        batch.end();

        drawFigures();
        drawCircle(positions.get(12), (int) (positions.get(13) + (size / 2f)), size,
                GameConstants.Colors.MEMORY_BG_COLOR.getColor());
        drawCircle(positions.get(14), (int) (positions.get(15) + (size / 2f)), size,
                GameConstants.Colors.MEMORY_BG_COLOR.getColor());
        drawCircle(positions.get(16), (int) (positions.get(17) + (size / 2f)), size,
                GameConstants.Colors.MEMORY_BG_COLOR.getColor());

        displayGamesTitles();
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
        texture.dispose();
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = (int) (GameConstants.screenHeight - screenY);

        Rectangle bounds;
        for (int i = 0; i < games.size(); i++) {
            bounds = new Rectangle(positions.get(i * 2) - size / 2f, positions.get((i * 2) + 1), size, size);
            if (bounds.contains(screenX, screenY)) {
                steinItUpGame.assetsLoader.playButtonSound();
                steinItUpGame.setInitialGameScreen(games.get(i));
                //Gdx.app.log(" ------------- clicked game -----------", "Game: " + games.get(i).getGameName());
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
