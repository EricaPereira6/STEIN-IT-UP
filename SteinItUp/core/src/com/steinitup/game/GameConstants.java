package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameConstants {

    public static  final String defaultImageProfile = "userhead.png";

    public static final String skin = "uiskin.json";
    public static final String fontTexture = "candarahiero.png";
    public static final String font = "candarahiero.fnt";

    public static final float screenWidth = Gdx.graphics.getWidth();
    public static final float screenHeight = Gdx.graphics.getHeight();
    public static final float centerX = screenWidth / 2f;
    public static final float centerY = screenHeight / 2f;

    public enum Colors {
        BACKGROUND(new Color(96f/255f, 199f/255f, 206f/255f, 255f/255f)),
        BUTTON_LOGIN(new Color(180f/255f, 180f/255f, 180f/255f, 255f/255f)),
        STAGE_BATCH_CLEARANCE(new Color(240f/255f, 240f/255f, 240f/255f, 255f/255f)),
        TITLE_LOGIN_BG(new Color(185f/255f, 132f/255f, 215f/255f, 255f/255f)),

        FOCUS_BG_COLOR(new Color(226f/255f,192f/255f,255f/255f,255f/255f)),
        MEMORY_BG_COLOR(new Color(250f/255f,255f/255f,155f/255f,255f/255f)),
        MATH_BG_COLOR(new Color(160f/255f,230f/255f,250f/255f,255f/255f)),
        LOGIC_BG_COLOR(new Color(195f/255f,255f/255f,180f/255f,255f/255f)),

        FOCUS_BG_DARKER(new Color(189f/255f,135f/255f,236f/255f,255f/255f)),
        MEMORY_BG_DARKER(new Color(250f/255f,215f/255f,130f/255f,255f/255f)),
        MATH_BG_DARKER(new Color(120f/255f,200f/255f,250f/255f,255f/255f)),
        LOGIC_BG_DARKER(new Color(105f/255f,235f/255f,90f/255f,255f/255f)),

        PROFILE_BACKGROUND(new Color(255f/255f, 230f/255f, 80f/255f, 255f/255f)),

        RULES_BG(new Color(0, 0, 0, 160f/255f)),
        NO_BUTTON_BG(new Color(255f/255f, 162f/255f, 162f/255f, 255f/255f)),
        YES_BUTTON_BG(new Color(162f/255f, 255f/255f, 162f/255f, 255f/255f)),
        NO_BUTTON(new Color(188f/255f, 86f/255f, 86f/255f, 255f/255f)),
        YES_BUTTON(new Color(75f/255f, 188f/255f, 75f/255f, 255f/255f)),

        COLOR_GAME_RED(new Color(255f/255f, 140f/255f, 140f/255f, 255f/255f)),
        COLOR_GAME_GREEN(new Color(140f/255f, 225f/255f, 170f/255f, 255f/255f)),
        COLOR_GAME_BLUE(new Color(140f/255f, 220f/255f, 255f/255f, 255f/255f)),
        COLOR_GAME_YELLOW(new Color(250f/255f, 225f/255f, 140f/255f, 255f/255f)),

        FADED_WHITE(new Color(1,1,1,220f/255f)),
        FADED_GREY(new Color(0.9f, 0.9f, 0.9f, 0.75f)),

        COMING_SOON_COLOR(new Color(0.75f,0.75f,0.75f,1)),
        COMING_SOON_DARKER(new Color(0.4f,0.4f,0.4f,1));

        private Color value;
        Colors(Color value) {
            this.value = value;
        }

        public Color getColor() {
            return value;
        }
    }

    public enum Themes {
        MATH("Math"),
        FOCUS("Focus"),
        LOGIC("Logic"),
        MEMORY("Memory");

        private String value;
        Themes(String value) {
            this.value = value;
        }

        public String getThemeName() {
            return value;
        }

        public Themes getThemeByName(String name) {
            for (Themes theme : Themes.values()) {
                if (theme.getThemeName().equals(name)) {
                    return theme;
                }
            }
            return this;
        }
    }

    public enum Games {
        TURTLES_SEQUENCE       ("Turtles' Sequence"     , Themes.MEMORY, false),
        FRACTIONARY            ("Frac-tionary"          , Themes.MATH  , true),
        WORLDS_PERCENTAGE      ("World's Percentage"    , Themes.MATH  , false),
        OPERATE_THE_VIRUS      ("Operate the Virus"     , Themes.MATH  , true),
        TIME_TO_LISTEN         ("Time to Listen"        , Themes.MEMORY, false),
        SQUARES_OF_MEMORY      ("Squares of Memory"     , Themes.MEMORY, true),
        PAINTING_COLORS        ("Painting Colors"       , Themes.FOCUS , true),
        FAST_FORMS             ("Fast Forms"            , Themes.FOCUS , false),
        JUMPING_TO_CONCLUSIONS ("Jumping to Conclusions", Themes.LOGIC , true);

        private String value;
        private Themes theme;
        private boolean comingSoon;
        Games(String value, Themes theme, boolean comingSoon) {
            this.value = value;
            this.theme = theme;
            this.comingSoon = comingSoon;
        }

        public String getGameName() {
            return value;
        }
        public Themes getGameTheme() {
            return theme;
        }
        public boolean getComingSoon() { return comingSoon; }
        public Games getGameByName(String name) {
            for (Games game : Games.values()) {
                if (game.getGameName().equals(name)) {
                    return game;
                }
            }
            return this;
        }
    }

    public enum Levels {
        BEGINNER("Beginner", 1),
        INTERMEDIATE("Intermediate", 2),
        ADVANCED("Advanced", 3),
        SPECIALIST("Specialist", 4);

        private String value;
        private int level;
        Levels(String value, int level) {
            this.value = value;
            this.level = level;
        }

        public String getLevelName() {
            return value;
        }

        public int getLevel() {
            return level;
        }

        public Levels getLevelByName(String name) {
            for (Levels level : Levels.values()) {
                if (level.getLevelName().equals(name)) {
                    return level;
                }
            }
            return Levels.SPECIALIST;
        }
    }

    public enum Languages {
        ENGLISH("english"),
        PORTUGUESE("portuguese"),
        FRENCH("french");

        private String value;
        Languages(String value) {
            this.value = value;
        }

        public String getName() {
            return value;
        }
    }

    static String getMiniDescription(Languages language, Games game) {
        String description = "";

        switch (game) {
            case FAST_FORMS:
                if (language == Languages.ENGLISH) {
                    description = "*  Fast Forms is a game where you need to be fast. " +
                            " You just have to answer if the shape you see is the same as the last one. ";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case JUMPING_TO_CONCLUSIONS:
            case WORLDS_PERCENTAGE:
                if (language == Languages.ENGLISH) {
                    description = "*  In the screen will appear three numbers, each and three" +
                            " percentages. You need to answer how much is the number applying" +
                            " the shown percentage.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case SQUARES_OF_MEMORY:
            case FRACTIONARY:
            case PAINTING_COLORS:
            case OPERATE_THE_VIRUS:
                break;
            case TURTLES_SEQUENCE:
                if (language == Languages.ENGLISH) {
                    description = "*  You have to pay attention to the color of the turtles," +
                            " how many there are and in what sequence they appear.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case TIME_TO_LISTEN:
                if (language == Languages.ENGLISH) {
                    description = "*  Now is the time to listen carefully. Pay attention to" +
                            " what you are hearing and match the options to the persons picture.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
        }
        return description;
    }

    public static String getDescription(Languages language, Games game) {
        String description = "";

        switch (game) {
            case FAST_FORMS:
                if (language == Languages.ENGLISH) {
                    description = "*  Fast Forms is a game where you need to be fast to earn more points. " +
                            "*  For level 1, you need to answer if the shape you see is the same as the last one. " +
                            "*  At level 2 you need to take the color of the form into account. " +
                            "*  For level 3, the game will iterate between the comparison of shape, color or both. " +
                            "*  In level 4, comparisons on specific shapes and colors will be added.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case JUMPING_TO_CONCLUSIONS:
            case SQUARES_OF_MEMORY:
            case OPERATE_THE_VIRUS:
            case PAINTING_COLORS:
            case FRACTIONARY:
                break;
            case WORLDS_PERCENTAGE:
                if (language == Languages.ENGLISH) {
                    description = "*  Three numbers will appear on the screen, each with three percentages.  " +
                            "*  You will need to answer how much is the number resulting from the percentage applied " +
                            "to the number on the screen.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case TURTLES_SEQUENCE:
                if (language == Languages.ENGLISH) {
                    description = "*  In this game you'll have to pay attention to the color and the number of turtles that" +
                            " appear on the screen. " +
                            "*  The turtles will appear on the screen several times in groups of 1 to 8 randomly. " +
                            "*  Each group of turtles has different colors. " +
                            "*  You'll either need to answer in what sequence of colors they appeared or in what sequence " +
                            "of number of turtles per group they appeared.";
                    break;
                }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
            case TIME_TO_LISTEN:
                if (language == Languages.ENGLISH) {
                description = "*  Now is the time to listen carefully. " +
                        "*  Pay attention to what the person is saying about himself. " +
                        "You'll see the person's photo as they speak. " +
                        "*  Then match the options that appear in the screen to the person's photo.";
                break;
            }
                if (language == Languages.PORTUGUESE) {
                    description = "";
                    break;
                }
                description = "";
                break;
        }
        return description;
    }
}
