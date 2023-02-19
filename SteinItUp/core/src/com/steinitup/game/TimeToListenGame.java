package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimeToListenGame extends MiniGame implements Screen, InputProcessor {

    private final SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;
    private RandomGames randomGames;
    private int level;
    private SpriteBatch batch;
    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font;
    private float op11Width, op12Width, op13Width, op14Width,
            op21Width, op22Width, op23Width, op24Width,
            op31Width, op32Width, op33Width, op34Width,
            op41Width, op42Width, op43Width, op44Width;

    private int pictureSize, numPeople, numAttrs;
    private Random random;
    private ArrayList<String> work, order, type, chosenPeople;

    private int index, personTime;
    private long timestamp;

    private int numPictures, size, height;
    private int indexDisplayPicture;
    private ArrayList<Integer> indexesDisplayPictures, eliminateOption;
    private Attrs[] options;
    private String[] op1, op2, op3, op4;
    private int question, opPicked, x1, y1, x2, y2, x3, y3, x4, y4, h, w;
    private int numAnswers;

    private enum Attrs {
        MARKORDER ("markorder"      , "Mark"   , "french fries"     ,"lemon juice"      , 8000),
        JOANNAORDER ("joannaorder"  ,"Joanna"  , "potato chips"     , "tuna sandwich"   , 7000),
        GUSTAFFORDER ("gustafforder", "Gustaff", "beer"             , "chicken bites"   , 6000),
        SOPHIAORDER ("sophiaorder"  , "Sophia" , "french fries"     , "coffee"          , 7000),
        MARGEORDER ("margeorder"    , "Marge"  , "vegetarian burger", "strawberry lemon", 8000),
        LANCEORDER ("lanceorder"    , "Lance"  , "turkey sandwich"  , "coca-cola"       , 8000),
        KEVINORDER ("kevinorder"    , "Kevin"  , "potato chips"     , "chicken bites"   , 7000),
        GINAORDER ("ginaorder"      , "Gina"   , "beer"             , "turkey burger"   , 8000),
        LUNAORDER ("lunaorder"      , "Luna"   , "beer"             , "dried pickles"   , 7000),
        KALVINWORK ("kalvinwork"    , "Kalvin" , "new employee"     , "communication"   , 8000),
        TINAWORK ("tinawork"        , "Tina"   , "secretary"        , "fishing"         , 8000),
        LINAWORK("linawork"         , "Lina"   , "same department"  , "lunch at 12:00"  , 9000),
        MARKOWORK ("markowork"      , "Marko"  , "secretary"        , "lunch at 12:30"  , 10000),
        INNAWORK ("innawork"        , "Inna"   , "storage"          , "coordinator"     , 9000),
        GUSTAFFWORK ("gustaffwork"  , "Gustaff", "sushi"            , "lunch at 13:00"  , 11000),
        MARIAWORK ("mariawork"      , "Maria"  , "Donny's Pizza"    , "coordinator"     , 10000),
        KEVINWORK ("kevinwork"      , "Kevin"  , "cafeteria"        , "scuba-diving"    , 9000),
        LANCY ("lancywork"          , "Lancy"  , "storage"          , "lunch at 13:00"  , 10000);

        private String finalName, name, attr1, attr2;
        private int audioTime;
        Attrs(String fileName, String name, String attr1, String attr2, int audioTime) {
            this.finalName = fileName;
            this.name = name;
            this.attr1 = attr1;
            this.attr2 = attr2;
            this.audioTime = audioTime;
        }
        public String getFileName() {
            return finalName;
        }
        public String getName() {
            return name;
        }
        public String getAttr1() {
            return attr1;
        }
        public String getAttr2() {
            return attr2;
        }
        public int getAudioTime() { return audioTime; }
        public Attrs getAttrsByFileName(String finalName) {
            for (Attrs attrs : Attrs.values()) {
                if (attrs.getFileName().equals(finalName)) {
                    return attrs;
                }
            }
            return this;
        }
    }

    TimeToListenGame(SteinItUpGame steinItUpGame, int level) {
        super(steinItUpGame, GameConstants.Games.TIME_TO_LISTEN, level, 1, 00, 4);
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.randomGames = steinItUpGame.randomGames;
        this.level = level;

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.3f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        // ----------- define people -----------
        random = new Random();
        defineVariables();
        definePeople();
        defineOptions();
    }

    private void defineVariables() {
        pictureSize = (int) (400 * GameConstants.screenWidth) / 720;
        numAttrs = 2;
        index = -1;
        personTime = 0;
        timestamp = 0;

        if (level == 1) {
            numPeople = 2;
        } else if (level == 2) {
            numPeople = 3;
        } else if (level == 3) {
            numPeople = 4;
        } else {
            numPeople = 5;
        }

        work = new ArrayList<>();
        order = new ArrayList<>();

        for (String name : assetsLoader.names) {
            if (name.contains("work")) {
                work.add(name);
            }
            else if (name.contains("order")) {
                order.add(name);
            }
        }
    }

    private void definePeople() {
        type = new ArrayList<>();
        chosenPeople = new ArrayList<>();
        int rnd = random.nextInt(100);
        if (rnd < 50) {
            type = work;
        }
        else {
            type = order;
        }

        for (int i = 0; i < numPeople; i++) {
            chosenPeople.add(type.get(random.nextInt(type.size())));
            if (i != 0) {
                Attrs person1 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(i));
                for (int j = 0; j < i; j++) {
                    Attrs person2 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(j));
                    if (person2.equals(person1) || person2.getAttr1().equals(person1.getAttr1()) ||
                            person2.getAttr2().equals(person1.getAttr2())) {
                        chosenPeople.remove(chosenPeople.size() - 1);
                        i--;
                        break;
                    }
                }
            }
        }
    }

    private void defineOptions() {
        size = 300;
        height = 300;
        indexDisplayPicture = 0;
        indexesDisplayPictures = new ArrayList<>();
        eliminateOption = new ArrayList<>();
        numAnswers = 0;

        if (level == 1) {
            numPictures = 2;
        } else if (level == 2) {
            numPictures = 3;
        } else {
            numPictures = 4;
        }

        question = 1;
        opPicked = 0;
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;
        x3 = 0;
        y3 = 0;
        x4 = 0;
        y4 = 0;
        w = (int) GameConstants.centerX - 45;
        h = 200;
        options = new Attrs[4];
        op1 = new String[4];
        op2 = new String[4];
        op3 = new String[4];
        op4 = new String[4];

        if (level == 1) {
            Attrs p1 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            Attrs p2 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
            options[0] = p1;
            options[1] = p2;
            for (int i = 2; i < 4; i++) {
                options[i] = Attrs.GINAORDER.getAttrsByFileName(type.get(random.nextInt(type.size())));
                for (int j = 0; j < i; j++) {
                    if (options[j].equals(options[i]) || options[j].getAttr1().equals(options[i].getAttr1()) ||
                            options[j].getAttr2().equals(options[i].getAttr2())) {
                        i--;
                        break;
                    }
                }
            }
            List<Attrs> strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op4[0] = p1.getAttr2();
            op4[1] = p2.getName();
            op4[2] = p1.getName();
            op4[3] = p2.getAttr1();
        }
        else if (level == 2) {
            Attrs p1 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            Attrs p2 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
            Attrs p3 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(2));
            options[0] = p1;
            options[1] = p2;
            options[2] = p3;
            for (int i = 3; i < 4; i++) {
                options[i] = Attrs.GINAORDER.getAttrsByFileName(type.get(random.nextInt(type.size())));;
                for (int j = 0; j < i; j++) {
                    if (options[j].equals(options[i]) || options[j].getAttr1().equals(options[i].getAttr1()) ||
                            options[j].getAttr2().equals(options[i].getAttr2())) {
                        i--;
                        break;
                    }
                }
            }
            List<Attrs> strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op4[0] = p3.getAttr2();
            op4[1] = p2.getName();
            op4[2] = p1.getName();
            op4[3] = p2.getAttr1();
        }
        else if (level == 3) {
            Attrs p1 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            Attrs p2 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
            Attrs p3 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(2));
            Attrs p4 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(3));
            options[0] = p1;
            options[1] = p2;
            options[2] = p3;
            options[3] = p4;
            List<Attrs> strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op4[0] = p3.getName();
            op4[1] = p4.getAttr2();
            op4[2] = p1.getAttr1();
            op4[3] = p2.getName();
        }
        if (level == 4) {
            Attrs p1 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            Attrs p2 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
            Attrs p3 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(2));
            Attrs p4 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(3));
            Attrs p5 = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(4));

            options[0] = p1;
            options[1] = p2;
            options[2] = p3;
            options[3] = p4;
            List<Attrs> strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op1[0] = options[0].getName();
            op1[1] = options[1].getName();
            op1[2] = options[2].getName();
            op1[3] = options[3].getName();

            // --------------- second question => different people ------------------

            options[0] = p5;
            options[1] = p1;
            options[2] = p2;
            options[3] = p3;
            strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op2[0] = options[0].getName();
            op2[1] = options[1].getName();
            op2[2] = options[2].getName();
            op2[3] = options[3].getName();

            // --------------- third question => different people ------------------

            options[0] = p4;
            options[1] = p5;
            options[2] = p1;
            options[3] = p2;
            strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op3[0] = options[0].getAttr1();
            op3[1] = options[1].getAttr1();
            op3[2] = options[2].getAttr1();
            op3[3] = options[3].getAttr1();

            // --------------- second question => different people ------------------

            options[0] = p3;
            options[1] = p4;
            options[2] = p5;
            options[3] = p1;
            strList = Arrays.asList(options);
            Collections.shuffle(strList);
            strList.toArray(options);

            op4[0] = options[0].getAttr2();
            op4[1] = options[1].getAttr2();
            op4[2] = options[2].getAttr2();
            op4[3] = options[3].getAttr2();
        }
        else {
            op1[0] = options[0].getName();
            op1[1] = options[1].getName();
            op1[2] = options[2].getName();
            op1[3] = options[3].getName();

            op2[0] = options[0].getAttr1();
            op2[1] = options[1].getAttr1();
            op2[2] = options[2].getAttr1();
            op2[3] = options[3].getAttr1();

            op3[0] = options[0].getAttr2();
            op3[1] = options[1].getAttr2();
            op3[2] = options[2].getAttr2();
            op3[3] = options[3].getAttr2();
        }

        GlyphLayout layout = new GlyphLayout();

        layout.setText(font, op1[0]);
        op11Width = layout.width;
        layout.setText(font, op1[1]);
        op12Width = layout.width;
        layout.setText(font, op1[2]);
        op13Width = layout.width;
        layout.setText(font, op1[3]);
        op14Width = layout.width;

        layout.setText(font, op2[0]);
        op21Width = layout.width;
        layout.setText(font, op2[1]);
        op22Width = layout.width;
        layout.setText(font, op2[2]);
        op23Width = layout.width;
        layout.setText(font, op2[3]);
        op24Width = layout.width;

        layout.setText(font, op3[0]);
        op31Width = layout.width;
        layout.setText(font, op3[1]);
        op32Width = layout.width;
        layout.setText(font, op3[2]);
        op33Width = layout.width;
        layout.setText(font, op3[3]);
        op34Width = layout.width;

        layout.setText(font, op4[0]);
        op41Width = layout.width;
        layout.setText(font, op4[1]);
        op42Width = layout.width;
        layout.setText(font, op4[2]);
        op43Width = layout.width;
        layout.setText(font, op4[3]);
        op44Width = layout.width;

        defineOptionImages();
    }

    private void display() {
        if (index < chosenPeople.size()) {
            if (System.currentTimeMillis() - timestamp > personTime) {
                index++;

                if (index > 0) {
                    assetsLoader.sounds.get(chosenPeople.get(index - 1)).stop();
                }

                if (index < chosenPeople.size()) {
                    personTime = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(index)).getAudioTime();

                    assetsLoader.sounds.get(chosenPeople.get(index)).play();
                    timestamp = System.currentTimeMillis();
                }
            }
            if (index < chosenPeople.size()) {
                batch.begin();
                batch.draw(assetsLoader.pictures.get(chosenPeople.get(index)),
                        GameConstants.centerX - 200, GameConstants.centerY - 200, 400, 400);
                batch.end();
            }
        }
        else {
            startTime();
            displayOptions();
        }
    }

    private void defineOptionImages() {
        indexesDisplayPictures = new ArrayList<>();
        for (int i = 0; i < numPictures; i++) {
            indexesDisplayPictures.add(indexDisplayPicture);
            indexDisplayPicture++;
            indexDisplayPicture = indexDisplayPicture % numPeople;
        }
    }

    private void displayOptionImages(int display) {
        batch.begin();

        batch.draw(assetsLoader.pictures.get(chosenPeople.get(indexesDisplayPictures.get(0))),
                GameConstants.screenWidth * 1 / 4f - size / 2f, GameConstants.screenHeight - height - size / 2f,
                size, size);
        batch.draw(assetsLoader.pictures.get(chosenPeople.get(indexesDisplayPictures.get(1))),
                GameConstants.screenWidth * 3 / 4f - size / 2f, GameConstants.screenHeight - height - size / 2f,
                size, size);

        if (display == 3) {
            batch.draw(assetsLoader.pictures.get(chosenPeople.get(indexesDisplayPictures.get(2))),
                    GameConstants.centerX - size / 2f, GameConstants.screenHeight - height - size - 150 - 20, size, size);
        }
        else if (display == 4) {
            batch.draw(assetsLoader.pictures.get(chosenPeople.get(indexesDisplayPictures.get(2))),
                    GameConstants.screenWidth * 1 / 4f - size / 2f, GameConstants.screenHeight - height - size - 150 - 20,
                    size, size);
            batch.draw(assetsLoader.pictures.get(chosenPeople.get(indexesDisplayPictures.get(3))),
                    GameConstants.screenWidth * 3 / 4f - size / 2f, GameConstants.screenHeight - height - size - 150 - 20,
                    size, size);
        }
        batch.end();
    }

    private void displayOptions() {

        displayOptionImages(numPictures);

        Color color = GameConstants.Colors.FADED_WHITE.getColor();
        if (opPicked == 1) {
            x1 = Gdx.input.getX();
            y1 = (int) (GameConstants.screenHeight - Gdx.input.getY());
            x2 = (int) GameConstants.centerX + 15;
            y2 = 30;
            x3 = 30;
            y3 = 30 + h + 30;
            x4 = (int) GameConstants.centerX + 15;
            y4 = 30 + h + 30;
        }
        else if (opPicked == 2) {
            x1 = 30;
            y1 = 30;
            x2 =  Gdx.input.getX();
            y2 = (int) (GameConstants.screenHeight - Gdx.input.getY());;
            x3 = 30;
            y3 = 30 + h + 30;
            x4 = (int) GameConstants.centerX + 15;
            y4 = 30 + h + 30;
        }
        else if (opPicked == 3) {
            x1 = 30;
            y1 = 30;
            x2 = (int) GameConstants.centerX + 15;
            y2 = 30;
            x3 = Gdx.input.getX();
            y3 = (int) (GameConstants.screenHeight - Gdx.input.getY());
            x4 = (int) GameConstants.centerX + 15;
            y4 = 30 + h + 30;
        }
        else if (opPicked == 4) {
            x1 = 30;
            y1 = 30;
            x2 = (int) GameConstants.centerX + 15;
            y2 = 30;
            x3 = 30;
            y3 = 30 + h + 30;
            x4 = Gdx.input.getX();
            y4 = (int) (GameConstants.screenHeight - Gdx.input.getY());
        }
        else {
            x1 = 30;
            y1 = 30;
            x2 = (int) GameConstants.centerX + 15;
            y2 = 30;
            x3 = 30;
            y3 = 30 + h + 30;
            x4 = (int) GameConstants.centerX + 15;
            y4 = 30 + h + 30;
        }

        boolean write1 = true, write2 = true, write3 = true, write4 = true;
        for (int n = 0; n < eliminateOption.size(); n++) {
            if (eliminateOption.get(n) == 1) {
                write1 = false;
            } else if (eliminateOption.get(n) == 2) {
                write2 = false;
            } else if (eliminateOption.get(n) == 3) {
                write3 = false;
            } else if (eliminateOption.get(n) == 4) {
                write4 = false;
            }
        }
        if (write1) { randomGames.roundedRectangle(x1, y1, 25, w, h, color); }
        if (write2) { randomGames.roundedRectangle(x2, y2, 25, w, h, color); }
        if (write3) {  randomGames.roundedRectangle(x3, y3, 25, w, h, color); }
        if (write4) { randomGames.roundedRectangle(x4, y4, 25, w, h, color); }

        batch.begin();

        if (question == 1) {
            if (write1) { font.draw(batch, op1[0], x1 + w / 2f - op11Width / 2f, y1 + h / 2f + 20); }
            if (write2) { font.draw(batch, op1[1], x2 + w / 2f - op12Width / 2f, y2 + h / 2f + 20); }
            if (write3) { font.draw(batch, op1[2], x3 + w / 2f - op13Width / 2f, y3 + h / 2f + 20); }
            if (write4) { font.draw(batch, op1[3], x4 + w / 2f - op14Width / 2f, y4 + h / 2f + 20); }
        } else if (question == 2) {
            if (write1) { font.draw(batch, op2[0], x1 + w / 2f - op21Width / 2f, y1 + h / 2f + 20); }
            if (write2) { font.draw(batch, op2[1], x2 + w / 2f - op22Width / 2f, y2 + h / 2f + 20); }
            if (write3) { font.draw(batch, op2[2], x3 + w / 2f - op23Width / 2f, y3 + h / 2f + 20); }
            if (write4) { font.draw(batch, op2[3], x4 + w / 2f - op24Width / 2f, y4 + h / 2f + 20); }
        } else if (question == 3) {
            if (write1) { font.draw(batch, op3[0], x1 + w / 2f - op31Width / 2f, y1 + h / 2f + 20); }
            if (write2) { font.draw(batch, op3[1], x2 + w / 2f - op32Width / 2f, y2 + h / 2f + 20); }
            if (write3) { font.draw(batch, op3[2], x3 + w / 2f - op33Width / 2f, y3 + h / 2f + 20); }
            if (write4) { font.draw(batch, op3[3], x4 + w / 2f - op34Width / 2f, y4 + h / 2f + 20); }
        } else if (question == 4) {
            if (write1) { font.draw(batch, op4[0], x1 + w / 2f - op41Width / 2f, y1 + h / 2f + 20); }
            if (write2) { font.draw(batch, op4[1], x2 + w / 2f - op42Width / 2f, y2 + h / 2f + 20); }
            if (write3) { font.draw(batch, op4[2], x3 + w / 2f - op43Width / 2f, y3 + h / 2f + 20); }
            if (write4) { font.draw(batch, op4[3], x4 + w / 2f - op44Width / 2f, y4 + h / 2f + 20); }
        }

        batch.end();
    }

    private void eliminateOption(int remove) {
        eliminateOption.add(remove);
    }

    private boolean nextQuestion() {
        if (level == 1 || level == 2) {
            if ((numAnswers == 4 && question == 4) || (numAnswers == numPeople && question != 4)) {
                return true;
            }
        } else {
            if (numAnswers == 4) {
                return true;
            }
        }
        return false;
    }

    private void restartVariables() {
        numAnswers = 0;
        eliminateOption = new ArrayList<>();
        defineOptionImages();
        question++;

        completedTask();
    }

    private void optionsTouchDown(int screenX, int screenY) {
        Rectangle bounds = new Rectangle(x1, y1, w, h);
        if (bounds.contains(screenX, screenY)) {
            if (eliminateOption.size() == 0) { opPicked = 1; }
            for (int n = 0; n < eliminateOption.size(); n++) {
                if (eliminateOption.get(n) == 1) { break; }
                if (n == eliminateOption.size() - 1) { opPicked = 1; }
            }
        } else {
            bounds = new Rectangle(x2, y2, w, h);
            if (bounds.contains(screenX, screenY)) {
                if (eliminateOption.size() == 0) { opPicked = 2; }
                for (int n = 0; n < eliminateOption.size(); n++) {
                    if (eliminateOption.get(n) == 2) { break; }
                    if (n == eliminateOption.size() - 1) { opPicked = 2; }
                }
            } else {
                bounds = new Rectangle(x3, y3, w, h);
                if (bounds.contains(screenX, screenY)) {
                    if (eliminateOption.size() == 0) { opPicked = 3; }
                    for (int n = 0; n < eliminateOption.size(); n++) {
                        if (eliminateOption.get(n) == 3) { break; }
                        if (n == eliminateOption.size() - 1) { opPicked = 3; }
                    }
                } else {
                    bounds = new Rectangle(x4, y4, w, h);
                    if (bounds.contains(screenX, screenY)) {
                        if (eliminateOption.size() == 0) { opPicked = 4; }
                        for (int n = 0; n < eliminateOption.size(); n++) {
                            if (eliminateOption.get(n) == 4) { break; }
                            if (n == eliminateOption.size() - 1) { opPicked = 4; }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void evaluateAnswer() {
        Rectangle bounds;
        Attrs person = null;
        int x, y;
        if (opPicked == 1) {
            x = x1;
            y = y1;
        } else if (opPicked == 2) {
            x = x2;
            y = y2;
        } else if (opPicked == 3) {
            x = x3;
            y = y3;
        } else {
            x = x4;
            y = y4;
        }
        if (level == 1) {
            bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                    GameConstants.screenHeight - height - size / 2f, size, size);
            if (bounds.contains(x, y)) {
                person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            }
            else {
                bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                        GameConstants.screenHeight - height - size / 2f, size, size);
                if (bounds.contains(x, y)) {
                    person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
                }
            }
        } else if (level == 2) {
            bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                    GameConstants.screenHeight - height - size / 2f, size, size);
            if (bounds.contains(x, y)) {
                person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            }
            else {
                bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                        GameConstants.screenHeight - height - size / 2f, size, size);
                if (bounds.contains(x, y)) {
                    person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
                }else {
                    bounds = new Rectangle(GameConstants.centerX - size / 2f,
                            GameConstants.screenHeight - height - size - 150 - 20, size, size);
                    if (bounds.contains(x, y)) {
                        person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(2));
                    }
                }
            }
        } else if (level == 3) {
            bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                    GameConstants.screenHeight - height - size / 2f, size, size);
            if (bounds.contains(x, y)) {
                person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(0));
            }
            else {
                bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                        GameConstants.screenHeight - height - size / 2f, size, size);
                if (bounds.contains(x, y)) {
                    person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(1));
                }else {
                    bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                            GameConstants.screenHeight - height - size - 150 - 20, size, size);
                    if (bounds.contains(x, y)) {
                        person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(2));
                    }else {
                        bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                                GameConstants.screenHeight - height - size - 150 - 20, size, size);
                        if (bounds.contains(x, y)) {
                            person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(3));
                        }
                    }
                }
            }
        } else {
            bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                    GameConstants.screenHeight - height - size / 2f, size, size);
            if (bounds.contains(x, y)) {
                person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(indexesDisplayPictures.get(0)));
            }
            else {
                bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                        GameConstants.screenHeight - height - size / 2f, size, size);
                if (bounds.contains(x, y)) {
                    person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(indexesDisplayPictures.get(1)));
                }else {
                    bounds = new Rectangle(GameConstants.screenWidth * 1 / 4f - size / 2f,
                            GameConstants.screenHeight - height - size - 150 - 20, size, size);
                    if (bounds.contains(x, y)) {
                        person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(indexesDisplayPictures.get(2)));
                    }else {
                        bounds = new Rectangle(GameConstants.screenWidth * 3 / 4f - size / 2f,
                                GameConstants.screenHeight - height - size - 150 - 20, size, size);
                        if (bounds.contains(x, y)) {
                            person = Attrs.GINAORDER.getAttrsByFileName(chosenPeople.get(indexesDisplayPictures.get(3)));
                        }
                    }
                }
            }
        }
        if (person != null && ((question == 1 &&
                (person.getName().equals(op1[opPicked - 1]) ||
                        person.getAttr1().equals(op1[opPicked - 1]) ||
                        person.getAttr2().equals(op1[opPicked - 1]))) ||
                (question == 2 &&
                        (person.getName().equals(op2[opPicked - 1]) ||
                                person.getAttr1().equals(op2[opPicked - 1]) ||
                                person.getAttr2().equals(op2[opPicked - 1]))) ||
                (question == 3 &&
                        (person.getName().equals(op3[opPicked - 1]) ||
                                person.getAttr1().equals(op3[opPicked - 1]) ||
                                person.getAttr2().equals(op3[opPicked - 1]))) ||
                (question == 4 &&
                        (person.getName().equals(op4[opPicked - 1]) ||
                                person.getAttr1().equals(op4[opPicked - 1]) ||
                                person.getAttr2().equals(op4[opPicked - 1]))))) {
            increasePoints(235);
            eliminateOption(opPicked);
            numAnswers++;
        }
        else if (person != null) {
            decreaseLife(true);
            decreasePoints(getLOSE_WRONG_POINTS() + 150);
        }

        if (nextQuestion()) {
            restartVariables();
        }
    }
    @Override
    void endGame() {
        if (isEndTasks()) {
            terminateGame(false);
        }
        if (isEndLives() || timesUp()) {
            terminateGame(true);
        }
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameHasStarted()) {
            display();
        }

        startGame(batch);
        displayStats(batch);
        endGame();
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
        texture.dispose();
        font.dispose();
        gameDispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(!rulesKeyDown(keycode) && keycode == Input.Keys.BACK && !isPaused()){
            pauseGame(true);
            return true;
        }
        return false;
    }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = (int) (GameConstants.screenHeight - screenY);

        if (!menuTouchDown(screenX, screenY)) {
            // menu
            Rectangle bounds = new Rectangle(10, GameConstants.screenHeight - 10 - 75, 75, 75);
            if (bounds.contains(screenX, screenY) && gameHasStarted()) {
                pauseGame(true);
            } else {
                // hint
                bounds = new Rectangle(10 + 75 + 10, GameConstants.screenHeight - 10 - 100, 130, 100);
                if (bounds.contains(screenX, screenY) && !hintTaken() && gameHasStarted() && timeHasStarted() && MyPreference.isPremium()) {
                    takeHint();
                    increasePoints(260);
                    restartVariables();
                }
                else {
                    optionsTouchDown(screenX, screenY);
                }
            }
        }
        return false;
    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //Gdx.app.log("------------- touch Up ----------", "entrou");
        if (gameHasStarted()) {
            evaluateAnswer();
            opPicked = 0;
        }
        return false;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override
    public boolean scrolled(int amount) { return false; }
}
