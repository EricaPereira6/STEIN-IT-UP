package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

class RandomGames {

    private SpriteBatch batch;

    private Calendar calendar;

    private Random random;
    private List<GameConstants.Games> values;
    private int numGames;

    private ShapeRenderer shapeRenderer;
    private PolygonSprite[] game1, game2, game3;
    private PolygonSpriteBatch polyBatch;
    private Texture textureSolid;

    private Texture texture;
    private BitmapFont font;
    private ArrayList<Float> titlePositions;

    RandomGames () {

        batch = new SpriteBatch();

        calendar = Calendar.getInstance();

        random = new Random();
        values = Arrays.asList(GameConstants.Games.values());
        numGames = values.size();

        game1 = new PolygonSprite[3];
        game2 = new PolygonSprite[3];
        game3 = new PolygonSprite[3];

        shapeRenderer = new ShapeRenderer();
        polyBatch = new PolygonSpriteBatch();

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (1.5f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        titlePositions = new ArrayList<>();
    }

    boolean changeOfDay() {

        // 1 + month because it's starts in the 0 month
        String currentDate = calendar.get(Calendar.DATE) + "-" +
                (1 + calendar.get(Calendar.MONTH)) + "-" +
                calendar.get(Calendar.YEAR);

        if (MyPreference.isDateEmpty()) {

            Gdx.app.log("changeOfDay -> empty day", "   -> getting 3 random Games");
            MyPreference.setDate(currentDate);
            MyPreference.setGames(getThreeRandomGames());

            return true;
        }

        String databaseDate = MyPreference.getDate();

        if (!databaseDate.equals(currentDate)) {
            Gdx.app.log("changeOfDay  -> different day", "   -> getting 3 random Games");
            MyPreference.setDate(currentDate);
            MyPreference.setGames(getThreeRandomGames());

            return true;
        }
        return false;
    }

    private ArrayList<GameConstants.Games> getThreeRandomGames() {

        ArrayList<GameConstants.Games> games = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            GameConstants.Games game = values.get(random.nextInt(numGames));

            if (!game.getComingSoon()) {
                if (i == 0) {
                    games.add(game);
                } else {
                    for (int g = 0; g < games.size(); g++) {
                        if (game == games.get(g)) {
                            i--;
                            break;
                        }
                        if (g == games.size() - 1) {
                            games.add(game);
                            break;
                        }
                    }
                }
            } else {
                i--;
            }
        }
        return games;
    }

    PolygonSprite defineTriangle(int x, int y, int size, Color color) {
        // Creating the color filling (but textures would work the same way)
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color); // green
        pix.fill();

        textureSolid = new Texture(pix);
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
                new float[] {                                // six vertices            2
                        x,              y,                   // vertex 0              /   \
                        x + size,       y,                   // vertex 1             /     \
                        x + size / 2f,  y + size,            // vertex 2           0 ------- 1

                }, new short[] {
                0, 1, 2          // Two triangles using vertex indices.
        });

        return new PolygonSprite(polyReg);
    }

    PolygonSprite defineHexagon(int x, int y, int size, Color color) {

        // Creating the color filling (but textures would work the same way)
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color); // blue
        pix.fill();

        int height = size - 10;

        textureSolid = new Texture(pix);
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
                new float[] {                                  // six vertices            3
                        x + height / 2f,  y,                   // vertex 0             /     \
                        x + height,       y + size / 4f,       // vertex 1           4         2
                        x + height,       y + size * 3 / 4f,   // vertex 2           |         |
                        x + height / 2f,  y + size,            // vertex 3           5         1
                        x,                y + size * 3 / 4f,   // vertex 4             \     /
                        x,                y + size / 4f        // vertex 5                0
                }, new short[] {
                0, 1, 2,         // Two triangles using vertex indices.
                0, 2, 3,         // Take care of the counter-clockwise direction.
                0, 3, 4,
                0, 4, 5
        });

        return new PolygonSprite(polyReg);
    }

    PolygonSprite defineLozenge(int x, int y, int size, Color color) {

        // Creating the color filling (but textures would work the same way)
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(color); //  purple
        pix.fill();

        textureSolid = new Texture(pix);
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(textureSolid),
                new float[] {                            // Four vertices     2
                        x + size / 2f,  y,               // Vertex 0       /  |  \
                        x + size,       y + size / 2f,   // Vertex 1     3    |    1
                        x + size / 2f,  y + size,        // Vertex 2       \  |  /
                        x,              y + size / 2f    // Vertex 3          0
                }, new short[] {
                0, 1, 2,         // Two triangles using vertex indices.
                0, 2, 3          // Take care of the counter-clockwise direction.
        });

        return new PolygonSprite(polyReg);

    }

    void defineShapes(ArrayList<Integer> positions, int width, int height){

        titlePositions = new ArrayList<>();
        game1 = new PolygonSprite[3];
        game2 = new PolygonSprite[3];
        game3 = new PolygonSprite[3];

        ArrayList<GameConstants.Games> games = MyPreference.getGames();

        Color color;
        Color color2;

        for (int i = 0; i < games.size(); i++) {

            int x = (int) (positions.get(i * 2) - (height / 2f));
            int y = positions.get((i * 2) + 1);

            int centerSize = (int) (height * 3 / 2f);
            float centerCompensation = (centerSize - height) / 2f;

            switch (games.get(i).getGameTheme()) {
                case MATH:
                    color = GameConstants.Colors.MATH_BG_COLOR.getColor();
                    color2 = GameConstants.Colors.MATH_BG_DARKER.getColor();

                    if (i == 0) {
                        game1[0] = defineHexagon(x, y, height, color);
                        game1[1] = defineHexagon(x + width, y, height, color);
                        game1[2] = defineHexagon((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    }
                    if (i == 1) {
                        game2[0] = defineHexagon(x, y, height, color);
                        game2[1] = defineHexagon(x + width, y, height, color);
                        game2[2] = defineHexagon((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    }
                    game3[0] = defineHexagon(x, y, height, color);
                    game3[1] = defineHexagon(x + width, y, height, color);
                    game3[2] = defineHexagon((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                            centerSize, color2);
                    break;
                case LOGIC:
                    color = GameConstants.Colors.LOGIC_BG_COLOR.getColor();
                    color2 = GameConstants.Colors.LOGIC_BG_DARKER.getColor();

                    if (i == 0) {
                        game1[0] = defineTriangle(x, y, height, color);
                        game1[1] = defineTriangle(x + width, y, height, color);
                        game1[2] = defineTriangle((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    } else if (i == 1) {
                        game2[0] = defineTriangle(x, y, height, color);
                        game2[1] = defineTriangle(x + width, y, height, color);
                        game2[2] = defineTriangle((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    }
                    game3[0] = defineTriangle(x, y, height, color);
                    game3[1] = defineTriangle(x + width, y, height, color);
                    game3[2] = defineTriangle((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                            centerSize, color2);
                    break;
                case FOCUS:
                    color = GameConstants.Colors.FOCUS_BG_COLOR.getColor();
                    color2 = GameConstants.Colors.FOCUS_BG_DARKER.getColor();
                    if (i == 0) {
                        game1[0] = defineLozenge(x, y, height, color);
                        game1[1] = defineLozenge(x + width, y, height, color);
                        game1[2] = defineLozenge((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    } else if (i == 1) {
                        game2[0] = defineLozenge(x, y, height, color);
                        game2[1] = defineLozenge(x + width, y, height, color);
                        game2[2] = defineLozenge((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                                centerSize, color2);
                        break;
                    }
                    game3[0] = defineLozenge(x, y, height, color);
                    game3[1] = defineLozenge(x + width, y, height, color);
                    game3[2] = defineLozenge((int) (x + (width / 2f) - centerCompensation), (int) (y - centerCompensation),
                            centerSize, color2);

                    break;
                default:
                    if (i == 0) {
                        game1 = null;
                        break;
                    }
                    if (i == 1) {
                        game2 = null;
                        break;
                    }
                    game3 = null;
                    break;
            }

            GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
            layout.setText(font, games.get(i).getGameName());
            titlePositions.add(layout.width);
            titlePositions.add(layout.height);

        }
    }

    void drawCircle(int x, int y, int size , Color color) {

        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x, y, size / 2f);
        shapeRenderer.end();
    }

    void drawPoly(PolygonSprite poly) {

        polyBatch.begin();
        poly.draw(polyBatch);
        polyBatch.end();
    }

    void displayThreeGames(ArrayList<Integer> positions, int between, int size) {

        Color color;

        ArrayList<GameConstants.Games> games = MyPreference.getGames();

        for (int i = 0; i < games.size(); i++) {

            int x = (int) (positions.get(i * 2) - (size / 2f));
            int y = positions.get((i * 2) + 1);

            if (i == 0 && game1 == null || i == 1 && game2 == null || i == 2 && game3 == null) {
                int centerSize = (int) (size * 3 / 2f);

                color = GameConstants.Colors.MEMORY_BG_DARKER.getColor();
                drawCircle((int) (x + (between / 2f) + (size / 2f)), (int) (y + (size / 2f)),
                        centerSize, color);

                color = GameConstants.Colors.MEMORY_BG_COLOR.getColor();
                drawCircle((int) (x + (size / 2f)), (int) (y + (size / 2f)), size, color);
                drawCircle((int) (x + between + (size / 2f)), (int) (y + (size / 2f)), size, color);
            } else if (i == 0) {
                drawPoly(game1[0]);
                drawPoly(game1[1]);
                drawPoly(game1[2]);
            } else if (i == 1) {
                drawPoly(game2[0]);
                drawPoly(game2[1]);
                drawPoly(game2[2]);
            } else if (i == 2) {
                drawPoly(game3[0]);
                drawPoly(game3[1]);
                drawPoly(game3[2]);
            }

            switch (games.get(i).getGameTheme()) {
                case MATH:
                    color = GameConstants.Colors.MATH_BG_COLOR.getColor();
                    break;
                case MEMORY:
                    color = GameConstants.Colors.MEMORY_BG_COLOR.getColor();
                    break;
                case LOGIC:
                    color = GameConstants.Colors.LOGIC_BG_COLOR.getColor();
                    break;
                case FOCUS:
                    color = GameConstants.Colors.FOCUS_BG_COLOR.getColor();
                    break;
                default:
                    color = Color.WHITE;
                    break;
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(color);
            shapeRenderer.rect(positions.get(i * 2), positions.get((i * 2) + 1), between, size);

            shapeRenderer.end();

            batch.begin();

            font.draw(batch,
                    games.get(i).getGameName(),
                    GameConstants.centerX - titlePositions.get(i * 2) / 2f,
                    y + size / 2f + titlePositions.get((i * 2) + 1) - 15);

            batch.end();
        }
    }

    void defineShape(int posX, int posY, int size, GameConstants.Games game){

        int x = (int) (posX - (size / 2f));
        int y = (int) ((GameConstants.screenHeight - posY) - (size / 2f));

        game1 = new PolygonSprite[1];

        switch (game.getGameTheme()) {
            case MATH:
                game1[0] = defineHexagon(x, y, size, GameConstants.Colors.MATH_BG_COLOR.getColor());
                break;
            case LOGIC:
                game1[0] = defineTriangle(x, y, size, GameConstants.Colors.LOGIC_BG_COLOR.getColor());
                break;
            case FOCUS:
                game1[0] = defineLozenge(x, y, size, GameConstants.Colors.FOCUS_BG_COLOR.getColor());
                break;
            default:
                break;
        }

        titlePositions = new ArrayList<>();

        String[] title = game.getGameName().split(" ");
        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member

        for(String word : title) {
            layout.setText(font, word);
            titlePositions.add(layout.width);
            titlePositions.add(layout.height);
        }

    }

    void displayGame(int posX, int posY, int size, GameConstants.Games game) {

        switch (game.getGameTheme()) {
            case FOCUS:
            case MATH:
                drawPoly(game1[0]);
                break;
            case MEMORY:
                drawCircle(posX, posY, size, GameConstants.Colors.MEMORY_BG_COLOR.getColor());
                break;
            case LOGIC:
                drawPoly(game1[0]);
                posY = (int) (posY - size / 5f);
                break;
            default:
                drawCircle(posX, posY, size, Color.WHITE);
                break;
        }

        String[] title = game.getGameName().split(" ");

        batch.begin();

        for(int i = 0; i < title.length; i++) {

            int ty = (int) (posY + titlePositions.get((i * 2) + 1) / 2f);
            int pos = ty;

            if (title.length == 2) {
                pos = ty - (40 * ((i * 2) - 1));
            }
            else if (title.length == 3) {
                pos = ty - (70 * (i - 1));
            }
            font.draw(batch, title[i], GameConstants.centerX - titlePositions.get(i * 2) / 2f, pos);
        }

        batch.end();

    }

    void roundedRectangle(int x, int y, int radius, int width, int height, Color color) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);

        // shapes background
        shapeRenderer.rect(x + radius, y + radius, width - 2*radius, height - 2*radius);

        // Four side rectangles, in clockwise order
        shapeRenderer.rect(x + radius, y, width - 2*radius, radius);
        shapeRenderer.rect(x + width - radius, y + radius, radius, height - 2*radius);
        shapeRenderer.rect(x + radius, y + height - radius, width - 2*radius, radius);
        shapeRenderer.rect(x, y + radius, radius, height - 2*radius);

        // Four arches, clockwise too
        shapeRenderer.arc(x + radius, y + radius, radius, 180f, 90f);
        shapeRenderer.arc(x + width - radius, y + radius, radius, 270f, 90f);
        shapeRenderer.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        shapeRenderer.arc(x + radius, y + height - radius, radius, 90f, 90f);

        shapeRenderer.end();
    }

    void dispose() {
        shapeRenderer.dispose();
        polyBatch.dispose();
        textureSolid.dispose();
        batch.dispose();
        font.dispose();
        texture.dispose();
    }
}
