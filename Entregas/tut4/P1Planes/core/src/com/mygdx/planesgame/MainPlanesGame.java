package com.mygdx.planesgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainPlanesGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

    private int screenWidth;
    private int screenHeight;

    private String appStateTag = "AppState";

	private BitmapFont font;
	private Pixmap pixmap;
	private Sprite sprite;
	private TextureAtlas textureAtlas;
	private Animation<TextureAtlas.AtlasRegion> animation;
	private int planesTextureWidth;
	private int planesTextureHeight;
	private float elapsedTime;
	private Animation<TextureRegion> rotateUpAnimation;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("plain.png");

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		font = new BitmapFont();
		font.setColor(Color.RED);
		font.getData().setScale(4.0f, 4.0f);

        // create 256 wide, 128 height using 8 bits for Red, Green, Blue and Alpha
        pixmap = new Pixmap(256, 128, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();

        // Draw two lines forming an X
        pixmap.setColor(Color.BLACK);
        pixmap.drawLine(0, 0, pixmap.getWidth() - 1, pixmap.getHeight() - 1);
        pixmap.drawLine(0, pixmap.getHeight() - 1, pixmap.getWidth() - 1, 0);

        // Draw a circle in the middle
        pixmap.setColor(Color.YELLOW);
        pixmap.drawCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2,
				pixmap.getHeight() / 2 - 1);

        // create a sprite, that is a texture
        sprite = new Sprite(new Texture(pixmap));


		textureAtlas = new TextureAtlas(Gdx.files.internal("spritesheet.atlas"));
		animation = new Animation<>(0.1f, textureAtlas.getRegions());
		TextureRegion textureRegion = textureAtlas.getRegions().get(0); 
		planesTextureWidth = textureRegion.getRegionWidth(); 
		planesTextureHeight = textureRegion.getRegionHeight();

		TextureRegion[] rotateUpFrames = new TextureRegion[10]; 
		for (int i = 0; i < rotateUpFrames.length; i++) { 
			rotateUpFrames[i] = textureAtlas.findRegion(String.format("00%02d", i + 1));
		} 
		rotateUpAnimation = new Animation <>(0.1f, rotateUpFrames);


		int logLevel = Application.LOG_DEBUG;
		Gdx.app.setLogLevel(logLevel);
		Gdx.app.log(appStateTag, "onCreate");
		//Gdx.app.debug(appStateTag, "onCreate");
		//Gdx.app.error(appStateTag, "onCreate");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(img, screenWidth / 2f - img.getWidth() / 2f,
				screenHeight / 2f - img.getHeight() / 2f);

		font.draw(batch, "Hello libGDX - Tutorial 4", 50, screenHeight - 50);

        sprite.setPosition(0, screenHeight / 2f - sprite.getHeight() / 2);
        sprite.draw(batch);
        sprite.setPosition(screenWidth - sprite.getWidth(),
                screenHeight / 2f - sprite.getHeight() / 2);
        sprite.draw(batch);

		elapsedTime += Gdx.graphics.getDeltaTime();
		batch.draw(animation.getKeyFrame(elapsedTime , true),
				screenWidth / 4f - planesTextureWidth / 2f,
				screenHeight / 2f - planesTextureHeight / 2f);

		batch.draw(rotateUpAnimation.getKeyFrame(elapsedTime , true),
				screenWidth * 3 / 4f - planesTextureWidth / 2f,
				screenHeight / 2f - planesTextureHeight / 2f);

		batch.end();

		//Gdx.app.log(appStateTag, "onRender");
		//Gdx.app.debug(appStateTag, "onRender");
		//Gdx.app.error(appStateTag, "onRender");
	}

	@Override
	public void pause() {
		super.pause();

		Gdx.app.log(appStateTag, "onPause");
		//Gdx.app.debug(appStateTag, "onPause");
		//Gdx.app.error(appStateTag, "onPause");
	}

	@Override
	public void resume() {
		super.resume();

		Gdx.app.log(appStateTag, "onResume");
		//Gdx.app.debug(appStateTag, "onResume");
		//Gdx.app.error(appStateTag, "onResume");
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		font.dispose();
		pixmap.dispose();
		textureAtlas.dispose();

		Gdx.app.log(appStateTag, "onDispose");
		//Gdx.app.debug(appStateTag, "onDispose");
		//Gdx.app.error(appStateTag, "onDispose");
	}
}
