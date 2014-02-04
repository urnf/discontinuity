package org.discontinuous.discgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by Urk on 1/8/14.
 */
public class Portrait extends Entity {
    int hover_x;
    int hover_y;
    int pointer_x_offset;
    int pointer_y_offset;
    int wrap_size;
    float text_height;
    float text_width;
    Contestant contestant;
    String hover_text;
    Entity combos;
    Entity movestats;

    public Portrait(Texture combo_img, int x, int y, int width, int height, int hover_x, int hover_y, int pointer_x_offset, int pointer_y_offset, int wrap_size, String hover_text) {
        super(x, y, width, height);
        this.hover_x = hover_x;
        this.hover_y = hover_y;
        this.hover_text = hover_text;
        BitmapFont.TextBounds bounds = DiscGame.text_font.getWrappedBounds(hover_text, wrap_size);
        text_height = bounds.height + 150;
        text_width = bounds.width;
        this.wrap_size = wrap_size;
        this.pointer_x_offset = pointer_x_offset;
        this.pointer_y_offset = pointer_y_offset;

        // set up combo and movestats image position
        combos = new Entity(DiscGame.screen_width/2 - 250, (int) (hover_y - text_height - 5), 113, 143);
        combos.setImg(combo_img);
        movestats = new Entity(DiscGame.screen_width/2 - 20, (int) (hover_y - text_height + 105), 256, 32);
        movestats.setImg(DiscGame.movestats);

        // Entity has action on hover, add to hover list
        DiscGame.hover_list.add(this);
        DiscGame.shape_hover_list.add(this);
    }

    // Not part of the constructor since it'll be null at Portrait instantiation
    public void setContestant(Contestant contestant) {
        this.contestant = contestant;
    }

    public void drawHover(SpriteBatch batch) {
        DiscGame.text_font.drawWrapped(batch, hover_text, hover_x, hover_y, wrap_size);
        contestant.draw_stats(batch, hover_x + 125, (int) (hover_y - text_height + 95));
        combos.draw(batch);
        movestats.draw(batch);
        Tooltip.drawDialogWidgets(hover_x, (int) (hover_y - text_height), (int) text_width, (int) text_height, batch);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
        Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);
        Tooltip.newTip(hover_x, (int) (hover_y - text_height),
                (int) text_width, (int) text_height,
                pointer_x_offset, pointer_y_offset,
                dark_grey, light_grey, false, shapes);

    }
}
