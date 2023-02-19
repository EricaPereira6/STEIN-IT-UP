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
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;

public class FastFormsGame extends MiniGame implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private int level;
    private SpriteBatch batch;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private RandomGames randomGames;

    private BitmapFont font, noFont, yesFont;
    private Texture texture;
    private GlyphLayout layout;
    private String question, questionType;
    private float questionWidth, yesWidth, yesHeight, noWidth, noHeight;

    private PolygonSprite poly;
    private int backgroundSize, figureSize;

    private boolean noPressed, yesPressed;

    private boolean firstChange;

    private RandomXS128 random = new RandomXS128(System.currentTimeMillis());
    private FormColor formColor, previousColor;
    private FormType formType, previousForm;

    public enum FormType {
        Hexagon, Circle, Triangle, Diamond
    }
    public enum FormColor {
        Yellow, Blue, Green, Red
    }
    public enum Questions {
        lVL3_1("shape", "Is the shape equal to the one before?"),
        lVL3_2("color", "Is the color equal to the one before?"),
        lVL3_3("both", "Is the shape and color equal to the one before?"),
        lVL4_1("circle","Is the shape a circle?"),
        lVL4_2("diamond","Is the shape a diamond?"),
        lVL4_3("hexagon","Is the shape a hexagon?"),
        lVL4_4("triangle","Is the shape a triangle?"),
        lVL4_5("yellow","Is the color yellow?"),
        lVL4_6("blue","Is the color blue?"),
        lVL4_7("green","Is the color green?"),
        lVL4_8("red","Is the color red?");

        private String value;
        private String question;
        Questions (String value, String question) {
            this.value = value;
            this.question = question;
        }
        public String getValue() {
            return value;
        }
        public String getQuestion() {
            return question;
        }
    }

    FastFormsGame(SteinItUpGame steinItUpGame, int level) {
        super(steinItUpGame, GameConstants.Games.FAST_FORMS, level,1, 0, 0);
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;
        this.level = level;

        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();

        startTime();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.1f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        scale = (2f * GameConstants.screenWidth) / 720;
        noFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        noFont.getData().setScale(scale);
        noFont.setColor(GameConstants.Colors.NO_BUTTON.getColor());

        yesFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        yesFont.getData().setScale(scale);
        yesFont.setColor(GameConstants.Colors.YES_BUTTON.getColor());

        // ----------- text size -----------
        question = "Observe the first image.";

        layout = new GlyphLayout();

        layout.setText(font, question);
        questionWidth = layout.width;

        layout.setText(noFont, "NO!");
        noWidth = layout.width;
        noHeight = layout.height;

        layout.setText(yesFont, "YES!");
        yesWidth = layout.width;
        yesHeight = layout.height;

        // ----------- buttons -----------
        noPressed = false;
        yesPressed = false;

        // ----------- background and figure size -----------
        backgroundSize = (int) (GameConstants.screenWidth - 100);
        figureSize = backgroundSize - 200;

        // ----------- define first form -----------
        formColor = FormColor.values()[random.nextInt(FormColor.values().length)];
        formType = FormType.values()[random.nextInt(FormType.values().length)];
        changeRandomFigure();
        previousColor = formColor;
        previousForm = formType;

        firstChange = true;
    }

    private Color getColor() {
        switch (formColor) {
            case Red:
                return GameConstants.Colors.COLOR_GAME_RED.getColor();
            case Blue:
                return GameConstants.Colors.COLOR_GAME_BLUE.getColor();
            case Green:
                return GameConstants.Colors.COLOR_GAME_GREEN.getColor();
            case Yellow:
                return GameConstants.Colors.COLOR_GAME_YELLOW.getColor();
        }
        return Color.WHITE;
    }

    private void changeRandomFigure () {
        int rnd = random.nextInt(100);
        if (rnd < 70) {
            formColor = FormColor.values()[random.nextInt(FormColor.values().length)];
            formType = FormType.values()[random.nextInt(FormType.values().length)];
        }

        switch (formType) {
            case Hexagon:
                poly = randomGames.defineHexagon((int) (GameConstants.screenWidth / 2f - figureSize / 2f),
                        (int) (GameConstants.screenHeight / 2f - figureSize / 2f),
                        figureSize, getColor());
                break;
            case Triangle:
                poly = randomGames.defineTriangle((int) (GameConstants.screenWidth / 2f - figureSize / 2f),
                        (int) (GameConstants.screenHeight / 2f - figureSize / 2f),
                        figureSize, getColor());
                break;
            case Diamond:
                poly = randomGames.defineLozenge((int) (GameConstants.screenWidth / 2f - figureSize / 2f),
                        (int) (GameConstants.screenHeight / 2f - figureSize / 2f),
                        figureSize, getColor());
                break;
        }
    }

    private void changeQuestion() {
        int index;
        switch (level) {
            case 1:
                questionType = Questions.values()[0].getValue();
                question = Questions.values()[0].getQuestion();
                break;
            case 2:
                questionType = Questions.values()[2].getValue();
                question = Questions.values()[2].getQuestion();
                break;
            case 3:
                index = random.nextInt(3);
                questionType = Questions.values()[index].getValue();
                question = Questions.values()[index].getQuestion();
                break;
            case 4:
                int rnd = random.nextInt(100);
                if (rnd < 75) {
                    index = random.nextInt(3);
                } else {
                    index = random.nextInt(Questions.values().length);
                }
                questionType = Questions.values()[index].getValue();
                question = Questions.values()[index].getQuestion();
                break;
        }
        layout.setText(font, question);
        questionWidth = layout.width;
    }

    private void drawFigure() {
        switch (formType) {
            case Triangle:
            case Hexagon:
            case Diamond:
                randomGames.drawPoly(poly);
                break;
            case Circle:
                randomGames.drawCircle((int) (GameConstants.screenWidth / 2f),
                        (int) (GameConstants.screenHeight / 2f),
                        figureSize, getColor());
                break;
        }
    }

    @Override
    public void evaluateAnswer(){
        if (level == 1) {
            if ((previousForm == formType && yesPressed) || (previousForm != formType && noPressed)) {
                increasePoints(20);
            } else {
                decreasePoints(getLOSE_WRONG_POINTS());
                decreaseLife(true);
            }
        }
        else if (level == 2) {
            if ((previousForm == formType && previousColor == formColor && yesPressed) ||
                    ((previousForm != formType || previousColor != formColor) && noPressed)) {
                increasePoints(20);
            } else {
                decreasePoints(getLOSE_WRONG_POINTS());
                decreaseLife(true);
            }
        }
        else if (level == 3) {
            if ((questionType.equals(Questions.lVL3_1.getValue()) &&
                    ((previousForm == formType && yesPressed) || (previousForm != formType && noPressed))) ||

                    (questionType.equals(Questions.lVL3_2.getValue()) &&
                    ((previousColor == formColor && yesPressed) || (previousColor != formColor && noPressed))) ||

                    (questionType.equals(Questions.lVL3_3.getValue()) &&
                    ((previousForm == formType && previousColor == formColor && yesPressed) ||
                            ((previousForm != formType || previousColor != formColor) && noPressed)))) {

                increasePoints(20);
            } else {
                decreasePoints(getLOSE_WRONG_POINTS());
                decreaseLife(true);
            }
        }
        else {
            if ((questionType.equals(Questions.lVL3_1.getValue()) &&
                    ((previousForm == formType && yesPressed) || (previousForm != formType && noPressed))) ||

                    (questionType.equals(Questions.lVL3_2.getValue()) &&
                            ((previousColor == formColor && yesPressed) || (previousColor != formColor && noPressed))) ||

                    (questionType.equals(Questions.lVL3_3.getValue()) &&
                            ((previousForm == formType && previousColor == formColor && yesPressed) ||
                                    ((previousForm != formType || previousColor != formColor) && noPressed))) ||

                    (questionType.equals(Questions.lVL4_1.getValue()) &&
                            ((formType == FormType.Circle && yesPressed) || (formType != FormType.Circle && noPressed))) ||

                    (questionType.equals(Questions.lVL4_2.getValue()) &&
                            ((formType == FormType.Diamond && yesPressed) || (formType != FormType.Diamond && noPressed))) ||

                    (questionType.equals(Questions.lVL4_3.getValue()) &&
                            ((formType == FormType.Hexagon && yesPressed) || (formType != FormType.Hexagon && noPressed))) ||

                    (questionType.equals(Questions.lVL4_4.getValue()) &&
                            ((formType == FormType.Triangle && yesPressed) || (formType != FormType.Triangle && noPressed))) ||

                    (questionType.equals(Questions.lVL4_5.getValue()) &&
                            ((formColor == FormColor.Yellow && yesPressed) || (formColor != FormColor.Yellow && noPressed))) ||

                    (questionType.equals(Questions.lVL4_6.getValue()) &&
                            ((formColor == FormColor.Blue && yesPressed) || (formColor != FormColor.Blue && noPressed))) ||

                    (questionType.equals(Questions.lVL4_7.getValue()) &&
                            ((formColor == FormColor.Green && yesPressed) || (formColor != FormColor.Green && noPressed))) ||

                    (questionType.equals(Questions.lVL4_8.getValue()) &&
                            ((formColor == FormColor.Red && yesPressed) || (formColor != FormColor.Red && noPressed)))) {

                increasePoints(20);
            }
            else {
                decreasePoints(getLOSE_WRONG_POINTS());
                decreaseLife(true);
            }
        }
        previousForm = formType;
        previousColor = formColor;
        yesPressed = false;
        noPressed = false;
    }
    @Override
    void endGame() {
        if (timesUp()) {
            terminateGame(false);
        }
        else if (isEndLives()) {
            terminateGame(true);
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // shapes background
        randomGames.roundedRectangle(50, (int) (GameConstants.screenHeight / 4f), 25,
                backgroundSize, backgroundSize, Color.WHITE);
        // no button
        randomGames.roundedRectangle(50, 50, 25,
                (int) (GameConstants.centerX - 75), 150,
                GameConstants.Colors.NO_BUTTON_BG.getColor());
        // yes button
        randomGames.roundedRectangle((int) (GameConstants.centerX + 25), 50, 25,
                (int) (GameConstants.centerX - 75), 150,
                GameConstants.Colors.YES_BUTTON_BG.getColor());


        batch.begin();

        noFont.draw(batch,
                "NO!",
                50 + (GameConstants.centerX - 75) / 2f - noWidth / 2f,
                50 + 75 + noHeight / 2f);
        yesFont.draw(batch,
                "YES!",
                (GameConstants.centerX + 25) + (GameConstants.centerX - 75) / 2f - yesWidth / 2f,
                    50 + 75 + yesHeight / 2f);
        font.draw(batch,
                question,
                GameConstants.centerX - questionWidth / 2f,
                GameConstants.screenHeight / 4f - 50);

        batch.end();

        startGame(batch);
        if (gameHasStarted() && firstChange) {
            changeRandomFigure();
            changeQuestion();
            firstChange = false;
        }
        drawFigure();
        displayStats(batch);
        endGame();
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
        font.dispose();
        noFont.dispose();
        yesFont.dispose();
        texture.dispose();
        gameDispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(!rulesKeyDown(keycode) && keycode == Input.Keys.BACK && !isPaused()){
            pauseGame(true);
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
        //MyGdxGame.log("TouchUp, x:" + screenX + " y:" + screenY); // from previous projects

        if(!menuTouchDown(screenX, screenY)) {
            Rectangle bounds = new Rectangle(50, 50, (int) (GameConstants.centerX - 75), 150);
            if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                noPressed = true;

                evaluateAnswer();
                changeRandomFigure();
                changeQuestion();
            } else {
                bounds = new Rectangle((int) (GameConstants.centerX + 25), 50, (int) (GameConstants.centerX - 75), 150);
                if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                    yesPressed = true;

                    evaluateAnswer();
                    changeRandomFigure();
                    changeQuestion();
                } else {
                    bounds = new Rectangle(10, GameConstants.screenHeight - 10 - 75, 75, 75);
                    if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                        pauseGame(true);
                    }
                    else {
                        bounds = new Rectangle(10 + 75 + 10, GameConstants.screenHeight - 10 - 100, 130, 100);
                        if (bounds.contains(screenX, screenY) && !hintTaken() && gameHasStarted() && MyPreference.isPremium()) {
                            takeHint();
                            increasePoints(20);
                            previousForm = formType;
                            previousColor = formColor;
                            yesPressed = false;
                            noPressed = false;
                            changeRandomFigure();
                            changeQuestion();
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
