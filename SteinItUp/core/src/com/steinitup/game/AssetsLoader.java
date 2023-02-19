package com.steinitup.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class AssetsLoader extends BaseAssetsLoader {

    Animation<TextureRegion> lifeAnimation;

    final Texture logoTexture, backTexture, userTexture, photoTexture, makerTexture, menuTexture, hintTexture,
            seaTexture, purpleTurtleTexture, blueTurtleTexture, greenTurtleTexture, yellowTurtleTexture,
            pinkTurtleTexture, redTurtleTexture, grayTurtleTexture;
    final String[] names = {"markowork", "markorder", "innawork", "margeorder", "tinawork", "joannaorder",
            "linawork", "ginaorder", "mariawork", "lunaorder", "sophiaorder", "lancywork", "gustaffwork",
            "kevinwork", "kevinorder", "lanceorder", "kalvinwork", "gustafforder"};
    HashMap<String, Sound> sounds;
    HashMap<String, Texture> pictures;

    final Sound correct, wrong, button, music;


    AssetsLoader () {

        // Animations
        addDisposable(new Texture(Gdx.files.internal("lifeanimation1.png")));
        lifeAnimation = buildAnimationFromTexture((Texture) getDisposable(0),
                172, false, false,
                Animation.PlayMode.LOOP_PINGPONG);

        // Textures
        addDisposable(logoTexture = new Texture(Gdx.files.internal("logoheadbg.png")));
        addDisposable(backTexture = new Texture(Gdx.files.internal("bgcolor.png")));
        addDisposable(userTexture = new Texture(Gdx.files.internal("userhead.png")));
        addDisposable(photoTexture = new Texture(Gdx.files.internal("userhead.png")));
        addDisposable(makerTexture = new Texture(Gdx.files.internal("makerhead.png")));
        addDisposable(menuTexture = new Texture(Gdx.files.internal("menu.png")));
        addDisposable(hintTexture = new Texture(Gdx.files.internal("hint.png")));
        // turtle's sequence
        addDisposable(seaTexture = new Texture(Gdx.files.internal("turtles/sea.png")));
        addDisposable(purpleTurtleTexture = new Texture(Gdx.files.internal("turtles/turtlepurple.png")));
        addDisposable(blueTurtleTexture = new Texture(Gdx.files.internal("turtles/turtleblue.png")));
        addDisposable(greenTurtleTexture = new Texture(Gdx.files.internal("turtles/turtlegreen.png")));
        addDisposable(yellowTurtleTexture = new Texture(Gdx.files.internal("turtles/turtleyellow.png")));
        addDisposable(pinkTurtleTexture = new Texture(Gdx.files.internal("turtles/turtlepink.png")));
        addDisposable(redTurtleTexture = new Texture(Gdx.files.internal("turtles/turtlered.png")));
        addDisposable(grayTurtleTexture = new Texture(Gdx.files.internal("turtles/turtlegray.png")));
        // time to Listen
        pictures = new HashMap<>();
        for (String name : names) {
            pictures.put(name, new Texture(Gdx.files.internal("picturePeople/" + name + ".png")));
            addDisposable(pictures.get(name));
        }

        sounds = new HashMap<>();
        for (String name : names) {
            sounds.put(name, Gdx.audio.newSound(Gdx.files.internal("audioPeople/" + name + ".mp3")));
            addDisposable(sounds.get(name));
        }

        addDisposable(correct = Gdx.audio.newSound(Gdx.files.internal("correct.mp3")));
        addDisposable(wrong = Gdx.audio.newSound(Gdx.files.internal("incorrect.mp3")));
        addDisposable(button = Gdx.audio.newSound(Gdx.files.internal("button1.mp3")));
        addDisposable(music = Gdx.audio.newSound(Gdx.files.internal("music.mp3")));
    }

    public void playCorrectSound() {
        if (MyPreference.isSoundOn()) {
            correct.play();
        }
    }
    public void playWrongSound() {
        if (MyPreference.isSoundOn()) {
            wrong.play();
        }
    }
    public void playButtonSound() {
        if (MyPreference.isSoundOn()) {
            button.play();
        }
    }
    public void playMusic () {
        if (MyPreference.isMusicOn()) {
            music.stop();
            music.play();
            music.loop();
        }
    }
    public void stopMusic () {
        music.stop();
    }
}


class BaseAssetsLoader {
    private List<Disposable> disposableResources = new ArrayList<>();

    void addDisposable(Disposable disposable) {
        disposableResources.add(disposable);
    }

    Disposable getDisposable(int index) {
        return disposableResources.get(index);
    }

    // Extract square textureRegions from texture and build animation with them static
    Animation<TextureRegion> buildAnimationFromTexture(Texture texture,
                                                       int textRegSize,
                                                       boolean flipHorizontally,
                                                       boolean flipVertically ,
                                                       Animation.PlayMode playMode) {
        int numberInWidth = texture.getWidth() / textRegSize;
        int numberInHeight = texture.getHeight() / textRegSize;
        int numberOfTextureRegions = numberInWidth * numberInHeight;

        TextureRegion[] texRegions = new TextureRegion[numberOfTextureRegions];
        for (int i = 0, x = 0, y = 0; i < texRegions.length; ++i) {

            texRegions[i] = new TextureRegion(texture, x * textRegSize, y * textRegSize,
                    textRegSize, textRegSize);

            texRegions[i].flip(flipHorizontally, flipVertically);

            if (++x >= numberInWidth) {
                x = 0; ++y;
            }
        }
        Animation <TextureRegion > animation = new Animation <>(0.5f, texRegions);
        animation.setPlayMode(playMode);

        return animation;
    }

    void dispose() {
        for (Disposable disposableResource : disposableResources) {
            disposableResource.dispose();
        }
    }
}
