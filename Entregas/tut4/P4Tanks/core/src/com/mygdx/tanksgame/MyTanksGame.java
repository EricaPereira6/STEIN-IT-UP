package com.mygdx.tanksgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyTanksGame extends Game {
	int screenWidth;
	int screenHeight;

	AssetsLoader assets;

	float assetsSizeFactor;

	@Override
	public void create () {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		assets = new AssetsLoader();
		assetsSizeFactor = 0.5f;

		log("onCreate");

		setInitialScreen();
	}

	private void setInitialScreen() {
		setScreen(new GameScreenLordOfTanks(this));
	}

	@Override
	public void dispose () {
		assets.dispose();

		log("onDispose");
	}

	static void log (String message) {
		Gdx.app.log("MyTanksGame", message);
	}
}
