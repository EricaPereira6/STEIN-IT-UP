package com.mygdx.facesgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreenFaces implements Screen, InputProcessor {
    private final MyGdxGame mainClass;
    private String appStateTag = "GameScreenFaces";
    private SpriteBatch batch;

    private int screenWidth;
    private int screenHeight;

    private AssetsLoader assets;
    private int assetsWidth;
    private int assetsHeight;

    private float elapsedTime;

    private Pixmap pixmap;
    private Sprite sprite;

    private boolean animate;

    private boolean canUseAcel = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    private float triangleOriginX;
    private float triangleOriginY;
    private float triangleX;
    private float triangleY;

    GameScreenFaces (MyGdxGame mainClass) {
        this.mainClass = mainClass;

        this.screenWidth = mainClass.screenWidth;
        this.screenHeight = mainClass.screenHeight;

        this.assets = mainClass.assets;
        this.assetsWidth = mainClass.assetsWidth;
        this.assetsHeight = mainClass.assetsHeight;

        int logLevel = Application.LOG_INFO;
        Gdx.app.setLogLevel(logLevel);
        Gdx.app.log(appStateTag, "constructor GameScreenFaces ");

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        debugLines();

        //box Animation variation
        animate = true;

        //triangle Animation variation
        triangleOriginX = screenWidth * 3 / 4f - assetsWidth / 2f;
        triangleOriginY = screenHeight * 3 / 4f - assetsHeight / 2f;
        triangleX = triangleOriginX;
        triangleY = triangleOriginY;

    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (canUseAcel) {
            processAccelerometer();
        }

        batch.begin();

        //drawDebugLines();

        elapsedTime += Gdx.graphics.getDeltaTime();

        batch.draw(assets.faceCircleAnimation.getKeyFrame(elapsedTime, true),
                screenWidth * 3 / 4f - assetsWidth / 2f,
                screenHeight / 4f - assetsHeight / 2f,
                assetsWidth,
                assetsHeight);

        batch.draw(assets.faceHexAnimation.getKeyFrame(elapsedTime, true),
                screenWidth / 4f - assetsWidth / 2f,
                screenHeight * 3 / 4f - assetsHeight / 2f,
                assetsWidth,
                assetsHeight);

        batch.draw(assets.faceTriAnimation.getKeyFrame(elapsedTime, true),
                triangleX,
                triangleY,
                assetsWidth,
                assetsHeight);

        boxAnimation();

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
        //assets.dispose();
        pixmap.dispose();

        Gdx.app.log(appStateTag, "onDispose");
    }

    private void debugLines() {
        // create 256 wide, 128 height using 8 bits for Red, Green, Blue and Alpha
        pixmap = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);
        //pixmap.setColor(Color.RED);
        //pixmap.fill();

        // Draw two lines forming an X
        pixmap.setColor(Color.BLUE);
        pixmap.drawLine(0, (int) (screenHeight / 2f), screenWidth - 1, (int) (screenHeight / 2f));
        pixmap.drawLine((int) (screenWidth / 2f), 0, (int) (screenWidth / 2f), screenHeight - 1);

        // create a sprite, that is a texture
        sprite = new Sprite(new Texture(pixmap));
    }

    private void drawDebugLines() {
        sprite.setPosition(0, screenHeight / 2f - sprite.getHeight() / 2);
        sprite.draw(batch);
        sprite.setPosition(screenWidth - sprite.getWidth(),
                screenHeight / 2f - sprite.getHeight() / 2);
        sprite.draw(batch);
    }

    private void boxAnimation() {
        if (animate) {
            batch.draw(assets.faceBoxAnimation.getKeyFrame(elapsedTime, true),
                    screenWidth / 4f - assetsWidth / 2f,
                    screenHeight / 4f - assetsHeight / 2f,
                    assetsWidth,
                    assetsHeight);
        } else {
            batch.draw(assets.faceBoxAnimation.getKeyFrames()[0],
                    Gdx.input.getX(),
                    screenHeight - Gdx.input.getY(),
                    assetsWidth,
                    assetsHeight);
			/*
			batch.draw(assets.faceBoxAnimation.getKeyFrames()[0],
					screenWidth / 4f - assetsWidth / 2f,
					screenHeight / 4f - assetsHeight / 2f,
					assetsWidth,
					assetsHeight);

			batch.draw(assets.faceBoxAnimation.getKeyFrame(elapsedTime, true),
					Gdx.input.getX(),
					screenHeight - Gdx.input.getY(),
					assetsWidth - assetsWidth * 1 / 3f,
					assetsHeight - assetsHeight * 1 / 3f);

			 */
        }
    }

    // auxiliary method
    private void processAccelerometer() {
        float x = Gdx.input.getAccelerometerY();
        float y = Gdx.input.getAccelerometerX();

        int logLevel = Application.LOG_INFO;
        Gdx.app.setLogLevel(logLevel);

        // triangleX and triangleY are the coords of the triangle animation
        if (Math.abs(x) > 1 && triangleX < (screenWidth - assetsWidth) && triangleX > 0) {
            triangleX += x;
            //Gdx.app.log("coordinates -----------------> ", " x: " + x);
        }
        else if (triangleX <= 0) {
            triangleX = 0;
            if (x > 0){
                triangleX += x;
            }
        }
        else if (triangleX >= (screenWidth - assetsWidth)) {
            triangleX = (screenWidth - assetsWidth);
            if (x < 0){
                triangleX += x;
            }
        }
        if (Math.abs(y) > 1 && triangleY < (screenHeight - assetsHeight) && triangleY > 0) {
            triangleY -= y;
            //Gdx.app.log("coordinates -----------------> ", "y: " + y);
        }
        else if (triangleY <= 0) {
            triangleY = 0;
            if (y < 0){
                triangleY -= y;
            }
        }
        else if (triangleY >= (screenHeight - assetsHeight)) {
            triangleY = (screenHeight - assetsHeight);
            if (y > 0){
                triangleY -= y;
            }
        }

        //Gdx.app.log("coordinates -----------------> ", "Tx: " + triangleX + ", Ty: " + triangleY);
    }

    @Override
    public boolean keyDown (int keycode) {
        if(keycode == Input.Keys.BACK){
            MyGdxGame.log(" back pressed ");
            mainClass.setInitialScreen();
            return true ;
        }
        return false ;
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
        animate = false;
        triangleX = triangleOriginX;
        triangleY = triangleOriginY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        animate = true;
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