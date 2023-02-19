package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public abstract class MiniGame {

    private final SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;
    private DatabaseAccess databaseAccess;
    private GameConstants.Games game;
    private int level, minutes, seconds, numTasks;

    private String description;
    private ArrayList<String> descriptionLines;

    private final int MAX_LIVES = 4;
    private final int MAX_SCORE = 2500;
    private final int LOSE_TIME_POINTS = 5;
    private final int LOSE_WRONG_POINTS = 17;

    private boolean pause, displayRules;
    private Sprite spriteBg, spriteMenu, sprite, spriteBgRules, spriteRules;
    private float menuWidth, resumeWidth, restartWidth, rulesWidth, exitWidth, ruleTitleWidth;
    private int optionsWidth, optionHeight, optionX, menuY, option1Y, option2Y, option3Y, option4Y;
    private int offset;

    private int lives;
    private int points;
    private int tasksDone;

    private int initSeconds;
    private String time;
    private long timestamp;
    private boolean restartTimer, answeringCountDown;

    private int lifeSize, lifeStage;
    private boolean animate;
    private long animTime;
    private float elapsedTime;

    private Texture texture;
    private BitmapFont scoreFont, timeFont, rulesFont;
    private float nextDigit, lastDigit;
    private GlyphLayout layout;
    private float scoreWidth;

    private boolean startGame, startTime;
    private long counterTime;
    private long counterTimestamp;
    private  int countDown;

    private boolean hintTaken;

    MiniGame (SteinItUpGame steinItUpGame, GameConstants.Games game, int level, int minutes, int seconds, int numTasks) {
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.databaseAccess = steinItUpGame.databaseAccess;
        this.game = game;
        this.level = level;
        this.minutes = minutes;
        this.seconds = seconds;
        this.numTasks = numTasks;

        description = GameConstants.getDescription(GameConstants.Languages.ENGLISH, game);
        descriptionLines = new ArrayList<>();

        pause = false;
        tasksDone = 0;

        initSeconds = seconds;
        restartTimer = false;
        answeringCountDown = false;
        startTime = false;

        if (minutes == 0 && seconds < 30) {
            restartTimer = true;
        }

        if (seconds < 10) {
            time = minutes + ":0" + seconds;
        }
        else {
            time = minutes + ":" + seconds;
        }
        timestamp = System.currentTimeMillis();

        lives = MAX_LIVES;
        points = 0;

        lifeSize = 50;
        lifeStage = 0;
        animate = false;
        animTime = System.currentTimeMillis();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        scoreFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        scoreFont.getData().setScale(scale);
        scoreFont.setColor(Color.WHITE);

        scale = (1.3f * GameConstants.screenWidth) / 720;
        timeFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        timeFont.getData().setScale(scale);
        timeFont.setColor(Color.WHITE);

        scale = (1.1f * GameConstants.screenWidth) / 720;
        rulesFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        rulesFont.getData().setScale(scale);
        rulesFont.setColor(Color.WHITE);

        // ----------- score size -----------
        nextDigit = 10;
        lastDigit = 1;

        layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(scoreFont, "" + points);
        scoreWidth = layout.width;

        defineMenu();
        defineRules();

        // ----------- countDown Timer -----------
        startGame = false;
        counterTime = System.currentTimeMillis();
        counterTimestamp = System.currentTimeMillis();
        countDown = 3;

        //  ----------- hint -----------
        hintTaken = false;
    }

    void startGame(SpriteBatch batch) {
        batch.begin();
        // countDown timer to start the game
        if (!startGame) {
            scoreFont.draw(batch,"" + countDown,
                    GameConstants.centerX,
                    GameConstants.screenHeight * 5 / 6f);

            if ((System.currentTimeMillis() - counterTime) > 1000 && countDown > 0) {
                countDown--;
                counterTime = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() - counterTimestamp > 3 * 1000) {
                startGame = true;
            }
        }
        batch.end();
    }
    int getMAX_SCORE() {
        return MAX_SCORE;
    }
    int getLOSE_TIME_POINTS() {
        return LOSE_TIME_POINTS;
    }
    int getLOSE_WRONG_POINTS() {
        return LOSE_WRONG_POINTS;
    }

    boolean gameHasStarted() {
        return startGame;
    }
    boolean timeHasStarted() { return startTime; }
    void startTime() { startTime = true; }

    public abstract void evaluateAnswer();
    void increasePoints(int plusPoints) {
        points = Math.min(points + plusPoints, getMAX_SCORE());
        assetsLoader.playCorrectSound();
    }
    void decreasePoints(int minusPoints) {
        points = Math.max(points - minusPoints, 0);
        if (minusPoints != getLOSE_TIME_POINTS()) {
            assetsLoader.playWrongSound();
        }
    }
    void decreaseLife(boolean staged) {
        if (!staged) {
            if (lives > 0) {
                lives--;
                animate = true;
                animTime = System.currentTimeMillis();
            }
        }
        else {
            lifeStage++;
            lifeStage = lifeStage % 3;

            if (lifeStage == 0 && lives > 0) {
                lives--;
            }
            animate = true;
            animTime = System.currentTimeMillis();
        }
    }

    void completedTask() { tasksDone++; }

    boolean isEndTasks() { return tasksDone == numTasks; }
    boolean isEndLives() { return lives == 0; }
    boolean timesUp() { return (minutes <= 0 && seconds <= 0); }

    void restartTimer () {
        seconds = initSeconds;
        setAnsweringCountDown(false);
    }
    void setAnsweringCountDown(boolean start) { answeringCountDown = start; }
    boolean isAnsweringCountDown() {
        return answeringCountDown;
    }

    abstract void endGame();
    void terminateGame(boolean gameOver) {
        steinItUpGame.setFinalGameScreen(game, gameOver, points);
        if (!gameOver) {
            databaseAccess.addPlayedGameScoreDB(MyPreference.getUsername(), game, points);
        }
    }

    void pauseGame(boolean pause) { this.pause = pause; }
    boolean isPaused() { return pause; }

    private void defineMenu() {
        optionsWidth = (int) GameConstants.screenWidth - 170;
        optionHeight = 100;

        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, (int) GameConstants.screenHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.RULES_BG.getColor());
        pixmap.fill();

        spriteBg = new Sprite(new Texture(pixmap));

        pixmap = new Pixmap((int) GameConstants.screenWidth - 100, (int) GameConstants.screenHeight - 380, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0,0,0,230f/255f));
        pixmap.fill();

        spriteMenu = new Sprite(new Texture(pixmap));

        pixmap = new Pixmap(optionsWidth, optionHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(50f/255f, 50f/255f, 50f/255f, 255f/255f));
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));

        layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(scoreFont, "Menu");
        menuWidth = layout.width;
        layout.setText(timeFont, "Resume");
        resumeWidth = layout.width;
        layout.setText(timeFont, "Restart");
        restartWidth = layout.width;
        layout.setText(timeFont, "Rules");
        rulesWidth = layout.width;
        layout.setText(timeFont, "Exit");
        exitWidth = layout.width;

        optionX  = (int) (GameConstants.centerX - sprite.getWidth() / 2f);
        menuY    = (int) (GameConstants.screenHeight * 8 / 10 - 100);
        option1Y = (int) (GameConstants.screenHeight * 7 / 10 - 100);
        option2Y = (int) (GameConstants.screenHeight * 6 / 10 - 100);
        option3Y = (int) (GameConstants.screenHeight * 5 / 10 - 100);
        option4Y = (int) (GameConstants.screenHeight * 4 / 10 - 100);

        offset = 15;
    }
    private void displayMenu(SpriteBatch batch) {

        spriteBg.setPosition(GameConstants.centerX - spriteBg.getWidth() / 2f, 0);
        spriteBg.draw(batch);

        spriteMenu.setPosition(GameConstants.centerX - spriteMenu.getWidth() / 2f, 290 - 100);
        spriteMenu.draw(batch);

        sprite.setPosition(optionX, option1Y);
        sprite.draw(batch);
        sprite.setPosition(optionX, option2Y);
        sprite.draw(batch);
        sprite.setPosition(optionX, option3Y);
        sprite.draw(batch);
        sprite.setPosition(optionX, option4Y);
        sprite.draw(batch);

        scoreFont.draw(batch, "Menu", GameConstants.centerX - menuWidth / 2f, menuY + optionHeight / 2f + offset * 2);
        timeFont.draw(batch, "Resume", GameConstants.centerX - resumeWidth / 2f, option1Y + optionHeight / 2f + offset);
        timeFont.draw(batch, "Restart", GameConstants.centerX - restartWidth / 2f, option2Y + optionHeight / 2f + offset);
        timeFont.draw(batch, "Rules", GameConstants.centerX - rulesWidth / 2f, option3Y + optionHeight / 2f + offset);
        timeFont.draw(batch, "Exit", GameConstants.centerX - exitWidth / 2f, option4Y + optionHeight / 2f + offset);
    }
    private void defineRules() {

        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, (int) GameConstants.screenHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.BACKGROUND.getColor());
        pixmap.fill();

        spriteBgRules = new Sprite(new Texture(pixmap));

        pixmap = new Pixmap((int) GameConstants.screenWidth - 100, (int) GameConstants.screenHeight - 200, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.RULES_BG.getColor());
        pixmap.fill();

        spriteRules = new Sprite(new Texture(pixmap));

        layout = new GlyphLayout();
        layout.setText(scoreFont, "Rules");
        ruleTitleWidth = layout.width;

        String[] text = description.split(" ");
        int maxSize = 37; // conversion for all screens
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
    private void displayRules(SpriteBatch batch) {
        spriteBgRules.setPosition(GameConstants.centerX - spriteBg.getWidth() / 2f, 0);
        spriteBgRules.draw(batch);

        spriteRules.setPosition(GameConstants.centerX - spriteMenu.getWidth() / 2f, 100);
        spriteRules.draw(batch);

        scoreFont.draw(batch, "Rules", GameConstants.centerX - ruleTitleWidth / 2f, GameConstants.screenHeight - 150);

        for (int i = 0; i < descriptionLines.size(); i++) {
            rulesFont.draw(batch, descriptionLines.get(i), 80, 100 + ((GameConstants.screenHeight - 370) * (15 - i) / 15));
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

    boolean menuTouchDown(int screenX, int screenY) {
        if (pause) {
            Rectangle bounds = new Rectangle(optionX, option1Y, optionsWidth, optionHeight);
            if (bounds.contains(screenX, screenY)) {
                pauseGame(false);
            } else {
                bounds = new Rectangle(optionX, option2Y, optionsWidth, optionHeight);
                if (bounds.contains(screenX, screenY)) {
                    if (game.equals(GameConstants.Games.TIME_TO_LISTEN)) {
                        for (Sound person : assetsLoader.sounds.values()) {
                            person.stop();
                        }
                    }
                    setGameScreen(level);
                } else {
                    bounds = new Rectangle(optionX, option3Y, optionsWidth, optionHeight);
                    if (bounds.contains(screenX, screenY)) {
                        displayRules = true;
                    } else {
                        bounds = new Rectangle(optionX, option4Y, optionsWidth, optionHeight);
                        if (bounds.contains(screenX, screenY)) {
                            if (game.equals(GameConstants.Games.TIME_TO_LISTEN)) {
                                for (Sound person : assetsLoader.sounds.values()) {
                                    person.stop();
                                }
                            }
                            steinItUpGame.setHomePage();
                        }
                    }
                }
            }
        }
        return pause;
    }
    boolean rulesKeyDown(int keycode) {
        if (displayRules) {
            if (keycode == Input.Keys.BACK) {
                displayRules = false;
            }
            return true;
        }
        return false;
    }

    boolean hintTaken() {
        return hintTaken;
    }
    void takeHint() { hintTaken = true; }

    void displayStats(SpriteBatch batch) {

        batch.begin();

        // ------------------- menu - button -------------------
        batch.draw(assetsLoader.menuTexture, 10, GameConstants.screenHeight - 10 - 75, 75, 75);

        displayPoints(batch);
        displayLives(batch);
        displayTimer(batch);
        displayHint(batch);

        if (pause){
            if (displayRules) {
                displayRules(batch);
            }
            else {
                displayMenu(batch);
            }
        }

        batch.end();
    }
    private void displayPoints(SpriteBatch batch) {

        // ------------------- points -------------------
        if (points >= nextDigit) {
            layout.setText(scoreFont, "" + points);
            scoreWidth = layout.width;
            nextDigit = nextDigit * 10;
            lastDigit = lastDigit * 10;
        }
        if (points != 0 && points < lastDigit) {
            layout.setText(scoreFont, "" + points);
            scoreWidth = layout.width;
            nextDigit = nextDigit / 10;
            lastDigit = lastDigit / 10;
        }
        scoreFont.draw(batch, "" + points,
                GameConstants.centerX - scoreWidth / 2f,
                GameConstants.screenHeight - 20);
    }
    private void displayLives(SpriteBatch batch) {
        // ------------------- lives -------------------

        elapsedTime += Gdx.graphics.getDeltaTime();

        for (int i = 0; i < 4; i++) {
            if (i < lives - 1) {
                batch.draw(assetsLoader.lifeAnimation.getKeyFrames()[0],
                        GameConstants.screenWidth - ((lifeSize + 10) * (i + 1)),
                        GameConstants.screenHeight - lifeSize - 10,
                        lifeSize, lifeSize);
            }
            else if (i == lives - 1) {
                if (animate) {
                    batch.draw(assetsLoader.lifeAnimation.getKeyFrame(elapsedTime * 4, false),
                            GameConstants.screenWidth - ((lifeSize + 10) * (i + 1)),
                            GameConstants.screenHeight - lifeSize - 10,
                            lifeSize, lifeSize);
                    if (System.currentTimeMillis() - animTime > 1000) {
                        animate = false;
                    }
                } else {
                    batch.draw(assetsLoader.lifeAnimation.getKeyFrames()[lifeStage],
                            GameConstants.screenWidth - ((lifeSize + 10) * (i + 1)),
                            GameConstants.screenHeight - lifeSize - 10,
                            lifeSize, lifeSize);
                }
            }
            else {
                batch.draw(assetsLoader.lifeAnimation.getKeyFrames()[3],
                        GameConstants.screenWidth - ((lifeSize + 10) * (i + 1)),
                        GameConstants.screenHeight - lifeSize - 10,
                        lifeSize, lifeSize);
            }
        }
    }
    private void displayTimer(SpriteBatch batch) {
        if (!restartTimer) {   // tipo de jogo que so conta o tempo demorado a dar a resposta
            if (!pause && startGame && startTime) {
                // ------------------- time -------------------
                if (System.currentTimeMillis() - timestamp > 1000) {
                    if (seconds == 0 && minutes > 0) {
                        minutes--;
                        seconds = 59;
                        decreasePoints(getLOSE_TIME_POINTS());
                    } else if (seconds > 0) {
                        seconds--;
                        decreasePoints(getLOSE_TIME_POINTS());
                    }

                    if (seconds < 10) {
                        time = minutes + ":0" + seconds;
                    } else {
                        time = minutes + ":" + seconds;
                    }
                    timestamp = System.currentTimeMillis();
                }
            }
            timeFont.draw(batch, time,
                    GameConstants.screenWidth - 100,
                    GameConstants.screenHeight - lifeSize - 10 - 20);
        }
        else {
            if (!pause && startGame && startTime && isAnsweringCountDown()) {
                // ------------------- time -------------------
                if (System.currentTimeMillis() - timestamp > 1000) {
                     if (seconds > 0) {
                        seconds--;
                        decreasePoints(getLOSE_TIME_POINTS());
                    }
                    if (seconds < 10) {
                        time = minutes + ":0" + seconds;
                    } else {
                        time = minutes + ":" + seconds;
                    }
                    timestamp = System.currentTimeMillis();
                }
                timeFont.draw(batch, time,
                        GameConstants.screenWidth - 100,
                        GameConstants.screenHeight - lifeSize - 10 - 20);
            }
        }
    }
    private void displayHint(SpriteBatch batch) {
        if (!hintTaken() && MyPreference.isPremium()) {
            batch.draw(assetsLoader.hintTexture, 10 + 75 + 10, GameConstants.screenHeight - 10 - 100, 130, 100);
        }
    }

    void gameDispose() {
        texture.dispose();
        scoreFont.dispose();
        timeFont.dispose();
        rulesFont.dispose();
    }
}