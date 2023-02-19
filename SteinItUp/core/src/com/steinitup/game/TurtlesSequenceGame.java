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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class TurtlesSequenceGame extends MiniGame implements Screen, InputProcessor {

    private final SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;
    private RandomGames randomGames;
    private int level;
    private SpriteBatch batch;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, optionsFont;
    private final String[] title1 = {"Count the number of turtles", "that pass by"};
    private final String[] title2 = {"Count the number of", "turtles that pass or memorize", "the order of their colors"};
    private final String[] question = {"What was the", "turtles' sequence?"};
    private float question11Width, question12Width, title11Width, title12Width, title21Width, title22Width, title23Width;

    private final Scrollable scenarioFront;
    private int turtleSize, numTurtlesMax; // number max of turtles per group
    private float move, speed;
    private ArrayList<Integer> numGroups;  // possible numbers of Groups per game

    private Random random;
    private int countGroups, chosenNumGroups;
    private Integer[] chosenNumTurtlesPerGroup;
    private Texture[] chosenTurtles;
    private float[] screenTop;             // to every line/group of turtles gets to exit the screen
    private String[] optionsColor, optionsNumber;
    private boolean colorQuestion;
    private int colorOption, numberOption, correctOption, chosenOption;

    private int x1 = 25;
    private int y1 = 50;
    private int y2 = 170;
    private int y3 = 290;
    private int y4 = 410;

    private enum turtleColors {
        PURPLE ("purple"),
        BLUE("blue"),
        GREEN("green"),
        YELLOW("yellow"),
        PINK("pink"),
        RED("red"),
        GRAY("gray");

        private String name;
        turtleColors(String name) {
            this.name = name;
        }
        public String getColorName() {
            return name;
        }
    }

    TurtlesSequenceGame (SteinItUpGame steinItUpGame, int level) {
        super(steinItUpGame, GameConstants.Games.TURTLES_SEQUENCE, level,0, 10, 5);  // 01:30 MIN
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.randomGames = steinItUpGame.randomGames;
        this.level = level;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        startTime();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.3f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        scale = (0.8f * GameConstants.screenWidth) / 720;
        optionsFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        optionsFont.getData().setScale(scale);
        optionsFont.setColor(Color.BLACK);

        // ----------- text size -----------
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, question[0]);
        question11Width = layout.width;
        layout.setText(font, question[1]);
        question12Width = layout.width;

        layout.setText(font, title1[0]);
        title11Width = layout.width;
        layout.setText(font, title1[1]);
        title12Width = layout.width;

        layout.setText(font, title2[0]);
        title21Width = layout.width;
        layout.setText(font, title2[1]);
        title22Width = layout.width;
        layout.setText(font, title2[2]);
        title23Width = layout.width;

        // ----------- sea -----------
        int frontWidth = assetsLoader.seaTexture.getWidth();
        int frontHeight = assetsLoader.seaTexture.getHeight();
        scenarioFront = new Scrollable(assetsLoader.seaTexture, 0, 0,
                (int) ((GameConstants.screenHeight * frontWidth) / frontHeight), (int) GameConstants.screenHeight,
                true, -40f);

        // ----------- sea turtles -----------
        random = new Random();
        defineVariables();
        defineTurtlesPerQuestion();
    }

    private void defineVariables() {
        turtleSize = 200;
        move = - turtleSize;  // to start at 0 height
        numGroups = new ArrayList<>();
        numGroups.add(6);

        if (level == 1 || level == 2) {

            numTurtlesMax = 5;
            speed = 5;
            numGroups.add(4);
            numGroups.add(5);

        } else if (level == 3) {

            numTurtlesMax = 7;
            speed = 7;
            numGroups.add(5);

        } else {

            numTurtlesMax = 8;
            speed = 11;
            numGroups.add(7);
        }
    }
    private void defineTurtlesPerQuestion() {
        countGroups = 0;
        chosenNumGroups = numGroups.get(random.nextInt(numGroups.size()));

        optionsColor = new String[]{"", "", "", ""};
        colorOption = random.nextInt(4);

        chosenTurtles = new Texture[chosenNumGroups];
        for (int i = 0; i < chosenTurtles.length; i++) {
            if (!choseColors(i, colorOption)) {
                i--;
            }
        }

        optionsNumber = new String[]{"", "", "", ""};
        numberOption = random.nextInt(4);

        chosenNumTurtlesPerGroup = new Integer[chosenNumGroups];
        screenTop = new float[chosenNumGroups];
        for (int i = 0; i < chosenNumTurtlesPerGroup.length; i++) {
            chosenNumTurtlesPerGroup[i] = random.nextInt(numTurtlesMax) + 1;

            if (i == 0) {
                optionsNumber[numberOption] = chosenNumTurtlesPerGroup[i] + "";
            } else {
                optionsNumber[numberOption] += " - " + chosenNumTurtlesPerGroup[i];
            }

            if (chosenNumTurtlesPerGroup[i] < 4) {
                screenTop[i] = GameConstants.screenHeight;
            } else if (chosenNumTurtlesPerGroup[i] < 6) {
                screenTop[i] = GameConstants.screenHeight + turtleSize;
            } else {
                screenTop[i] = GameConstants.screenHeight + turtleSize * 2;
            }
        }

        defineOptions();
    }
    private void defineOptions() {
        int rnd = random.nextInt(100);

        if (rnd < 50 && level != 1) {

            colorQuestion = true;
            correctOption = colorOption;

            for (int j = 0; j < 4; j++) {
                if (optionsColor[j].length() == 0) {
                    for (int i = 0; i < chosenNumGroups; i++) {
                        if(!choseOptionsColors(optionsColor, i, j)) {
                            i--;
                        }
                    }
                }
            }
        } else {

            colorQuestion = false;
            correctOption = numberOption;

            for (int j = 0; j < 4; j++) {
                if (optionsNumber[j].length() == 0) {
                    for (int i = 0; i < chosenNumGroups; i++) {
                        int num = random.nextInt(numTurtlesMax) + 1;
                        if (i == 0)
                            optionsNumber[j] = "" + num;
                        else
                            optionsNumber[j] += " - " + num;
                    }
                }
            }
        }
    }

    private boolean choseColors(int index, int indexOption) {
        String colorName;
        turtleColors color = turtleColors.values()[random.nextInt(turtleColors.values().length)];

        switch (color) {
            case BLUE:
                chosenTurtles[index] = assetsLoader.blueTurtleTexture;
                colorName = turtleColors.BLUE.getColorName();
                break;
            case GREEN:
                chosenTurtles[index] = assetsLoader.greenTurtleTexture;
                colorName = turtleColors.GREEN.getColorName();
                break;
            case YELLOW:
                chosenTurtles[index] = assetsLoader.yellowTurtleTexture;
                colorName = turtleColors.YELLOW.getColorName();
                break;
            case PINK:
                chosenTurtles[index] = assetsLoader.pinkTurtleTexture;
                colorName = turtleColors.PINK.getColorName();
                break;
            case RED:
                if (level == 1 || level == 2) {
                    return false;
                }
                chosenTurtles[index] = assetsLoader.redTurtleTexture;
                colorName = turtleColors.RED.getColorName();
                break;
            case PURPLE:
                if (level == 1 || level == 2) {
                    return false;
                }
                chosenTurtles[index] = assetsLoader.purpleTurtleTexture;
                colorName = turtleColors.PURPLE.getColorName();
                break;
            case GRAY:
                if (level != 4) {
                    return false;
                }
                chosenTurtles[index] = assetsLoader.grayTurtleTexture;
                colorName = turtleColors.GRAY.getColorName();
                break;
            default:
                return false;
        }
        if (index == 0) {
            optionsColor[indexOption] = colorName;
        } else {
            optionsColor[indexOption] += " - " + colorName;
        }
        return true;
    }
    private boolean choseOptionsColors(String[] options, int index, int indexOption) {
        String colorName = "";
        turtleColors color = turtleColors.values()[random.nextInt(turtleColors.values().length)];
        switch (color) {
            case BLUE:
                colorName = turtleColors.BLUE.getColorName();
                break;
            case GREEN:
                colorName = turtleColors.GREEN.getColorName();
                break;
            case YELLOW:
                colorName = turtleColors.YELLOW.getColorName();
                break;
            case PINK:
                colorName = turtleColors.PINK.getColorName();
                break;
            case RED:
                if (level == 1 || level == 2) {
                    return false;
                }
                colorName = turtleColors.RED.getColorName();
                break;
            case PURPLE:
                if (level == 1 || level == 2) {
                    return false;
                }
                colorName = turtleColors.PURPLE.getColorName();
                break;
            case GRAY:
                if (level != 4) {
                    return false;
                }
                colorName = turtleColors.GRAY.getColorName();
                break;
            default:
                return false;
        }
        if (index == 0) {
            options[indexOption] = colorName;
        } else {
            options[indexOption] += " - " + colorName;
        }
        return true;
    }

    private void displayTurtles() {
        if (countGroups < chosenNumGroups) {
            switch (chosenNumTurtlesPerGroup[countGroups]) {
                case 1:
                    lineOfTurtles(batch, 1, 0);
                    break;
                case 2:
                    lineOfTurtles(batch, 2, 0);
                    break;
                case 3:
                    lineOfTurtles(batch, 3, 0);
                    break;
                case 4:
                    lineOfTurtles(batch, 1, 0); // first line of turtles
                    lineOfTurtles(batch, 3, 1); // second line of turtles
                    break;
                case 5:
                    lineOfTurtles(batch, 2, 0);
                    lineOfTurtles(batch, 3, 1);
                    break;
                case 6:
                    lineOfTurtles(batch, 1, 0);
                    lineOfTurtles(batch, 3, 1);
                    lineOfTurtles(batch, 2, 2);
                    break;
                case 7:
                    lineOfTurtles(batch, 2, 0);
                    lineOfTurtles(batch, 3, 1);
                    lineOfTurtles(batch, 2, 2);
                    break;
                case 8:
                    lineOfTurtles(batch, 2, 0);
                    lineOfTurtles(batch, 3, 1);
                    lineOfTurtles(batch, 3, 2);
                    break;
            }
        }
    }
    private void lineOfTurtles(SpriteBatch batch, int  numTurtles, int secondLine) {

        int x = (int) GameConstants.centerX;
        int y = (int) move;

        if (secondLine == 1) {
            y = (int) (move - turtleSize - 20);
        }
        else if (secondLine == 2) {
            y = (int) (move - turtleSize * 2 - 40);
        }
        if (numTurtles == 1) {
            batch.draw(chosenTurtles[countGroups], x - turtleSize / 2f                   , y, turtleSize, turtleSize);
        } else if (numTurtles == 2) {
            batch.draw(chosenTurtles[countGroups], x - turtleSize - 10                   , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x + 10                                , y, turtleSize, turtleSize);
        } else if (numTurtles == 3) {
            batch.draw(chosenTurtles[countGroups], x - turtleSize / 2f - turtleSize - 20 , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x - turtleSize / 2f                   , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x + turtleSize / 2f + 20              , y, turtleSize, turtleSize);
        } else {
            batch.draw(chosenTurtles[countGroups], x - turtleSize * 2 - 10 - 20          , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x - turtleSize - 10                   , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x + 10                                , y, turtleSize, turtleSize);
            batch.draw(chosenTurtles[countGroups], x + 10 + turtleSize + 20              , y, turtleSize, turtleSize);
        }
    }

    private void displayTitle(SpriteBatch batch) {
        if (level != 1) {
            font.draw(batch, title2[0], GameConstants.centerX - title21Width / 2f, GameConstants.centerY + 300);
            font.draw(batch, title2[1], GameConstants.centerX - title22Width / 2f, GameConstants.centerY + 200);
            font.draw(batch, title2[2], GameConstants.centerX - title23Width / 2f, GameConstants.centerY + 100);
        } else {
            font.draw(batch, title1[0], GameConstants.centerX - title11Width / 2f, GameConstants.centerY + 200);
            font.draw(batch, title1[1], GameConstants.centerX - title12Width / 2f, GameConstants.centerY + 100);
        }
    }
    private void displayQuestion(SpriteBatch batch) {
        font.draw(batch, question[0], GameConstants.centerX - question11Width / 2f, GameConstants.centerY + 250);
        font.draw(batch, question[1], GameConstants.centerX - question12Width / 2f, GameConstants.centerY + 150);
    }
    private void displayOptions(SpriteBatch batch) {
                randomGames.roundedRectangle(x1, y1,25, (int) (GameConstants.screenWidth - 50), 100, Color.WHITE);
        randomGames.roundedRectangle(x1, y2,25, (int) (GameConstants.screenWidth - 50), 100, Color.WHITE);
        randomGames.roundedRectangle(x1, y3,25, (int) (GameConstants.screenWidth - 50), 100, Color.WHITE);
        randomGames.roundedRectangle(x1, y4,25, (int) (GameConstants.screenWidth - 50), 100, Color.WHITE);

        batch.begin();

        if (colorQuestion) {
            optionsFont.draw(batch, optionsColor[0], x1 * 2, y1 + 70);
            optionsFont.draw(batch, optionsColor[1], x1 * 2, y2 + 70);
            optionsFont.draw(batch, optionsColor[2], x1 * 2, y3 + 70);
            optionsFont.draw(batch, optionsColor[3], x1 * 2, y4 + 70);
        }
        else {
            font.draw(batch, optionsNumber[0], x1 * 4, y1 + 70);
            font.draw(batch, optionsNumber[1], x1 * 4, y2 + 70);
            font.draw(batch, optionsNumber[2], x1 * 4, y3 + 70);
            font.draw(batch, optionsNumber[3], x1 * 4, y4 + 70);
        }

        batch.end();
    }

    private void optionsTouchDown(int screenX, int screenY) {
        Rectangle bounds = new Rectangle(x1, y1, (int) (GameConstants.screenWidth - 50), 100);
        if (bounds.contains(screenX, screenY) && gameHasStarted()) {
            chosenOption = 0;
            evaluateAnswer();
        } else {
            bounds = new Rectangle(x1, y2, (int) (GameConstants.screenWidth - 50), 100);
            if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                chosenOption = 1;
                evaluateAnswer();
            } else {
                bounds = new Rectangle(x1, y3, (int) (GameConstants.screenWidth - 50), 100);
                if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                    chosenOption = 2;
                    evaluateAnswer();
                } else {
                    bounds = new Rectangle(x1, y4, (int) (GameConstants.screenWidth - 50), 100);
                    if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                        chosenOption = 3;
                        evaluateAnswer();
                    }
                }
            }
        }
    }
    @Override
    public void evaluateAnswer() {
        if (chosenOption == correctOption) {
            increasePoints(500);
        } else {
            decreasePoints(getLOSE_WRONG_POINTS() * 3);
            decreaseLife(false);
        }
        completedTask();
        restartTimer();
        defineTurtlesPerQuestion();
    }
    @Override
    void endGame() {
        if (isEndTasks()) {
            terminateGame(false);
        }
        if (isEndLives()) {
            terminateGame(true);
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scenarioFront.update(delta);

        batch.begin();

        if (!gameHasStarted()) {
            displayTitle(batch);
        }
        else if (countGroups < chosenNumGroups) {
            if (move < screenTop[countGroups]) {
                displayTurtles();
                move += speed;
            } else {
                move = 0;
                countGroups++;
            }
        }

        scenarioFront.draw(batch, (int) GameConstants.screenWidth);

        if (countGroups >= chosenNumGroups) {
            if (!isAnsweringCountDown()) {
                setAnsweringCountDown(true);
            }
            displayQuestion(batch);

            batch.end();
            displayOptions(batch);
            batch.begin();

            if (timesUp()) {
                completedTask();
                decreaseLife(false);
                decreasePoints(getLOSE_WRONG_POINTS());
                restartTimer();
                defineTurtlesPerQuestion();
            }
        }

        batch.end();

        startGame(batch);
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
        texture.dispose();
        font.dispose();
        optionsFont.dispose();
        for (Texture t : chosenTurtles) {
            t.dispose();
        }
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

        if (!menuTouchDown(screenX, screenY)) {
            // menu
            Rectangle bounds = new Rectangle(10, GameConstants.screenHeight - 10 - 75, 75, 75);
            if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                pauseGame(true);
            } else {
                // hint
                bounds = new Rectangle(10 + 75 + 10, GameConstants.screenHeight - 10 - 100, 130, 100);
                if (bounds.contains(screenX, screenY) && !hintTaken() && gameHasStarted() && isAnsweringCountDown() && MyPreference.isPremium()) {
                    takeHint();
                    increasePoints(260);
                    completedTask();
                    restartTimer();
                    defineTurtlesPerQuestion();
                }
                else {
                    optionsTouchDown(screenX, screenY);
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
