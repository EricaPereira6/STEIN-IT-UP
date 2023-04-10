package com.mygdx.cowboygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyCowboyGame extends Game {
	int screenWidth;
	int screenHeight;

	AssetsLoader assets;

	@Override
	public void create () {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		assets = new AssetsLoader();

		log("onCreate");

		setInitialScreen();
	}

	private void setInitialScreen() {
		setScreen(new GameScreenCowboy(this));
	}

	@Override
	public void dispose () {
		assets.dispose();

		log("onDispose");
	}

	static void log (String message) {
		Gdx.app.log(" MyCowboyGame ", message);
	}

}
