package com.mygdx.cowboygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public class AssetsLoader extends BaseAssetsLoader {
    // externally used textures and animations
    Texture backTexture, midTexture, frontTexture;
    Animation <TextureRegion> cowboyAnimation;

    AssetsLoader () {
        Texture cowboyTexture;

        addDisposable(backTexture = new Texture(Gdx.files.internal("parallax_background_layer_back.png")));
        addDisposable(midTexture = new Texture(Gdx.files.internal("parallax_background_layer_mid.png")));
        addDisposable(frontTexture = new Texture(Gdx.files.internal("parallax_background_layer_front.png")));
        addDisposable(new Texture(Gdx.files.internal("cowboy_run_sprite.png")));

        cowboyTexture = (Texture) getDisposable(3);
        cowboyAnimation = buildAnimationFromTexture(cowboyTexture,175,
                true, false, Animation.PlayMode.NORMAL);
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