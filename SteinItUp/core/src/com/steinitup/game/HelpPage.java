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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HelpPage implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;

    private SpriteBatch batch;
    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, titleFont, responseFont;
    private final String titleStr = "Common Questions";
    private float titleWidth, titleHeight;
    private Sprite sprite, bgSprite;

    HelpPage(SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;

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

        scale = (0.8f * GameConstants.screenWidth) / 720;
        responseFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        responseFont.getData().setScale(scale);
        responseFont.setColor(Color.DARK_GRAY);

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

        font.draw(batch,         " >  How do I see my Stats?"                          , 60, GameConstants.screenHeight - 80 * 3);
        responseFont.draw(batch, "At home page click on the menu. You can see your"    , 90, GameConstants.screenHeight - 80 * 4);
        responseFont.draw(batch, "stats clicking on the option 'Stats'. Then, by "     , 90, GameConstants.screenHeight - 80 * 4 - 50);
        responseFont.draw(batch, "clicking on the categories' symbol, you can see each", 90, GameConstants.screenHeight - 80 * 4 - 100);
        responseFont.draw(batch, "game and then click on the games' symbol to see"     , 90, GameConstants.screenHeight - 80 * 4 - 150);
        responseFont.draw(batch, "more of your stats."                                      , 90, GameConstants.screenHeight - 80 * 4 - 200);
        responseFont.draw(batch, "To get access to more stats become Premium."         , 90, GameConstants.screenHeight - 80 * 4 - 250);

        font.draw(batch,         " >  How do I get access to helps in the"             , 60, GameConstants.screenHeight - 80 * 8 - 15);
        font.draw(batch,         "minigames?"                                          , 60, GameConstants.screenHeight - 80 * 9);
        responseFont.draw(batch, "To get access to 1 help per minigame, you need to"   , 90, GameConstants.screenHeight - 80 * 10);
        responseFont.draw(batch, "become Premium."                                     , 90, GameConstants.screenHeight - 80 * 10 - 50);

        font.draw(batch,         " >  How do I get to Master Level at a"               , 60, GameConstants.screenHeight - 80 * 12 + 15);
        font.draw(batch,         "category?"                                           , 60, GameConstants.screenHeight - 80 * 13 + 30);
        responseFont.draw(batch, "To get to Master level in a category, you need to"   , 90, GameConstants.screenHeight - 80 * 14 + 30);
        responseFont.draw(batch, "get to Master level in all mini games belonging to"  , 90, GameConstants.screenHeight - 80 * 14 - 20);
        responseFont.draw(batch, "that category."                                      , 90, GameConstants.screenHeight - 80 * 14 - 70);

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
        responseFont.dispose();
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