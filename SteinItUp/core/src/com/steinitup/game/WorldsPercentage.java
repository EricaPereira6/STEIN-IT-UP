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

public class WorldsPercentage extends MiniGame implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private RandomGames randomGames;
    private int level;
    private SpriteBatch batch;
    private Color background = GameConstants.Colors.BACKGROUND.getColor();

    private final int inputWidth = (int) ((300 * GameConstants.screenWidth) / 720);
    private final int inputHeight = (int) ((110 * GameConstants.screenWidth) / 720);
    private final int btnWidth =  (int) ((170 * GameConstants.screenWidth) / 720);
    private final int btnHeight = (int) ((110 * GameConstants.screenWidth) / 720);
    private final int diff = (int) ((inputWidth + btnWidth) / 2f);
    private final int boxWidth = (int) ((250 * GameConstants.screenWidth) / 720);
    private final int boxHeight = (int) ((150 * GameConstants.screenWidth) / 720);
    private final int keySize = (int) ((100 * GameConstants.screenWidth) / 720);

    private Texture texture;
    private BitmapFont font, answerFont, numbersFont;

    private final int numNumbers = 3;
    private final int numPercentages = 3;
    private int number, percentage;
    private float numberWidth, percentWidth;

    private Random random;
    private int indexNumber, indexPercentage;
    private ArrayList<Integer> numbers, percentages;
    private Integer[] chosenNumbers, chosenPercentages;
    private String answer;

    WorldsPercentage(SteinItUpGame steinItUpGame, int level) {
        super(steinItUpGame, GameConstants.Games.WORLDS_PERCENTAGE, level, 1, 20, 9);
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;
        this.level = level;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        startTime();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        scale = (1.3f * GameConstants.screenWidth) / 720;
        answerFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        answerFont.getData().setScale(scale);
        answerFont.setColor(Color.WHITE);

        scale = (1.2f * GameConstants.screenWidth) / 720;
        numbersFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        numbersFont.getData().setScale(scale);
        numbersFont.setColor(Color.BLACK);

        // ----------- text size -----------
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, "" + number);
        numberWidth = layout.width;
        layout.setText(font, percentage + " %");
        percentWidth = layout.width;

        // ----------- define numbers ------------
        random = new Random();
        defineNumbers();
        choseNumbers();
        indexNumber = 0;
        indexPercentage = 0;

        number = chosenNumbers[indexNumber];
        percentage = chosenPercentages[indexPercentage];

        indexPercentage++;

        answer = "";
    }

    private void defineNumbers() {
        numbers = new ArrayList<>();
        percentages = new ArrayList<>();

        if (level == 1) {

            for (int i = 10; i < 100; i += 10) {
                numbers.add(i);
                percentages.add(i);
            }
            for (int i = 100; i <= 900; i += 100) {
                numbers.add(i);
            }
        }
        else if (level == 2) {  // mais percentagens

            for (int i = 5; i < 100; i += 5) {
                int odd = i / 2;
                if (i % 10 == 0 && odd % 2 == 0) {
                    numbers.add(i);
                }
                percentages.add(i);
            }
            for (int i = 100; i <= 900; i += 100) {
                numbers.add(i);
            }
        }
        else if (level == 3) { // mais 1 percentagem e numeros maiores

            for (int i = 100; i < 1000; i += 100) {
                numbers.add(i);
            }
            for (int i = 1000; i <= 9000; i += 1000) {
                numbers.add(i);
            }
            percentages.add(1);
            for (int i = 5; i < 100; i++) {
                if (i % 5 == 0) {
                    percentages.add(i);
                }
            }
        }
        else {// mais percentagens

            for (int i = 100; i < 1000; i += 100) {
                numbers.add(i);
            }
            for (int i = 1000; i <= 9000; i += 1000) {
                numbers.add(i);
            }
            percentages.add(1);
            for (int i = 5; i <= 99; i++) {
                if (i % 5 == 0) {
                    percentages.add(i);
                    percentages.add(i+1);
                }
            }
        }
    }

    private void choseNumbers() {
        chosenNumbers = new Integer[numNumbers];
        chosenPercentages = new Integer[numNumbers * numPercentages];

        for (int i = 0; i < numNumbers; i++) {
            chosenNumbers[i] = numbers.get(random.nextInt(numbers.size()));
            if (i != 0) {
                for (int j = 0; j < i; j++) {
                    if (chosenNumbers[j].equals(chosenNumbers[i])) {
                        i--;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < numNumbers; i++) {
            for (int a = 0; a < numPercentages; a++) {

                int p = random.nextInt(percentages.size());

                int rnd = random.nextInt(100);
                if ((level == 3 || level == 4) && rnd < 50) {
                    p = 0;
                }
                int index = a + (i * numPercentages);
                chosenPercentages[index] = percentages.get(p);
                if (a != 0) {
                    for (int j = i * numPercentages; j < index; j++) {
                        if (chosenPercentages[j].equals(chosenPercentages[index])) {
                            a--;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void displayCalculator() {
        // calculadora
        //esquerdas
        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100 + keySize * 2 + 20, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 7
        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100 + keySize + 10, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 4
        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 1

        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize - 10), 100 + keySize * 2 + 20, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 8
        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize - 10), 100 + keySize + 10, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 5
        randomGames.roundedRectangle((int) (GameConstants.centerX - keySize - 10), 100, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 2
        //direitas
        randomGames.roundedRectangle((int) (GameConstants.centerX), 100 + keySize * 2 + 20, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 9
        randomGames.roundedRectangle((int) (GameConstants.centerX), 100 + keySize + 10, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 6
        randomGames.roundedRectangle((int) (GameConstants.centerX), 100, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 3

        randomGames.roundedRectangle((int) (GameConstants.centerX + keySize + 20), 100 + keySize * 2 + 20, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // 0
        randomGames.roundedRectangle((int) (GameConstants.centerX + keySize + 20), 100 + keySize + 10, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // .
        randomGames.roundedRectangle((int) (GameConstants.centerX + keySize + 20), 100, 25,
                keySize, keySize, GameConstants.Colors.FADED_GREY.getColor()); // <=

        batch.begin();

        numbersFont.draw(batch, "7",
                (int) (GameConstants.centerX - keySize * 2 - 20) + keySize / 2f - 10, 100 + keySize * 2 + 20 + keySize / 2f + 20);
        numbersFont.draw(batch, "4",
                (int) (GameConstants.centerX - keySize * 2 - 20) + keySize / 2f - 10, 100 + keySize + 10 + keySize / 2f + 20);
        numbersFont.draw(batch, "1",
                (int) (GameConstants.centerX - keySize * 2 - 20) + keySize / 2f - 10, 100 + keySize / 2f + 20);
        numbersFont.draw(batch, "8",
                (int) (GameConstants.centerX - keySize - 10) + keySize / 2f     - 10, 100 + keySize * 2 + 20 + keySize / 2f + 20);
        numbersFont.draw(batch, "5",
                (int) (GameConstants.centerX - keySize - 10) + keySize / 2f     - 10, 100 + keySize + 10 + keySize / 2f + 20);
        numbersFont.draw(batch, "2",
                (int) (GameConstants.centerX - keySize - 10) + keySize / 2f     - 10, 100 + keySize / 2f + 20);
        numbersFont.draw(batch, "9",
                (int) (GameConstants.centerX) + keySize / 2f                    - 10, 100 + keySize * 2 + 20 + keySize / 2f + 20);
        numbersFont.draw(batch, "6",
                (int) (GameConstants.centerX) + keySize / 2f                    - 10, 100 + keySize + 10 + keySize / 2f + 20);
        numbersFont.draw(batch, "3",
                (int) (GameConstants.centerX) + keySize / 2f                    - 10, 100 + keySize / 2f + 20);
        numbersFont.draw(batch, "0",
                (int) (GameConstants.centerX + keySize + 20) + keySize / 2f     - 10, 100 + keySize * 2 + 20 + keySize / 2f + 20);
        numbersFont.draw(batch, ".",
                (int) (GameConstants.centerX + keySize + 20) + keySize / 2f     - 10, 100 + keySize + 10 + keySize / 2f + 20);
        numbersFont.draw(batch, "<=",
                (int) (GameConstants.centerX + keySize + 20) + keySize / 2f     - 20, 100 + keySize / 2f + 20);

        batch.end();
    }

    private void restartVariables () {
        completedTask();

        if (indexNumber < chosenNumbers.length && indexPercentage < chosenPercentages.length) {
            number = chosenNumbers[indexNumber];
            percentage = chosenPercentages[indexPercentage];

            indexPercentage++;
            if (indexPercentage % numPercentages == 0) {
                indexNumber++;
            }
        }
        answer = "";
    }

    @Override
    public void evaluateAnswer() {
        int result = (number * percentage) / 100;
        if (answer.length() > 0) {

            if (Integer.parseInt(answer) == result) {
                increasePoints(260);
            }
            else {
                decreasePoints(getLOSE_WRONG_POINTS());
                decreaseLife(false);
            }

            restartVariables();
        }
    }
    @Override
    void endGame() {
        if (isEndTasks()) {
            terminateGame(false);
        }
        if (timesUp() || isEndLives()) {
            terminateGame(true);
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // number background
        randomGames.roundedRectangle((int) ((GameConstants.screenWidth * 1 / 4f) - (boxWidth / 2f)),
                (int) ((GameConstants.screenHeight * 6 / 8f) - (boxHeight / 2f)), 25,
                boxWidth, boxHeight, GameConstants.Colors.FADED_WHITE.getColor());

        // percentage background
        randomGames.roundedRectangle((int) ((GameConstants.screenWidth * 3 / 4f) - (boxWidth / 2f)),
                (int) ((GameConstants.screenHeight * 5 / 8f) - (boxHeight / 2f)), 25,
                boxWidth, boxHeight, GameConstants.Colors.FADED_WHITE.getColor());

        //input
        randomGames.roundedRectangle((int) GameConstants.centerX - diff - 10,
                (int) (GameConstants.centerY - 200), 15,
                inputWidth, inputHeight, Color.DARK_GRAY);
        //send
        randomGames.roundedRectangle((int) GameConstants.centerX + inputWidth - diff + 10,
                (int) (GameConstants.centerY - 200), 15,
                btnWidth, btnHeight, Color.GRAY);


        batch.begin();

        font.draw(batch,
                number + "",
                (GameConstants.screenWidth * 1 / 4f) - numberWidth,
                (GameConstants.screenHeight * 6 / 8f) + 20);

        font.draw(batch,
                percentage + " %",
                (GameConstants.screenWidth * 3 / 4f) - (percentWidth / 2f),
                (GameConstants.screenHeight * 5 / 8f) + 20);

        answerFont.draw(batch,
                answer,
                (int) GameConstants.centerX - diff + 50,
                (int) (GameConstants.centerY - 200 + 75));
        answerFont.draw(batch,
                "Send",
                (int) GameConstants.centerX + inputWidth - diff + 50,
                (int) (GameConstants.centerY - 200 + 65));

        batch.end();

        // keys
        displayCalculator();

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
        answerFont.dispose();
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
            // send
            Rectangle bounds = new Rectangle(GameConstants.centerX + inputWidth - diff + 10,
                    (int) (GameConstants.centerY - 200), btnWidth, btnHeight);
            if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                evaluateAnswer();
            } else {
                // menu
                bounds = new Rectangle(10, GameConstants.screenHeight - 10 - 75, 75, 75);
                if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                    pauseGame(true);
                }else {
                    // hint
                    bounds = new Rectangle(10 + 75 + 10, GameConstants.screenHeight - 10 - 100, 130, 100);
                    if (bounds.contains(screenX, screenY) && !hintTaken() && gameHasStarted() && MyPreference.isPremium()) {
                        takeHint();
                        increasePoints(260);
                        restartVariables();
                    }else {
                        touchDownCalculator(screenX, screenY);
                    }
                }
            }
        }
        return false;
    }

    private void touchDownCalculator(int screenX, int screenY) {
        Rectangle bounds = new Rectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100 + keySize * 2 + 20,
                keySize, keySize);
        if (answer.length() < 8 && gameHasStarted()) {
            if (bounds.contains(screenX, screenY)) {
                answer += "7";
            } else {
                bounds = new Rectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100 + keySize + 10,
                        keySize, keySize);
                if (bounds.contains(screenX, screenY)) {
                    answer += "4";
                } else {
                    bounds = new Rectangle((int) (GameConstants.centerX - keySize * 2 - 20), 100,
                            keySize, keySize);
                    if (bounds.contains(screenX, screenY)) {
                        answer += "1";
                    } else {
                        bounds = new Rectangle((int) (GameConstants.centerX - keySize - 10),
                                100 + keySize * 2 + 20, keySize, keySize);
                        if (bounds.contains(screenX, screenY)) {
                            answer += "8";
                        } else {
                            bounds = new Rectangle((int) (GameConstants.centerX - keySize - 10),
                                    100 + keySize + 10, keySize, keySize);
                            if (bounds.contains(screenX, screenY)) {
                                answer += "5";
                            } else {
                                bounds = new Rectangle((int) (GameConstants.centerX - keySize - 10),
                                        100, keySize, keySize);
                                if (bounds.contains(screenX, screenY)) {
                                    answer += "2";
                                } else {
                                    bounds = new Rectangle((int) (GameConstants.centerX),
                                            100 + keySize * 2 + 20, keySize, keySize);
                                    if (bounds.contains(screenX, screenY)) {
                                        answer += "9";
                                    } else {
                                        bounds = new Rectangle((int) (GameConstants.centerX),
                                                100 + keySize + 10, keySize, keySize);
                                        if (bounds.contains(screenX, screenY)) {
                                            answer += "6";
                                        } else {
                                            bounds = new Rectangle((int) (GameConstants.centerX),
                                                    100, keySize, keySize);
                                            if (bounds.contains(screenX, screenY)) {
                                                answer += "3";
                                            } else {
                                                bounds = new Rectangle((int) (GameConstants.centerX + keySize + 20),
                                                        100 + keySize * 2 + 20, keySize, keySize);
                                                if (bounds.contains(screenX, screenY)) {
                                                    answer += "0";
                                                } else {
                                                    bounds = new Rectangle((int) (GameConstants.centerX + keySize + 20),
                                                            100 + keySize + 10, keySize, keySize);
                                                    if (bounds.contains(screenX, screenY)) {
                                                        answer += ".";
                                                    } else {
                                                        bounds = new Rectangle((int) (GameConstants.centerX + keySize + 20),
                                                                100, keySize, keySize);
                                                        if (bounds.contains(screenX, screenY) && answer.length() > 0) {
                                                            answer = answer.substring(0, answer.length() - 1);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (gameHasStarted() && answer.length() > 0) {
            bounds = new Rectangle((int) (GameConstants.centerX + keySize + 20),
                    100, keySize, keySize);
            if (bounds.contains(screenX, screenY)) {
                answer = answer.substring(0, answer.length() - 1);
            }
        }
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
