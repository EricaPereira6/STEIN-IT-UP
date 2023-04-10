package com.mygdx.cowboygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class GameScreenCowboy implements Screen {
    private SpriteBatch batch;
    private MyCowboyGame mainClass;
    private String appStateTag = "GameScreenCowboy";

    private int screenWidth;
    private  int screenHeight;

    private AssetsLoader assets;
    private final int COWBOY_SIZE = 150;
    private float elapsedTime;

    private final Scrollable scenarioBack;
    private final Scrollable scenarioMiddle;
    private final Scrollable scenarioFront;

    GameScreenCowboy(MyCowboyGame mainClass) {
        batch = new SpriteBatch();

        this.mainClass = mainClass;
        this.screenWidth = mainClass.screenWidth;
        this.screenHeight = mainClass.screenHeight;

        this.assets = mainClass.assets;

        Gdx.app.log(appStateTag, "constructor GameScreenCowboy");

        int backWidth = assets.backTexture.getWidth();
        int backHeight = assets.backTexture.getHeight();
        scenarioBack = new Scrollable(assets.backTexture, 0, 0,
                (screenHeight * backWidth) / backHeight, screenHeight, -2f);

        int midWidth = assets.midTexture.getWidth();
        int midHeight = assets.midTexture.getHeight();
        scenarioMiddle = new Scrollable(assets.midTexture, 0, 0,
                (150 * midWidth) / midHeight, 150, -20f);

        int frontWidth = assets.frontTexture.getWidth();
        int frontHeight = assets.frontTexture.getHeight();
        scenarioFront = new Scrollable(assets.frontTexture, 0, 0,
                (150 * frontWidth) / frontHeight, 150, -75f);
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        elapsedTime += delta * 4;

        scenarioBack.update(delta);
        scenarioMiddle.update(delta);
        scenarioFront.update(delta);

        batch.begin();

        int b = scenarioBack.draw(batch, screenWidth);

        int m = scenarioMiddle.draw(batch, screenWidth);

        batch.draw(assets.cowboyAnimation.getKeyFrame(elapsedTime, true),
                (screenWidth / 2f) - (COWBOY_SIZE / 2f), 90,
                COWBOY_SIZE, COWBOY_SIZE);

        int f = scenarioFront.draw(batch, screenWidth);

        //MyCowboyGame.log("back: " + b + ", mid: " + m + ", front: " + f);

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

        Gdx.app.log(appStateTag, "onDispose");
    }
}
