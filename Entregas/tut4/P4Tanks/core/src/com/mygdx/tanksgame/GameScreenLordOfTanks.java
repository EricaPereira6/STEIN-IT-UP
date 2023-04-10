package com.mygdx.tanksgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

public class GameScreenLordOfTanks implements Screen, InputProcessor {
    private final MyTanksGame mainClass;
    private final int screenWidth;
    private final int screenHeight;
    private final AssetsLoader assets;
    private final float assetsFactor;

    private SpriteBatch batch;
    private final String appStateTag = "GameScreenLordOfTanks";

    private final ShapeRenderer shapeRenderer;
    private Rectangle tankButton;
    private Rectangle notesButton;

    GameScreenLordOfTanks (MyTanksGame mainClass) {
        this.mainClass = mainClass;

        this.screenWidth = mainClass.screenWidth;
        this.screenHeight = mainClass.screenHeight;
        
        this.assets = mainClass.assets;
        this.assetsFactor = mainClass.assetsSizeFactor;

        Gdx.app.log(appStateTag, "constructor " + appStateTag);

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
        shapeRenderer = new ShapeRenderer();

        defineButtons();
    }
    @Override
    public void show() { }

    @Override
    public void render(float delta) {

        batch.begin();

        // draw background
        batch.draw(assets.backTexture, 0, 0, screenWidth, screenHeight);

        // Draw shadow and text
        assets.shadow.draw(batch,"Touch for Sound !",
                screenWidth / 2f - 150 + 5,
                (200 * assetsFactor) + assets.font.getCapHeight() + 5,
                400, Align.center, false);

        assets.font.draw(batch,"Touch for Sound !",
                screenWidth / 2f - 150,
                (200 * assetsFactor) + assets.font.getCapHeight(),
                400, Align.center, false);

        batch.end();

        float border = 50 * assetsFactor;
        // draw delimiter lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.RED);

        shapeRenderer.rect(tankButton.x - border, tankButton.y - border,
                tankButton.width + border * 2, tankButton.height + border * 2);

        shapeRenderer.setColor(Color.YELLOW);

        shapeRenderer.rect(notesButton.x - border, notesButton.y - border,
                notesButton.width + border * 2, notesButton.height + border * 2);

        shapeRenderer.end();

        batch.begin();

        // draw tank and notes image
        batch.draw(assets.tankTexture, tankButton.x, tankButton.y,
                tankButton.getWidth(), tankButton.getHeight());

        batch.draw(assets.notesTexture, notesButton.x, notesButton.y,
                notesButton.getWidth(), notesButton.getHeight());

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
        shapeRenderer.dispose();

        Gdx.app.log(appStateTag, "onDispose");
    }

    private void defineButtons () {
        // 2 rectangles to used around the textures in order to built the buttons

        int factor = 4;
        float tankWidth = assets.tankTexture.getWidth() * assetsFactor * factor;
        float tankHeight = assets.tankTexture.getHeight() * assetsFactor * factor;
        tankButton = new Rectangle (
                screenWidth / 4f - tankWidth / 2f,
                3 * screenHeight / 5f - tankHeight / 2f,
                tankWidth, tankHeight);

        float notesWidth = assets.notesTexture.getWidth() * assetsFactor * factor;
        float notesHeight = assets.notesTexture.getHeight() * assetsFactor * factor;
        notesButton = new Rectangle(
                3 * screenWidth / 4f - notesWidth / 2f,
                3 * screenHeight / 5f - notesHeight / 2f,
                notesWidth, notesHeight);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Mouse Y is inverted
        screenY = screenHeight - screenY;
        MyTanksGame.log("TouchUp, x:" + screenX + " y:" + screenY); // from previous projects

        if (notesButton.contains(screenX, screenY)) {
            MyTanksGame.log(" Touch on Notes button ");
            assets.music.stop();
            assets.music.play();
        }
        else if (tankButton.contains(screenX, screenY)) {
            MyTanksGame.log(" Touch on Tank button ");
            assets.explosion.play();
        }
        else {
            assets.music.stop();
            assets.explosion.stop();
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
