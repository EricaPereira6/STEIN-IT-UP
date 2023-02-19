package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class LevelPage implements Screen, InputProcessor {

    private final SteinItUpGame steinItUpGame;
    private GameConstants.Games game;
    private int level;

    private SpriteBatch batch;
    private Color background;

    private ShapeRenderer shapeRenderer;
    private PolygonSprite[] poly;
    private RandomGames randomGames;

    private Texture texture;
    private BitmapFont font;
    private float titleWidth, titleHeight;
    private ArrayList<Integer> positions;
    private int width, height;

    LevelPage(SteinItUpGame steinItUpGame, GameConstants.Games game, int level) {

        this.steinItUpGame = steinItUpGame;
        this.game = game;
        this.level = level;

        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        background = GameConstants.Colors.BACKGROUND.getColor();

        poly = new PolygonSprite[8];
        randomGames = new RandomGames();

        shapeRenderer = new ShapeRenderer();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.5f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        // ----------- text size -----------
        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, "Level 1");
        titleWidth = layout.width;
        titleHeight = layout.height;

        // ----------- define Levels -----------

        positions = new ArrayList<>();
        positions.add(150);
        positions.add((int) (GameConstants.screenHeight * 6 / 10f));
        positions.add(150);
        positions.add((int) (GameConstants.screenHeight * 5 / 10f));
        positions.add(150);
        positions.add((int) (GameConstants.screenHeight * 4 / 10f));
        positions.add(150);
        positions.add((int) (GameConstants.screenHeight * 3 / 10f));

        width = (int) (GameConstants.screenWidth - 300);
        height = (int) ((100 * GameConstants.screenHeight) / 1280);

        Color color;

        for (int i = 0; i < 4; i++) {

            int x = (int) (positions.get(i * 2) - (height / 2f));
            int y = positions.get((i * 2) + 1);

            if (i < level) {
                color = Color.WHITE;
            } else {
                color = Color.GRAY;
            }
            poly[i * 2] = randomGames.defineLozenge(x, y, height, color);
            poly[(i * 2) + 1] = randomGames.defineLozenge(x + width, y, height, color);

        }
    }

    private void setGameScreen(int chosenLevel) {
        switch (game) {
            case JUMPING_TO_CONCLUSIONS:
                steinItUpGame.setGameJumpingToConclusions(chosenLevel);
                break;
            case FAST_FORMS:
                steinItUpGame.setGameFastForms(chosenLevel);
                break;
            case PAINTING_COLORS:
                steinItUpGame.setGamePaintingColors(chosenLevel);
                break;
            case WORLDS_PERCENTAGE:
                steinItUpGame.setGameWorldsPercentage(chosenLevel);
                break;
            case OPERATE_THE_VIRUS:
                steinItUpGame.setGameOperateTheVirus(chosenLevel);
                break;
            case FRACTIONARY:
                steinItUpGame.setGameFractionary(chosenLevel);
                break;
            case TIME_TO_LISTEN:
                steinItUpGame.setGameTimeToListen(chosenLevel);
                break;
            case TURTLES_SEQUENCE:
                steinItUpGame.setGameTurtlesSequence(chosenLevel);
                break;
            case SQUARES_OF_MEMORY:
                steinItUpGame.setGameSquaresOfMemory(chosenLevel);
                break;
            default:
                steinItUpGame.setHomePage();
        }
    }

    private void displayLevels() {

        Color color;

        for (int i = 0; i < 4; i++) {

            if (i < level) {
                color = Color.WHITE;
            } else {
                color = Color.GRAY;
            }
            randomGames.drawPoly(poly[i * 2]);
            randomGames.drawPoly(poly[(i * 2) + 1]);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(color);
            shapeRenderer.rect(positions.get(i * 2), positions.get((i * 2) + 1), width, height);

            shapeRenderer.end();

            batch.begin();

            font.draw(batch,
                    "Level " + (i + 1),
                    GameConstants.centerX - titleWidth / 2f,
                    positions.get((i * 2) + 1) + height / 2f + titleHeight - 15);

            batch.end();
        }
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        displayLevels();
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
        font.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            steinItUpGame.setHomePage();
            return true;
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

        Rectangle bounds = new Rectangle(positions.get(0) - (height / 2f), positions.get(1),
                width + (height / 2f), height);
        if (bounds.contains(screenX, screenY)) {
            steinItUpGame.assetsLoader.playButtonSound();
            setGameScreen(1);
            //Gdx.app.log("touch down >>>>>", "level 1");
        }
        else {
            bounds = new Rectangle(positions.get(2) - (height / 2f), positions.get(3),
                    width + (height / 2f), height);
            if (bounds.contains(screenX, screenY) && level >= 2) {
                steinItUpGame.assetsLoader.playButtonSound();
                setGameScreen(2);
                //Gdx.app.log("touch down >>>>>", "level 2");
            }
            else {
                bounds = new Rectangle(positions.get(4) - (height / 2f), positions.get(5),
                        width + (height / 2f), height);
                if (bounds.contains(screenX, screenY) && level >= 3) {
                    steinItUpGame.assetsLoader.playButtonSound();
                    setGameScreen(3);
                    //Gdx.app.log("touch down >>>>>", "level 3");
                }
                else {
                    bounds = new Rectangle(positions.get(6) - (height / 2f), positions.get(7),
                            width + (height / 2f), height);
                    if (bounds.contains(screenX, screenY) && level >= 4) {
                        steinItUpGame.assetsLoader.playButtonSound();
                        setGameScreen(4);
                        //Gdx.app.log("touch down >>>>>", "level 4");
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
