package com.mygdx.facesgame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends Game {
	int screenWidth;
	int screenHeight;

	AssetsLoader assets;
	int assetsWidth;
	int assetsHeight;

	@Override
	public void create () {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		assets = new AssetsLoader();
		//assetsWidth = 300;
		//assetsHeight = 300;
		assetsWidth = 150;
		assetsHeight = 150;
		//log(screenHeight + " x " + screenWidth);  // 720 x 1280 -> my device
		//assetsWidth = 225;
		//assetsHeight = 225;
		//log(screenHeight + " x " + screenWidth);  // 1080 x 2028 -> my emulator PIXEL 3

		log("onCreate");

		setInitialScreen();
	}

	void setScreenFaces () {
		setScreen(new GameScreenFaces(this));
	}

	void setScreenBox2D () {
		setScreen(new GameScreenBox2D(this));
	}

	void setInitialScreen () {
		setScreen(new InitialScreen(this));
	}

	@Override
	public void dispose () {
		assets.dispose();

		log("onDispose");
	}

	static void log (String message) {
		Gdx.app.log(" MyGdxGame ", message);
	}
}
