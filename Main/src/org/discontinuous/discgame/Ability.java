package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 2/3/14.
 */
public class Ability extends Entity {
    int tooltip_x = 30;
    int tooltip_y = 30;
    int tooltip_width = 100;
    int tooltip_height = 50;
    String text;

    public Ability (int x, int y, int width, int height, String text) {
        super(x, y, width, height);
        DiscGame.hover_list.add(this);
        DiscGame.click_list.add(this);
        this.text = text;

    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.text_font.drawWrapped(batch, text, tooltip_x, tooltip_y + tooltip_height, tooltip_width - 20);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
        Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
        Tooltip.newTip(tooltip_x, tooltip_y,
                tooltip_width, tooltip_height,
                x + width/2, y + height/2,
                dark_grey, light_grey, false, shapes);
    }

    public void clickHandler() {
        // Use ability if enough inspiration
    }
}
