package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.regex.Pattern;

import pl.mk5.gdx.fireapp.GdxFIRApp;
import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;

public class Login implements Screen {
    private final SteinItUpGame steinItUpGame;
    private SpriteBatch batch;
    private AssetsLoader assetsLoader;
    private DatabaseAccess databaseAccess;

    private boolean authenticated;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, error;
    private float titleWidth, titleHeight, errorWidth, errorHeight;
    private Pixmap pixmap;
    private Sprite sprite;

    private String errorStr = "";
    private final String errorLogin = "E-mail or password are incorrect";
    private final String loginStr = "Login";
    private final String emailStr = "Insert e-mail here";
    private final String passwordStr = "Insert password here";

    private Stage stage;
    private Skin skin;
    private final int btnWidth = (int) (500 * GameConstants.screenWidth) / 720;
    private final int btnHeight = (int) (100 * GameConstants.screenHeight) / 1280;
    private boolean addListener;
    private Button btnLogin;
    private float bgWidth, bgHeight;
    private TextField txtEmail, txtPassword;

    Login (SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        batch = new SpriteBatch();

        this.assetsLoader = steinItUpGame.assetsLoader;
        this.databaseAccess = steinItUpGame.databaseAccess;

        authenticated = false;

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(2f);
        font.setColor(Color.WHITE);

        error = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        error.getData().setScale(0.85f);
        error.setColor(Color.RED);

        // ----------- title & error message size -----------
        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, loginStr);
        titleWidth = layout.width;// contains the width of the current set text
        titleHeight = layout.height; // contains the height of the current set text

        GlyphLayout errorLayout = new GlyphLayout();
        errorLayout.setText(error, errorLogin);
        errorWidth = errorLayout.width;
        errorHeight = errorLayout.height;

        // ----------- background title -----------
        pixmap = new Pixmap((int) GameConstants.screenWidth, (int) titleHeight + 60, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameConstants.Colors.TITLE_LOGIN_BG.getColor());
        pixmap.fill();

        sprite = new Sprite(new Texture(pixmap));

        // ----------- stage & skin -----------
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        skin = new Skin(Gdx.files.internal(GameConstants.skin));

        // ----------- TextFields -----------

        int offset = (int) (125 * GameConstants.screenWidth) / 720;
        txtEmail = new TextField("", skin);
        txtEmail.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight + offset);
        txtEmail.setSize(btnWidth, btnHeight);
        txtEmail.setColor(255f/255f, 255f/255f, 255f/255f, 180f/255f);
        txtEmail.setMessageText(emailStr);

        stage.addActor(txtEmail);

        txtPassword = new TextField("", skin);
        txtPassword.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight);
        txtPassword.setSize(btnWidth, btnHeight);
        txtPassword.setColor(255f/255f, 255f/255f, 255f/255f, 180f/255f);
        txtPassword.setMessageText(passwordStr);
        txtPassword.setPasswordMode(true);
        txtPassword.setPasswordCharacter('*');

        stage.addActor(txtPassword);

        // ----------- button -----------

        bgWidth = 100 + ((GameConstants.screenHeight * assetsLoader.backTexture.getWidth()) / assetsLoader.backTexture.getHeight());
        bgHeight = 100 + GameConstants.screenHeight;

        offset = (int) (150 * GameConstants.screenWidth) / 720;
        btnLogin = new TextButton("Submit", skin);
        btnLogin.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight - offset);
        btnLogin.setSize(btnWidth, btnHeight);
        btnLogin.setColor(180f/255f, 180f/255f, 180f/255f, 255f/255f);

        addListener = false;
        btnLogin.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetsLoader.playButtonSound();
                processLogin();
            }
        });

        stage.addActor(btnLogin);

        //txtEmail.setText("erica.0701@hotmail.com");
        //txtPassword.setText("123456");

    }

    private void processLogin() {

        final String emailText = txtEmail.getText();
        String passwordText = txtPassword.getText();

        Pattern sPattern = Pattern.compile("([\\p{L}0-9.@_]+)");

        if (!sPattern.matcher(emailText).matches()){
            txtEmail.setText("");
            txtEmail.setMessageText(emailStr);
        }
        else if (!sPattern.matcher(passwordText).matches()){
            txtPassword.setText("");
            txtPassword.setMessageText(passwordStr);
        }
        else {

            GdxFIRApp.inst().configure();

            GdxFIRAuth.inst()
                    .signInWithEmailAndPassword(emailText, passwordText.toCharArray())
                    .then(new Consumer<GdxFirebaseUser>() {
                        @Override
                        public void accept(GdxFirebaseUser gdxFirebaseUser) {

                            databaseAccess.setUsernameByEmail(emailText);
                            authenticated = true;

                        }
                    }).fail(new BiConsumer<String, Throwable>() {
                        @Override
                        public void accept(String s, Throwable throwable) {

                            errorStr = errorLogin;

                        }
            });
        }
        addListener = true;
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);

        stage.getBatch().begin();

        stage.getBatch().setColor(GameConstants.Colors.STAGE_BATCH_CLEARANCE.getColor());

        // draw background
        stage.getBatch().draw(assetsLoader.backTexture,
                GameConstants.centerX - bgWidth / 2f,
                GameConstants.screenHeight / 2f - bgHeight / 2f,
                bgWidth,
                bgHeight);

        stage.getBatch().end();

        stage.draw();

        batch.begin();

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f,
                GameConstants.screenHeight * 3 / 4f - sprite.getHeight() / 2f);
        sprite.draw(batch);

        font.draw(batch,
                loginStr,
                GameConstants.centerX - titleWidth / 2f,
                GameConstants.screenHeight * 3 / 4f + titleHeight / 2f);

        error.draw(batch,
                errorStr,
                GameConstants.centerX - errorWidth / 2f,
                GameConstants.centerY - btnHeight - 150 - errorHeight - 5);

        batch.end();

        if (addListener) {
            btnLogin.addListener(new ClickListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    assetsLoader.playButtonSound();
                    processLogin();
                }
            });
            addListener = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            steinItUpGame.setInitialScreen();
        }

        if (authenticated){
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
        pixmap.dispose();
        texture.dispose();
        font.dispose();
        error.dispose();
        stage.dispose();
        skin.dispose();
    }
}