package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/28/14.
 */
public class Icon extends Entity  {
    int tooltip_x;
    int tooltip_y;
    int tooltip_width;
    int tooltip_height;
    String text;

    public Icon(int x, int y, int width, int height, int tooltip_x, int tooltip_y, int tooltip_width, int tooltip_height, String text) {
        super(x, y, width, height);

        this.tooltip_x = tooltip_x;
        this.tooltip_y = tooltip_y;
        this.tooltip_width = tooltip_width;
        this.tooltip_height = tooltip_height;
        this.text = text;

        // Entity has action on hover, add to hover list
        DiscGame.hover_list.add(this);
    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.text_font.drawWrapped(batch, text, tooltip_x, tooltip_y + tooltip_height, tooltip_width - 20);
        Tooltip.drawDialogWidgets(tooltip_x, tooltip_y, tooltip_width, tooltip_height, batch);
        //DiscGame.text_font_small.draw(batch, "X: " + Gdx.input.getX() + " Y: " + Gdx.input.getY() + " Hover item: " + DiscGame.hover.toString(), x, y + 30);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
        Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
        Tooltip.newTip(tooltip_x, tooltip_y,
                tooltip_width, tooltip_height,
                x + width/2, y + height/2,
                dark_grey, light_grey, false, shapes);
    }

}
