package com.mygdx.cowboygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Scrollable {
    private Texture texture;
    private Vector2 position, velocity;
    private int width, height;

    public Scrollable (Texture texture, float x, float y, int width, int height,
                       float scrollSpeed) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(scrollSpeed, 0);
        this.width = width;
        this.height = height;

    }

    public void update (float delta) {
        position.add(velocity.cpy().scl(delta));

        if ( position.x + width < 0) {
            position.x += width;
        }
    }

    public int draw (SpriteBatch batch, int screenWidth) {
        int mult = 0;

        while ((width * mult) < screenWidth) {
            batch.draw(texture, position.x + (width * mult), 0, width, height);
            mult++;
        }

        batch.draw(texture, position.x + (width * mult), 0, width, height);
        mult++;

        return mult;
    }
}
