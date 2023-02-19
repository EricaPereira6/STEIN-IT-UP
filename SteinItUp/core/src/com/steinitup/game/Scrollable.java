package com.steinitup.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Scrollable {
    private Texture texture;
    private Vector2 position, velocity;
    private int width, height;
    private boolean horizontal;

    public Scrollable (Texture texture, float x, float y, int width, int height, boolean horizontal,
                       float scrollSpeed) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(scrollSpeed, 0);
        this.width = width;
        this.height = height;
        this.horizontal = horizontal;

    }

    public void update (float delta) {
        position.add(velocity.cpy().scl(delta));

        if ( position.x + width < 0) {
            position.x += width;
        }
    }

    public int draw (SpriteBatch batch, int screenHeight) {
        int mult = 0;

        while ((width * mult) < screenHeight) {
            if (horizontal) {
                batch.draw(texture, position.x + (width * mult), 0, width, height);
            } else {
                batch.draw(texture, 0, position.y + (height * mult), width, height);
            }
            mult++;
        }
        if (horizontal) {
            batch.draw(texture, position.x + (width * mult), 0, width, height);
        } else {
            batch.draw(texture, 0, position.y + (height * mult), width, height);
        }
        mult++;

        return mult;
    }
}
