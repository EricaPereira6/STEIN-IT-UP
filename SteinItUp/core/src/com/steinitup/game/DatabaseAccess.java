package com.steinitup.game;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.mk5.gdx.fireapp.GdxFIRDatabase;
import pl.mk5.gdx.fireapp.distributions.DatabaseDistribution;
import pl.mk5.gdx.fireapp.functional.Consumer;

class DatabaseAccess {

    private ArrayList<String> usernames;
    private HashMap<String, Integer> playerLevels, gameLevels, bestScores;
    private String email, photo;

    private LevelManagement levelManagement;

    DatabaseAccess () {
        usernames = new ArrayList<>();
        playerLevels = new HashMap<>();
        gameLevels = new HashMap<>();
        email = "";
        photo = "";

        levelManagement = new LevelManagement();

        updateUsernamesList();
        updateGameLevels();
    }

    private void updateUsernamesList() {

        GdxFIRDatabase.promise()
                .then(GdxFIRDatabase.inst().inReference("/stein/users").onDataChange(List.class))
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {

                        usernames = new ArrayList<>();

                        for (Object o : list) {
                            o = o.toString().substring(1, o.toString().length() - 1);
                            String[] array = o.toString().split(", ");

                            for (String element : array) {
                                String[] type = element.split("=");
                                if (type[0].equals("username")) {
                                    usernames.add(type[1]);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    boolean isUsernameUnique(String username) {

        boolean unique = true;

        for (String user : usernames) {

              if (user.equals(username)) {
                    unique = false;
                    break;
              }
        }

        return unique;
    }

    void setUsernameByEmail(final String email) {

        GdxFIRDatabase inst = GdxFIRDatabase.inst();
        DatabaseDistribution ref = inst.inReference("/stein/users");

        ref.readValue(List.class)
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {
                        String userAux = "";
                        for (int o = 0; o < list.size(); o++) {

                            String attrs = list.get(o).toString().substring(1, list.get(o).toString().length() - 1);
                            String[] array = attrs.split(", ");

                            for (String element : array) {
                                String[] type = element.split("=");
                                if (type[0].equals("username")) {
                                    userAux = type[1];
                                }
                                else if (type[0].equals("email") && type[1].equals(email)) {
                                    o = list.size();
                                }
                            }
                        }
                        MyPreference.setUsername(userAux);
                    }
                });
    }

    void addUser(String username, String email) {

        Map<String, String> userData = new HashMap<>();

        //ref.removeValue(); // removes user
        // empty hashmap also removes user
        // not including this three values every time, deletes the values not added to the hashmap
        userData.put("username", username);
        userData.put("email", email);
        userData.put("photo", GameConstants.defaultImageProfile);

        GdxFIRDatabase inst = GdxFIRDatabase.inst();
        DatabaseDistribution refUser = inst.inReference("/stein/users/" + username);
        refUser.setValue(userData);

        for (GameConstants.Games game : GameConstants.Games.values()) {
            addPlayedGameScoreDB(username, game, 0);
        }

        MyPreference.setUsername(username);
    }

    void addPlayedGameScoreDB(String username, GameConstants.Games game, int score) {

        Date currentTime = Calendar.getInstance().getTime();

        Map<String, String> userPlay = new HashMap<>();
        userPlay.put("score", Integer.toString(score));
        userPlay.put("date", currentTime.toString());
        userPlay.put("game", game.getGameName());

        String gameName = game.getGameName();
        String gameTheme = game.getGameTheme().getThemeName();

        GdxFIRDatabase inst = GdxFIRDatabase.inst();
        DatabaseDistribution ref = inst.inReference("/stein/user_game/"
                + username + "/"
                + gameName + "/"
                + currentTime);

        ref.setValue(userPlay);

        int totalPoints = levelManagement.updatePlayerScores(game, score, gameLevels);
        String levelName = levelManagement.getThemeLevel().get(GameConstants.Themes.FOCUS.getThemeByName(gameTheme));

        updatePlayerLevelDB(username, gameName, totalPoints);
        updatePlayerThemeLevelBD(username, gameTheme, levelName);
    }

    void updatePlayerLevelDB(String username, String gameName, int totalPoints) {

        Map<String, String> userLevel = new HashMap<>();
        userLevel.put("points", Integer.toString(totalPoints));
        userLevel.put("name", gameName);

        GdxFIRDatabase inst = GdxFIRDatabase.inst();
        DatabaseDistribution ref = inst.inReference("/stein/user_level/"
                + username + "/"
                + gameName);

        ref.setValue(userLevel);
    }

    void updatePlayerThemeLevelBD(String username, String themeName, String levelName) {

        Map<String, String> userLevel = new HashMap<>();
        userLevel.put("level", levelName);
        userLevel.put("name", themeName);

        GdxFIRDatabase inst = GdxFIRDatabase.inst();
        DatabaseDistribution ref = inst.inReference("/stein/user_theme/"
                + username + "/"
                + themeName);

        ref.setValue(userLevel);
    }

    void setEmailAndPhotoByUsername(final String username) {

        GdxFIRDatabase.promise()
                .then(GdxFIRDatabase.inst().inReference("/stein/users").onDataChange(List.class))
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {

                        for (int o = 0; o < list.size(); o++) {

                            String attrs = list.get(o).toString().substring(1, list.get(o).toString().length() - 1);
                            String[] array = attrs.split(", ");

                            for (String element : array) {
                                String[] type = element.split("=");
                                if (type[0].equals("email")) {
                                    email = type[1];
                                }
                                else if (type[0].equals("photo")) {
                                    photo = type[1];
                                }
                                else if(type[0].equals("username") && type[1].equals(username)) {
                                    o = list.size();
                                }

                            }

                        }
                    }
                });
    }

    String getEmail() {
        return email;
    }

    String getPhoto() {
        return photo;
    }

    void updateGameLevels() {
        GdxFIRDatabase.promise()
                .then(GdxFIRDatabase.inst().inReference("/stein/levels/").onDataChange(List.class))
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {

                        gameLevels = new HashMap<>();

                        for (Object o : list) {

                            o = o.toString().substring(1, o.toString().length() - 1);
                            String[] array = o.toString().split(", ");

                            String[] type1 = array[0].split("=");
                            String[] type2 = array[1].split("=");
                            if (type1[0].equals("name")) {
                                gameLevels.put(type1[1], Integer.parseInt(type2[1]));
                            }
                            else {
                                gameLevels.put(type2[1], Integer.parseInt(type1[1]));
                            }
                        }
                    }
                });
    }

    void updatePlayerLevels(String username) {

        GdxFIRDatabase.promise()
                .then(GdxFIRDatabase.inst().inReference("/stein/user_level/" + username).onDataChange(List.class))
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {

                        playerLevels = new HashMap<>();

                        for (Object o : list) {

                            o = o.toString().substring(1, o.toString().length() - 1);
                            String[] array = o.toString().split(", ");

                            String[] type1 = array[0].split("=");
                            String[] type2 = array[1].split("=");
                            if (type1[0].equals("name")) {
                                playerLevels.put(type1[1], Integer.parseInt(type2[1]));
                            }
                            else {
                                playerLevels.put(type2[1], Integer.parseInt(type1[1]));
                            }
                        }

                        levelManagement.updateGamesLevels(playerLevels, gameLevels);
                        levelManagement.updateThemesLevels(gameLevels);
                    }
                });
    }

    void updatePlayerBestScore(String username) {

        GdxFIRDatabase.promise()
                .then(GdxFIRDatabase.inst().inReference("/stein/user_game/" + username).onDataChange(List.class))
                .then(new Consumer<List>() {
                    @Override
                    public void accept(List list) {

                        bestScores = new HashMap<>();

                        for (Object o : list) {

                            String[] auxStr, finalStr;

                            int size = o.toString().length();
                            String gameName = "";
                            int scoreAux = 0;

                            String str = o.toString().substring(1, size);
                            String[] strs = str.split("\\{");

                            for (int i = 1; i < strs.length; i++) {

                                auxStr = strs[i].split("\\}");
                                finalStr = auxStr[0].split(", ");

                                for (String element : finalStr) {
                                    String[] type = element.split("=");
                                    if (type[0].equals("game")) {
                                        gameName = type[1];
                                        if (bestScores.get(gameName) == null) {
                                            bestScores.put(type[1], scoreAux);
                                        }
                                    }
                                    else if (type[0].equals("score")) {
                                        if (gameName.equals("")) {
                                            scoreAux = Integer.parseInt(type[1]);
                                        }else if (bestScores.get(gameName) < Integer.parseInt(type[1])) {
                                            bestScores.put(gameName, Integer.parseInt(type[1]));
                                        }
                                    }

                                }
                            }
                        }
                    }
                });
    }

    HashMap<GameConstants.Themes, String> getUserThemeLevels() {
        return levelManagement.getThemeLevel();
    }
    ArrayList<GameConstants.Games> getThemeGames(GameConstants.Themes theme) {
        return levelManagement.getThemeGames(theme);
    }
    HashMap<GameConstants.Games, String> getPlayerLevel() {
        return levelManagement.getPlayerLevel();
    }
    HashMap<GameConstants.Games, Integer> getScores() {
        return levelManagement.getScores();
    }

    HashMap getGameLevels() {
        return gameLevels;
    }

    HashMap getPlayerLevels() {
        return playerLevels;
    }

    int getBestScore(GameConstants.Games game) {
        return bestScores.get(game.getGameName());
    }

    int getCurrentLevel(GameConstants.Games game) {

        int level = 1;

        if (playerLevels.size() == 0 || gameLevels.size() == 0) {
            return 0;
        }
        int score = playerLevels.get(game.getGameName());

        for (String s : gameLevels.keySet()) {
            if (score >= gameLevels.get(s) && level < gameLevels.get(s)) {
                level = GameConstants.Levels.SPECIALIST.getLevelByName(s).getLevel();
            }
        }

        return level;
    }
}

class LevelManagement {

    private ArrayList<GameConstants.Games> memoryGames, focusGames, logicGames, mathGames;
    private HashMap<GameConstants.Games, Integer> scores;
    private HashMap<GameConstants.Games, String> playerLevel;
    private HashMap<GameConstants.Themes, String> themeLevel;

    LevelManagement() {
        memoryGames = new ArrayList<>();
        focusGames = new ArrayList<>();
        logicGames = new ArrayList<>();
        mathGames = new ArrayList<>();

        countThemeGames();

        scores = new HashMap<>();
        playerLevel = new HashMap<>();
        themeLevel = new HashMap<>();
    }

    ArrayList<GameConstants.Games> getThemeGames(GameConstants.Themes theme) {
        switch (theme) {
            case LOGIC:
                return logicGames;
            case FOCUS:
                return focusGames;
            case MATH:
                return mathGames;
            case MEMORY:
                return memoryGames;
        }
        return null;
    }
    HashMap<GameConstants.Games, String> getPlayerLevel() {
        return playerLevel;
    }
    HashMap<GameConstants.Themes, String> getThemeLevel() {
        return themeLevel;
    }
    HashMap<GameConstants.Games, Integer> getScores() { return scores; }

    void updateGamesLevels(HashMap<String, Integer> playerLevels, HashMap<String, Integer> gameLevels) {

        for (GameConstants.Games game : GameConstants.Games.values()) {
            int score = playerLevels.get(game.getGameName());

            scores.put(game, score);
            playerLevel.put(game, getLevelName(score, gameLevels));
        }
    }
    void updateThemesLevels(HashMap<String, Integer> gameLevels) {

        for (GameConstants.Themes theme : GameConstants.Themes.values()) {
            themeLevel.put(theme, calculateThemeLevel(theme, gameLevels));
        }
    }

    private void countThemeGames() {
        for(GameConstants.Games game : GameConstants.Games.values()) {
            switch (game.getGameTheme()) {
                case MEMORY:
                    memoryGames.add(game);
                    break;
                case LOGIC:
                    logicGames.add(game);
                    break;
                case FOCUS:
                    focusGames.add(game);
                    break;
                case MATH:
                    mathGames.add(game);
                    break;
            }
        }
    }

    private String calculateThemeLevel(GameConstants.Themes theme, HashMap<String, Integer> gameLevels) {
        int average = 0;
        switch (theme) {
            case MEMORY:
                for (GameConstants.Games game: memoryGames) {
                    average += scores.get(game);
                }
                average = average / memoryGames.size();
                break;
            case LOGIC:
                for (GameConstants.Games game: logicGames) {
                    average += scores.get(game);
                }
                average = average / logicGames.size();
                break;
            case FOCUS:
                for (GameConstants.Games game: focusGames) {
                    average += scores.get(game);
                }
                average = average / focusGames.size();
                break;
            case MATH:
                for (GameConstants.Games game: mathGames) {
                    average += scores.get(game);
                }
                average = average / mathGames.size();
                break;
        }

        return getLevelName(average, gameLevels);
    }

    private String getLevelName(int score, HashMap<String, Integer> gameLevels) {

        String strKeys = gameLevels.keySet().toString();
        strKeys = strKeys.substring(1, strKeys.length() - 1);
        String[] keys = strKeys.split(", ");

        int auxLevel = 0;
        int index = 0;
        String levelName = "Beginner";

        for (int level : gameLevels.values()) {
            if (score > level && auxLevel < level) {
                auxLevel = level;
                levelName = keys[index];
            }
            index++;
        }

        return levelName;
    }

    int updatePlayerScores(GameConstants.Games game, int score, HashMap<String, Integer> gameLevels) {

        int finalScore = scores.get(game) + score;

        scores.put(game, finalScore);
        playerLevel.put(game, getLevelName(finalScore, gameLevels));

        updateThemesLevels(gameLevels);

        return finalScore;
    }
}