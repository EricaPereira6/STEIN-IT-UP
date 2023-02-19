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
import com.badlogic.gdx.math.Rectangle;

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.functional.Consumer;

public class ProfilePage implements Screen, InputProcessor {

    private SteinItUpGame steinItUpGame;
    private AssetsLoader assetsLoader;

    private SpriteBatch batch;
    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, titleFont, settingsFont, redFont;
    private final String titleStr = "Profile";
    private final String soundStr = "Sounds: Off";
    private final String musicStr = "Music: Off";
    private final String logoutStr = "Log Out";
    private final String premiumStr = "Give Up the Premium";
    private String sound, music;
    private float titleWidth, soundWidth, soundHeight, musicWidth, musicHeight,
            logoutWidth, logoutHeight, premiumWidth, premiumHeight;
    private Sprite sprite, bgSprite;

    private String email, photo;
    private final int size = (int) (400 * GameConstants.screenWidth) / 720;
    private final int offset = (int) (80 * GameConstants.screenWidth) / 720;

    private  RandomGames randomGames;
    private PolygonSprite poly;

    private GlyphLayout layout;

    ProfilePage(SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        this.assetsLoader = steinItUpGame.assetsLoader;
        this.randomGames = steinItUpGame.randomGames;

        email = steinItUpGame.databaseAccess.getEmail();
        photo = steinItUpGame.databaseAccess.getPhoto();

        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(this);

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        titleFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        titleFont.getData().setScale(scale);
        titleFont.setColor(Color.WHITE);

        scale = (0.9f * GameConstants.screenWidth) / 720;
        settingsFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        settingsFont.getData().setScale(scale);
        settingsFont.setColor(Color.GRAY);

        scale = (1.1f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.BLACK);

        redFont = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        redFont.getData().setScale(scale);
        redFont.setColor(Color.RED);

        // ----------- red font size -----------
        layout = new GlyphLayout();
        layout.setText(font, soundStr);
        soundWidth = layout.width;
        soundHeight = layout.height;

        layout.setText(font, musicStr);
        musicWidth = layout.width;
        musicHeight = layout.height;

        layout.setText(redFont, logoutStr);
        logoutWidth = layout.width;
        logoutHeight = layout.height;

        layout.setText(redFont, premiumStr);
        premiumWidth = layout.width;
        premiumHeight = layout.height;

        // ----------- title -----------
        layout.setText(titleFont, titleStr);
        titleWidth = layout.width;// contains the width of the current set text
        float titleHeight = layout.height; // contains the height of the current set text

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
        poly = randomGames.defineHexagon((int) (GameConstants.centerX - size / 2f - 10),
                (int) (GameConstants.screenHeight - sprite.getHeight()) - 80 - size - 10,
                size + 20, GameConstants.Colors.PROFILE_BACKGROUND.getColor());

        // ----------- define sounds and music -----------
        if (MyPreference.isSoundOn()) {
            sound = "On";
        }
        else {
            sound = "Off";
        }
        if (MyPreference.isMusicOn()) {
            music = "On";
        }
        else {
            music = "Off";
        }
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // white background
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

        int y = (int) (GameConstants.screenHeight - sprite.getHeight() - size);

        // profile photo
        batch.draw(assetsLoader.photoTexture,
                GameConstants.centerX - size / 2f,
                y - 80,
                size, size);

        font.draw(batch, "username: " + MyPreference.getUsername(),   75, y - offset * 2);
        font.draw(batch, "email: " + email,                           75, y - offset * 3);
        settingsFont.draw(batch,
                " - - - - - - - - - - - - - - - - Settings - - - - - - - - - - - - - - - - ",
                                                                           90, y - 20 - offset * 4);
        font.draw(batch, "Language: English",                          75, y - 20 - offset * 5);
        font.draw(batch, "Sounds: " + sound,                               75, y - 20 - offset * 6);
        font.draw(batch, "Music: " + music,                GameConstants.centerX, y - 20 - offset * 6);
        redFont.draw(batch, premiumStr,                                    75, y - 20 - offset * 7 - 15);
        redFont.draw(batch, logoutStr,                                     75, y - 20 - offset * 8 - 15);

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
        font.dispose();
        titleFont.dispose();
        settingsFont.dispose();
        redFont.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK) {
            steinItUpGame.setHomePage();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = (int) (GameConstants.screenHeight - screenY);

        Rectangle bounds = new Rectangle(75,
                (int) (GameConstants.screenHeight - sprite.getHeight() - size) - 20 - offset * 7 - 15 - premiumHeight,
                premiumWidth, premiumHeight);
        if (bounds.contains(screenX, screenY)) {
            //Gdx.app.log("------------ give up premium -----------", "clicked");
            assetsLoader.playButtonSound();
            MyPreference.setPremium(false);
        }
        else {
            bounds = new Rectangle(75,
                    (int) (GameConstants.screenHeight - sprite.getHeight() - size) - 20 - offset * 8 - 15 - logoutHeight,
                    logoutWidth, logoutHeight);
            if (bounds.contains(screenX, screenY)) {
                //Gdx.app.log("------------ log out -----------", "clicked");
                assetsLoader.playButtonSound();
                GdxFIRAuth.inst().signOut()
                        .then(new Consumer<Void>() {
                            @Override
                            public void accept(Void o) {
                                MyPreference.setPrefsDefaultValues();
                                steinItUpGame.setScreenLogin();
                            }
                        });
            }
            else {
                bounds = new Rectangle(75,
                        (int) (GameConstants.screenHeight - sprite.getHeight() - size) - 20 - offset * 6 - soundHeight,
                        soundWidth, soundHeight);
                if (bounds.contains(screenX, screenY)) {
                    assetsLoader.playButtonSound();
                    //Gdx.app.log("------------ mute sound -----------", "clicked: " + sound);
                    MyPreference.setSound(!MyPreference.isSoundOn());
                    if (MyPreference.isSoundOn()) {
                        sound = "On";
                    }else {
                        sound = "Off";
                    }
                }
                else {
                    bounds = new Rectangle(GameConstants.centerX,
                            (int) (GameConstants.screenHeight - sprite.getHeight() - size) - 20 - offset * 6 - musicHeight,
                            musicWidth, musicHeight);
                    if (bounds.contains(screenX, screenY)) {
                        //Gdx.app.log("------------ mute music -----------", "clicked: " + music);
                        assetsLoader.playButtonSound();
                        MyPreference.setMusic(!MyPreference.isMusicOn());
                        if (MyPreference.isMusicOn()) {
                            music = "On";
                            assetsLoader.playMusic();
                        }
                        else {
                            music = "Off";
                            assetsLoader.stopMusic();
                        }
                    }
                }
            }

        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
