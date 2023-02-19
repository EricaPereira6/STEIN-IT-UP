package com.steinitup.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class SteinItUpGame extends Game {
	AssetsLoader assetsLoader;
	DatabaseAccess databaseAccess;
	RandomGames randomGames;

	@Override
	public void create () {
		assetsLoader = new AssetsLoader();
		databaseAccess = new DatabaseAccess();
		randomGames = new RandomGames();

		MyPreference.setPreferencesRef();
		MyPreference.setMusic(false);

		if (MyPreference.isUsernameEmpty()) {
			setInitialScreen();
		}
		else {
			setHomePage();
		}
	}

	void setInitialScreen() { setScreen(new InitialScreen(this)); }
	void setScreenLogin() { setScreen(new Login(this)); }
	void setScreenRegister() { setScreen(new Register(this)); }
	void setHomePage() { setScreen(new HomePage(this)); }

	void setProfilePage() { setScreen(new ProfilePage(this)); }
	void setStatsPage() { setScreen(new StatsPage(this)); }
	void setAboutPage() { setScreen(new AboutPage(this)); }
	void setHelpPage() { setScreen(new HelpPage(this)); }
	void setMoreGamesPage() { setScreen(new MoreGamesScreen(this)); }

	void setInitialGameScreen(GameConstants.Games game) {
		if (game.getComingSoon()) {
			setScreen(new ComingSoonPage(this, game));
		} else {
			setScreen(new InitialGameScreen(this, game));
		}
	}
	void setFinalGameScreen(GameConstants.Games game, boolean gameOver, int finalScore) {
		setScreen(new FinalGameScreen(this, game, gameOver, finalScore));
	}
	void setLevelPage(GameConstants.Games game, int level) {
		setScreen(new LevelPage(this, game, level));
	}

	void setGameJumpingToConclusions(int level) { }
	void setGameTurtlesSequence(int level) { setScreen(new TurtlesSequenceGame(this, level)); }
	void setGameTimeToListen(int level) { setScreen(new TimeToListenGame(this, level)); }
	void setGameSquaresOfMemory (int level) { }
	void setGameOperateTheVirus(int level) { }
	void setGameWorldsPercentage(int level) { setScreen(new WorldsPercentage(this, level)); }
	void setGameFractionary(int level) { }
	void setGameFastForms(int level) { setScreen(new FastFormsGame(this, level)); }
	void setGamePaintingColors(int level) { }


	@Override
	public void dispose () {
		assetsLoader.dispose();
		randomGames.dispose();
	}

	static void log (String message) {
		Gdx.app.log(" MyGdxGame ", message);
	}
}