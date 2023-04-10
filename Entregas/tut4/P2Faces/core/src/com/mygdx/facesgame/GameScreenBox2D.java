package com.mygdx.facesgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

class GameScreenBox2D implements Screen, InputProcessor {
    private final MyGdxGame mainClass;
    private String appStateTag = "GameScreenBox2D";
    private SpriteBatch batch;

    private int screenWidth;
    private int screenHeight;

    private AssetsLoader assets;

    private final World world;
    private final Box2DDebugRenderer debugRenderer;
    private final OrthographicCamera camera;

    //world objects
    private static final float WORLD_STEP = 1 / 60f;
    private static final int WORLD_VELOCITY_ITERATIONS = 6;
    private static final int WORLD_POSITION_ITERATIONS = 2;
    //private int frameLength = 100;
    private int frameLength = 50;  // 720 x 1280 -> my device
    //private int frameLength = 75;  // 1080 x 2028 -> my emulator PIXEL 3
    private int FIGURE_SIZE;

    // random world objects
    private RandomXS128 random = new RandomXS128(System.currentTimeMillis());

    //animations for bodies
    private Array<Body> bodies = new Array<>();
    private float elapsedTime;

    //fountain
    private long timestamp;
    private final long FOUNTAIN_TIME = 10000;
    private final int FOUNTAIN_BODIES = 10;
    private final int MAX_BODIES = 80;
    private BitmapFont font;
    private boolean screenFull;

    public enum FormType {
        Box, Circle, Triangle, Hexagon
    }

    GameScreenBox2D(MyGdxGame mainClass) {
        this.mainClass = mainClass;

        this.screenWidth = mainClass.screenWidth;
        this.screenHeight = mainClass.screenHeight;

        this.assets = mainClass.assets;
        int assetsWidth = mainClass.assetsWidth;
        int assetsHeight = mainClass.assetsHeight;

        Gdx.app.log(appStateTag, "constructor GameScreenBox2D ");

        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        // Builds a Box2D world
        // First argument ( Vector ) sets the horizontal and vertical gravity forces
        // Second argument tells to render all bodies , even inactive bodies
        world = new World(new Vector2(0, 0), false);

        // Builds a Box2DDebugRenderer object
        debugRenderer = new Box2DDebugRenderer();

        // build OrthographicCamera
        camera = buildCamera();

        // Update the batch with our Camera ’s view and projection matrices
        batch.setProjectionMatrix(camera.combined);

        // create world objects
        FIGURE_SIZE = assetsHeight * 2 / 3;

        createWorldBoundaries();
        createBoxBody(assetsWidth + frameLength - 10, assetsHeight);
        createCircleBody(assetsWidth + frameLength - 10, assetsHeight * 2);
        createTriBody(assetsWidth + frameLength - 10, assetsHeight * 3);
        createHexBody(assetsWidth + frameLength - 10, assetsHeight * 4);

        //fountain
        timestamp = System.currentTimeMillis() -  FOUNTAIN_TIME;

        font = new BitmapFont();
        font.setColor(new Color(0xff0000ff));
        font.getData().setScale(4.0f, 3.5f);
        screenFull = false;

        world.setGravity(new Vector2(0, -20));
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.5f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // show debug information
        debugRenderer.render(world, camera.combined);

        elapsedTime += delta;
        
        batch.begin();

        fountain();

        if (screenFull) {
            font.draw(batch, "It's full!", (screenWidth / 2f) - 50, screenHeight - 5);
        }

        world.getBodies(bodies);

        for (Body body : bodies) {

            Object data = body.getUserData();
            if (data != null) {
                Animation<TextureRegion> animation;

                if (data == FormType.Circle) {
                    animation = assets.faceCircleAnimation;
                }
                else if (data == FormType.Triangle) {
                    animation = assets.faceTriAnimation;
                }
                else if (data == FormType.Hexagon) {
                    animation = assets.faceHexAnimation;
                }
                else {
                    animation = assets.faceBoxAnimation;
                }

                TextureRegion region = animation.getKeyFrame(elapsedTime, true);
                batch.draw(region, body.getPosition().x - FIGURE_SIZE / 2f,
                        body.getPosition().y - FIGURE_SIZE / 2f,
                        FIGURE_SIZE / 2f, FIGURE_SIZE / 2f,
                        FIGURE_SIZE, FIGURE_SIZE, 1, 1,
                        body.getAngle() * MathUtils.radiansToDegrees);
            }
        }

        batch.end();

        // Make a world step
        world.step(WORLD_STEP, WORLD_VELOCITY_ITERATIONS, WORLD_POSITION_ITERATIONS);

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

        Gdx.app.log(appStateTag, "onDispose");
    }

    // class method
    private OrthographicCamera buildCamera () {
        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
        // Set camera ’s position in the center of the screen
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
        // Update and return the camera
        camera.update();
        return camera ;
    }

    // Create 4 boundary objects with thickness of .5f, limiting the device screen
    // Build one static body at 100 of the top , bottom , left and right
    private void createWorldBoundaries () {

        // bottom - .5f is half pixel
        createStaticBody(0, frameLength, camera.viewportWidth, .5f);
        // top
        createStaticBody(0, camera.viewportHeight - frameLength, camera.viewportWidth, .5f);
        // left
        createStaticBody(frameLength, 0, .5f, camera.viewportHeight);
        // right
        createStaticBody(camera.viewportWidth - frameLength, 0, 0.5f, camera.viewportHeight);
        //createStaticBody((camera.viewportWidth / 4) + frameLength - 50, 0, 0.5f, camera.viewportHeight);
    }

    private void createStaticBody (float x, float y, float width, float height) {
        BodyDef WallBodyDef = new BodyDef();
        WallBodyDef.position.set(new Vector2(x, y));

        Body wallBody = world.createBody(WallBodyDef);

        PolygonShape wallBox = new PolygonShape();
        wallBox.setAsBox(width, height);

        wallBody.createFixture(wallBox, 0.0f);
    }

    private void createBoxBody (int screenX, int screenY) {

        PolygonShape box = new PolygonShape();
        box.setAsBox(FIGURE_SIZE / 2f, FIGURE_SIZE / 2f); // hx/hy half - width / height
        createBody(screenX, screenY, box, FormType.Box);
    }

    private void createCircleBody (int screenX, int screenY) {
        CircleShape circle = new CircleShape();
        circle.setRadius(FIGURE_SIZE / 2f);
        createBody(screenX, screenY, circle, FormType.Circle);
    }

    private void createHexBody (int screenX, int screenY) {
        PolygonShape hex = new PolygonShape ();
        Vector2 [] vertices = new Vector2[6];
        vertices [0] = new Vector2 (0, FIGURE_SIZE / 2f); // top
        vertices [1] = new Vector2 (0, -FIGURE_SIZE / 2f); // bottom
        vertices [2] = new Vector2 ( -FIGURE_SIZE * 0.4375f, FIGURE_SIZE * 0.2578125f);
        vertices [3] = new Vector2 ( -FIGURE_SIZE * 0.4375f, -FIGURE_SIZE * 0.2578125f);
        vertices [4] = new Vector2 ( FIGURE_SIZE * 0.4375f, FIGURE_SIZE * 0.2578125f);
        vertices [5] = new Vector2 ( FIGURE_SIZE * 0.4375f, -FIGURE_SIZE * 0.2578125f);
        hex.set(vertices);
        createBody(screenX, screenY, hex, FormType.Hexagon);
    }

    private void createTriBody ( int screenX , int screenY ) {
        PolygonShape tri = new PolygonShape ();
        Vector2 [] vertices = new Vector2[3];
        vertices [0] = new Vector2 (0, FIGURE_SIZE / 2f); // bottom
        vertices [1] = new Vector2 (-FIGURE_SIZE * 0.4375f, -FIGURE_SIZE / 2f);
        vertices [2] = new Vector2 ( FIGURE_SIZE * 0.4375f, -FIGURE_SIZE / 2f);
        tri.set(vertices);
        createBody(screenX, screenY, tri, FormType.Triangle);
    }

    private void createBody (int screenX, int screenY, Shape shape, FormType formType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(screenX, screenY);

        Body body = world.createBody(bodyDef);
        body.createFixture(createFixtureDef(shape));
        body.setUserData(formType);
    }

    private FixtureDef createFixtureDef (Shape shape) {
        FixtureDef fixtureDef = new FixtureDef ();

        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.0f;
        fixtureDef.restitution = 1;

        return fixtureDef;
    }

    private boolean addRandomBody (int screenX, int screenY) {
        if (world.getBodyCount() < MAX_BODIES) {
            switch (FormType.values()[random.nextInt(FormType.values().length)]) {
                case Box:
                    createBoxBody(screenX, screenY);
                    break;
                case Circle:
                    createCircleBody(screenX, screenY);
                    break;
                case Triangle:
                    createTriBody(screenX, screenY);
                    break;
                case Hexagon:
                    createHexBody(screenX, screenY);
                    break;
            }
            return true;
        }
        screenFull = true;
        return false;
    }

    private void fountain () {
        if (System.currentTimeMillis() - timestamp > FOUNTAIN_TIME){
            for (int i = 0; i < FOUNTAIN_BODIES; i++) {
                boolean added = addRandomBody((int) (screenWidth / 2f),
                        screenHeight - frameLength * 3);
                if (!added) {
                    break;
                }
            }
            timestamp = System.currentTimeMillis();
        }
    }

        @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.BACK){
            MyGdxGame.log(" back pressed ");
            mainClass.setInitialScreen();
            return true ;
        }
        return false ;
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
        MyGdxGame.log(" touchDown on x:" + screenX + ", y " + screenY);
        int y = screenHeight - screenY;
        MyGdxGame.log(" insert object at x:" + screenX + ", y " + y);
        addRandomBody(screenX, y);
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
