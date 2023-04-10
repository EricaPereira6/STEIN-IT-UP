package com.mygdx.facesgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public class AssetsLoader extends BaseAssetsLoader {

    // externally used textures and animations
    Animation<TextureRegion> faceBoxAnimation, faceCircleAnimation, faceHexAnimation, faceTriAnimation;

    // load assets to textures and build animation
    AssetsLoader() {
        addDisposable(new Texture(Gdx.files.internal("face_box_tiled.png")));
        faceBoxAnimation = buildAnimationFromTexture((Texture) getDisposable(0),
                32, false, false,
                Animation.PlayMode.LOOP_PINGPONG);

        addDisposable(new Texture(Gdx.files.internal("face_circle_tiled.png")));
        faceCircleAnimation = buildAnimationFromTexture((Texture) getDisposable(1),
                32, false, false,
                Animation.PlayMode.LOOP_PINGPONG);

        addDisposable(new Texture(Gdx.files.internal("face_hexagon_tiled.png")));
        faceHexAnimation = buildAnimationFromTexture((Texture) getDisposable(2),
                32, false, false,
                Animation.PlayMode.LOOP_PINGPONG);

        addDisposable(new Texture(Gdx.files.internal("face_triangle_tiled.png")));
        faceTriAnimation = buildAnimationFromTexture((Texture) getDisposable(3),
                32, false, false,
                Animation.PlayMode.LOOP_PINGPONG);

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
    Animation <TextureRegion > buildAnimationFromTexture(Texture texture,
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

            if (++x > numberInWidth) {
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

