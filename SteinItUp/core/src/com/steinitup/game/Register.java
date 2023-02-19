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

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;

public class Register implements Screen {
    private final SteinItUpGame steinItUpGame;
    private SpriteBatch batch;
    private AssetsLoader assetsLoader;
    private DatabaseAccess databaseAccess;

    private boolean authenticated;

    private final Color background = GameConstants.Colors.BACKGROUND.getColor();

    private Texture texture;
    private BitmapFont font, error, line;
    private float titleWidth, titleHeight,
            errorWidth, errorHeight,
            line1Width, line1Height,
            line2Width, line2Height,
            line3Width, line3Height,
            line4Width, line4Height;
    private Pixmap pixmap;
    private Sprite sprite;

    public enum errorMessages {
        USERNAME, UNIQUE, EMAIL, PASSWORD, ACCOUNT, NONE
    }

    private String lineError, line1, line2, line3, line4;
    private final String registerStr = "Register";
    private final String usernameStr = "Insert username here";
    private final String emailStr = "Insert e-mail here";
    private final String passwordStr = "Insert password here";

    private Stage stage;
    private Skin skin;
    private final int btnWidth = (int) (500 * GameConstants.screenWidth) / 720;
    private final int btnHeight = (int) (100 * GameConstants.screenHeight) / 1280;
    private boolean addListener;
    private Button btnRegister;
    private float bgWidth, bgHeight;
    private TextField txtUsername, txtEmail, txtPassword;

    Register (SteinItUpGame steinItUpGame) {
        this.steinItUpGame = steinItUpGame;
        batch = new SpriteBatch();

        this.assetsLoader = steinItUpGame.assetsLoader;
        this.databaseAccess = steinItUpGame.databaseAccess;

        authenticated = false;

        // ----------- Font -----------
        texture = new Texture(Gdx.files.internal(GameConstants.fontTexture), true); // true enables mipmaps
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image

        float scale = (2f * GameConstants.screenWidth) / 720;
        font = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        font.getData().setScale(scale);
        font.setColor(Color.WHITE);

        scale = (0.85f * GameConstants.screenWidth) / 720;
        error = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        error.getData().setScale(scale);
        error.setColor(Color.RED);

        line = new BitmapFont(Gdx.files.internal(GameConstants.font), new TextureRegion(texture), false);
        line.getData().setScale(scale);
        line.setColor(Color.BLACK);

        // ----------- title & error message size -----------
        GlyphLayout layout = new GlyphLayout(); //dont do this every frame! Store it as member
        layout.setText(font, registerStr);
        titleWidth = layout.width; // contains the width of the current set text
        titleHeight = layout.height; // contains the height of the current set text

        updateErrorMessages(errorMessages.NONE);

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

        txtUsername = new TextField("", skin);
        txtUsername.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight + offset);
        txtUsername.setSize(btnWidth, btnHeight);
        txtUsername.setColor(255f/255f, 255f/255f, 255f/255f, 180f/255f);
        txtUsername.setMessageText(usernameStr);

        stage.addActor(txtUsername);

        txtEmail = new TextField("", skin);
        txtEmail.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight);
        txtEmail.setSize(btnWidth, btnHeight);
        txtEmail.setColor(255f/255f, 255f/255f, 255f/255f, 180f/255f);
        txtEmail.setMessageText(emailStr);

        stage.addActor(txtEmail);

        txtPassword = new TextField("", skin);
        txtPassword.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight * 2 - 25);
        txtPassword.setSize(btnWidth, btnHeight);
        txtPassword.setColor(255f/255f, 255f/255f, 255f/255f, 180f/255f);
        txtPassword.setMessageText(passwordStr);
        txtPassword.setPasswordMode(true);
        txtPassword.setPasswordCharacter('*');

        stage.addActor(txtPassword);

        // ----------- button -----------

        bgWidth = 100 + ((GameConstants.screenHeight * assetsLoader.backTexture.getWidth()) / assetsLoader.backTexture.getHeight());
        bgHeight = 100 + GameConstants.screenHeight;

        btnRegister = new TextButton("Submit", skin);
        btnRegister.setPosition(GameConstants.centerX - btnWidth / 2f,
                GameConstants.centerY - btnHeight * 3 - 50);
        btnRegister.setSize(btnWidth, btnHeight);
        btnRegister.setColor(180f/255f, 180f/255f, 180f/255f, 255f/255f);

        addListener = false;
        btnRegister.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                assetsLoader.playButtonSound();
                processCreateUser();
            }
        });

        stage.addActor(btnRegister);

        //txtUsername.setText("scoparia");
        //txtEmail.setText("erica.0701@hotmail.com");
        //txtPassword.setText("123456");
    }

    private void processCreateUser() {

        final String username = txtUsername.getText();
        final String email = txtEmail.getText();
        String password = txtPassword.getText();

        Pattern userFilter = Pattern.compile("(^[\\p{L}0-9.\\-_]+$)");
        Pattern emailFilter = Pattern.compile("(^[a-zA-Z0-9._]+[@][a-zA-Z0-9._]+[.][a-zA-Z0-9]+$)");
        Pattern passwordFilter = Pattern.compile("(^[\\p{L}0-9.\\-_]{6,}+$)");

        if (!userFilter.matcher(username).matches()) {

            updateErrorMessages(errorMessages.USERNAME);

        } else if (!databaseAccess.isUsernameUnique(username)) {

            updateErrorMessages(errorMessages.UNIQUE);

        } else if (!emailFilter.matcher(email).matches()){

            updateErrorMessages(errorMessages.EMAIL);

        } else if (!passwordFilter.matcher(password).matches()){

            updateErrorMessages(errorMessages.PASSWORD);

            txtPassword.setText("");
        } else {
            GdxFIRAuth.inst()
                    .createUserWithEmailAndPassword(email, password.toCharArray())
                    .then(new Consumer<GdxFirebaseUser>() {
                        @Override
                        public void accept(GdxFirebaseUser gdxFirebaseUser) {

                            databaseAccess.addUser(username, email);
                            authenticated = true;

                        }
                    });
                    /*
                    .fail(new BiConsumer<String, Throwable>() {
                        @Override
                        public void accept(String s, Throwable throwable) {

                            if (s.contains("The email address is already in use by another account")) {

                                updateErrorMessages(errorMessages.ACCOUNT);

                            }
                        }
                    });

                     */
        }
        addListener = true;
    }

    private void updateErrorMessages(errorMessages type){
        lineError = "";
        line1 = "";
        line2 = "";
        line3 = "";
        line4 = "";
        switch (type) {
            case USERNAME:
                lineError = "Invalid username";
                line1 = "Do not use space";
                line2 = "You can write accented letters, numbers,";
                line3 = "dash, underscore and period";
                break;
            case UNIQUE:
                lineError = "This username already exists";
                break;
            case PASSWORD:
                lineError = "Invalid password";
                line1 = "It must be at least 6 characters";
                line2 = "Do not use space";
                line3 = "You can write accented letters, numbers,";
                line4 = "dash, underscore and period";
                break;
            case EMAIL:
                lineError = "Invalid email";
                break;
            case ACCOUNT:
                lineError = "This account already exists";
                break;
            default:
                break;
        }

        GlyphLayout layout = new GlyphLayout();
        layout.setText(error, lineError);
        errorWidth = layout.width;
        errorHeight = layout.height;

        layout = new GlyphLayout();
        layout.setText(line, line1);
        line1Width = layout.width;
        line1Height = layout.height;

        layout = new GlyphLayout();
        layout.setText(line, line2);
        line2Width = layout.width;
        line2Height = layout.height;

        layout = new GlyphLayout();
        layout.setText(line, line3);
        line3Width = layout.width;
        line3Height = layout.height;

        layout = new GlyphLayout();
        layout.setText(line, line4);
        line4Width = layout.width;
        line4Height = layout.height;
    }

    private void displayErrorMessages(){

        float errorPosY = GameConstants.centerY - btnHeight * 3 - 50 - errorHeight - 5;
        error.draw(batch,
                lineError,
                GameConstants.centerX - errorWidth / 2f,
                errorPosY);

        errorPosY = errorPosY - line1Height - 15;
        line.draw(batch,
                line1,
                GameConstants.centerX - line1Width / 2f,
                errorPosY);

        errorPosY = errorPosY - line2Height - 10;
        line.draw(batch,
                line2,
                GameConstants.centerX - line2Width / 2f,
                errorPosY);

        errorPosY = errorPosY - line3Height - 10;
        line.draw(batch,
                line3,
                GameConstants.centerX - line3Width / 2f,
                errorPosY);

        errorPosY = errorPosY - line4Height - 10;
        line.draw(batch,
                line4,
                GameConstants.centerX - line4Width / 2f,
                errorPosY);
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
                GameConstants.screenHeight / 2f - bgHeight/ 2f,
                bgWidth,
                bgHeight);

        stage.getBatch().end();

        stage.draw();

        batch.begin();

        sprite.setPosition(GameConstants.centerX - sprite.getWidth() / 2f,
                GameConstants.screenHeight * 3 / 4f - sprite.getHeight() / 2f);
        sprite.draw(batch);

        font.draw(batch,
                registerStr,
                GameConstants.centerX - titleWidth / 2f,
                GameConstants.screenHeight * 3 / 4f + titleHeight / 2f);

        displayErrorMessages();

        batch.end();

        if (addListener) {
            btnRegister.addListener(new ClickListener() {
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    assetsLoader.playButtonSound();
                    processCreateUser();
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

