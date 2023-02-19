package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.ArrayList;

public class MyPreference {

    static Preferences prefs;

    final static String usernameKey = "username";
    final static String dateKey = "date";
    final static String game1Key = "game1";
    final static String game2Key = "game2";
    final static String game3Key = "game3";
    final static String premiumKey = "premium";
    final static String musicKey = "music";
    final static String soundKey = "sound";

    static void setPreferencesRef () {
        prefs = Gdx.app.getPreferences("MyPreferences");
        prefs.flush();
    }

    static void setPrefsDefaultValues() {
        prefs.putString(usernameKey, "");
        prefs.putString(dateKey, "");
        prefs.putString(game1Key, "");
        prefs.putString(game2Key, "");
        prefs.putString(game3Key, "");
        prefs.putBoolean(premiumKey, false);
        prefs.putBoolean(musicKey, true);
        prefs.putBoolean(soundKey, true);
        prefs.flush();
    }

    static boolean isUsernameEmpty() {
        return prefs.getString(usernameKey, "").equals("");
    }

    static String getUsername() {
        return prefs.getString("username");
    }

    static void setUsername(String username) {
        prefs.putString(usernameKey, username);
        prefs.flush();
    }

    static boolean isDateEmpty() {
        return prefs.getString(dateKey, "").equals("");
    }

    static String getDate() {
        return prefs.getString(dateKey);
    }

    static void setDate(String date) {
        prefs.putString(dateKey, date);
        prefs.flush();
    }

    static ArrayList<GameConstants.Games> getGames() {
        ArrayList<GameConstants.Games> games = new ArrayList<>();

        String name = prefs.getString(game1Key);
        games.add(GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name));
        name = prefs.getString(game2Key);
        games.add(GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name));
        name = prefs.getString(game3Key);
        games.add(GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name));

        return games;
    }

    static void setGames(ArrayList<GameConstants.Games> games) {
        prefs.putString(game1Key, games.get(0).getGameName());
        prefs.putString(game2Key, games.get(1).getGameName());
        prefs.putString(game3Key, games.get(2).getGameName());
        prefs.flush();
    }

    static GameConstants.Games getGame1() {

        String name = prefs.getString(game1Key);

        return GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name);
    }

    static GameConstants.Games getGame2() {

        String name = prefs.getString(game2Key);

        return GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name);
    }

    static GameConstants.Games getGame3() {

        String name = prefs.getString(game3Key);

        return GameConstants.Games.TURTLES_SEQUENCE.getGameByName(name);
    }

    static boolean isPremium() {
        return prefs.getBoolean(premiumKey);
    }

    static void setPremium(boolean bool) {
        prefs.putBoolean(premiumKey, bool);
        prefs.flush();
    }

    static boolean isMusicOn() { return prefs.getBoolean(musicKey); }

    static void setMusic(boolean play) {
        prefs.putBoolean(musicKey, play);
        prefs.flush();
    }

    static boolean isSoundOn() { return prefs.getBoolean(soundKey); }

    static void setSound(boolean play) {
        prefs.putBoolean(soundKey, play);
        prefs.flush();
    }
}
