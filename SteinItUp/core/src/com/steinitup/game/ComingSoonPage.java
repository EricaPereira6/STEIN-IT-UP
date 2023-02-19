package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ComingSoonPage  implements Screen {

    private final SteinItUpGame steinItUpGame;
    private RandomGames randomGames;
    private GameConstants.Games game;

    private final Color background = GameConstants.Colors.COMING_SOON_DARKER.getColor();

    private SpriteBatch batch;
    private PolygonSprite poly;
    private int size;

    private Texture texture;
    private BitmapFont font;
    private float titlePosition;

    ComingSoonPage(final SteinItUpGame steinItUpGame, final GameConstants.Games game) {
        this.steinItUpGame = steinItUpGame;
        this.randomGames = steinItUpGame.randomGames;
        this.game = game;

        batch = new SpriteBatch();

        size = (int) GameConstants.screenWidth - 100;

        switch (game.getGameTheme()) {
            case MATH:
                poly = randomGames.defineHexagon((int) GameConstants.centerX - size / 2,
                        (int) GameConstants.centerY - size / 2,
                        size, GameConstants.Colors.COMING_SOON_COLOR.getColor());
                break;
            case FOCUS:
                poly = randomGames.defineLozenge((int) GameConstants.centerX - size / 2,
                        (int) GameConstants.centerY - size / 2,
                        size, GameConstants.Colors.COMING_SOON_COLOR.getColor());
                break;
            case LOGIC:
                poly = randomGames.defineTriangle((int) GameConstants.centerX - size / 2,
                        (int) GameConstants.centerY - size / 2,
                        size, GameConstants.Colors.COMING_SOON_COLOR.getColor());
                break;
        }

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, "Coming Soon");
        titlePosition = layout.width;
    }

    @Override
    public void show() { }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.getGameTheme().equals(GameConstants.Themes.MEMORY)) {
            randomGames.drawCircle((int) GameConstants.centerX, (int) GameConstants.centerY,
                    size, GameConstants.Colors.COMING_SOON_COLOR.getColor());
        }
        else {
            randomGames.drawPoly(poly);
        }

        batch.begin();

        int posY = (int) GameConstants.centerY;
        if (game.getGameTheme().equals(GameConstants.Themes.LOGIC)) {
            posY = (int) (posY - size / 5f);
        }
        font.draw(batch, "Coming Soon", GameConstants.centerX - titlePosition / 2f, posY + 20);


        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            steinItUpGame.setHomePage();
        }
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
    }
}
