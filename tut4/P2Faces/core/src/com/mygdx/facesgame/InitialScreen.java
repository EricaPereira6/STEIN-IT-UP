package com.mygdx.facesgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

class InitialScreen implements Screen , InputProcessor {
    private final MyGdxGame mainClass;
    private String appStateTag = "InitialScreen";
    private SpriteBatch batch;

    private final AssetsLoader assets;
    private int assetsWidth;
    private int assetsHeight;

    private final float faceBoxX;
    private final float faceHexX;
    private final float faceBoxY;
    private final float faceHexY;

    InitialScreen (MyGdxGame mainClass) {
        batch = new SpriteBatch ();

        this.mainClass = mainClass;

        this.assets = mainClass.assets;
        this.assetsWidth = mainClass.assetsWidth;
        this.assetsHeight = mainClass.assetsHeight;

        Gdx.app.log(appStateTag, "onDispose");

        Gdx.input.setInputProcessor(this);

        faceBoxX = mainClass.screenWidth / 3f - assetsWidth / 2f;
        faceHexX = 2 * mainClass.screenWidth / 3f - assetsWidth / 2f;
        faceBoxY = mainClass.screenHeight / 2f - assetsHeight / 2f;
        faceHexY = faceBoxY;
    }

    @Override
    public void show() {

    }

    @Override
    public void render ( float delta ) {
        Gdx.gl.glClearColor(1, 0.7f, 0.3f, 1); // set colour
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(assets.faceBoxAnimation.getKeyFrames()[0],
                faceBoxX,
                faceBoxY,
                assetsWidth,
                assetsHeight);

        batch.draw(assets.faceHexAnimation.getKeyFrames()[0],
                faceHexX,
                faceHexY,
                assetsWidth,
                assetsHeight);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();

        Gdx.app.log(appStateTag, "onDispose");
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
    public boolean touchDown (int screenX, int screenY, int pointer, int button){
        MyGdxGame.log(" touchDown ...");
        Rectangle bounds = new Rectangle(faceBoxX, faceBoxY, assetsWidth, assetsHeight);
        if (bounds.contains(screenX, screenY)) {
            MyGdxGame.log("touchedDown on FaceBox image ...");
            mainClass.setScreenFaces();
        } else {
            bounds = new Rectangle (faceHexX, faceHexY, assetsWidth, assetsHeight);
            if (bounds.contains(screenX, screenY)) {
                MyGdxGame.log("touchedDown on FaceHex image ...");
                mainClass.setScreenBox2D();
            }
        }
        return false ;
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
