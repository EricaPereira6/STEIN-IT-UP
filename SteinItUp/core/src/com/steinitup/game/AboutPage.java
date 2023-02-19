package com.steinitup.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class AboutPage implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;

    private SpriteBatch batch;
    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, titleFont;
    private final String titleStr = "About";
    private float titleWidth, titleHeight;
    private Sprite sprite, bgSprite;

    private RandomGames randomGames;
    private PolygonSprite poly;

    AboutPage(SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.randomGames = steinItUpGame.randomGames;

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(this);

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        titleFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        titleFont.getData().setScale(scale);
        titleFont.setColor(Color.WHITE);

        scale = (1.1f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        // ----------- title -----------
        GlyphLayout layout = new GlyphLayout();
        layout.setText(titleFont, titleStr);
        titleWidth = layout.width;// contains the width of the current set text
        titleHeight = layout.height; // contains the height of the current set text

        // ----------- background title -----------
        int border = (int) ((60 * GameConstants.screenWidth) / 720);
        Pixmap pixmap = new Pixmap((int) GameConstants.screenWidth, (int) titleHeight + border, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.TITLE_LOGIN_BG.getColor());
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));

        // ----------- background -----------
        pixmap = new Pixmap((int) GameConstants.screenWidth - 70, (int) GameConstants.screenHeight - 70, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        bgSprite = new Sprite(new Texture(pixmap));

        // ----------- profile picture -----------
        poly = randomGames.defineHexagon((int) (GameConstants.centerX - 225),
                (int) (GameConstants.screenHeight - sprite.getHeight()) - 80 - 400 - 55,
                460, GameConstants.Colors.PROFILE_BACKGROUND.getColor());
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // background
        bgSprite.setPosition(35, 35);
        bgSprite.draw(batch);

        // draw title
        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f, GameConstants.screenHeight - 50 - sprite.getHeight());
        sprite.draw(batch);

        titleFont.draw(batch,
                titleStr,
                GameConstants.centerX - titleWidth / 2f,
                GameConstants.screenHeight - 30 - sprite.getHeight() / 2f);

        batch.end();

        randomGames.drawPoly(poly);

        batch.begin();

        int y = (int) (GameConstants.screenHeight - sprite.getHeight()) - 400;

        // profile photo
        batch.draw(assetsLoader.makerTexture,
                GameConstants.centerX - 150, y - 80, 300, 350);

        font.draw(batch, "Game Author:   Érica Pereira"              , 75, y - 80 * 2);
        font.draw(batch, "Student Number:   42356"                   , 75, y - 80 * 3);
        font.draw(batch, "Institution:   Instituto Superior de"      , 75, y - 80 * 4);
        font.draw(batch, "            Engenharia de Lisboa"          , 75, y - 80 * 5);
        font.draw(batch, "Graduation:  Licenciatura em Engenharia"   , 75, y - 80 * 6);
        font.draw(batch, "            Informática e Multimédia"      , 75, y - 80 * 7);

        batch.end();
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
        titleFont.dispose();
        font.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            steinItUpGame.setHomePage();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(int amount) { return false; }
}
