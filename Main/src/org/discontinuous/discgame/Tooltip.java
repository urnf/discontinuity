package org.discontinuous.discgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors.*;

import java.util.ArrayList;

/**
 * Created by Urk on 1/8/14.
 */
public class Tooltip {
    // No constructor, this is a utility class

    static Sprite upper_left = new Sprite(new Texture(Gdx.files.internal("img/upper-left.png")), 64, 64);
    static Sprite upper_right = new Sprite(new Texture(Gdx.files.internal("img/upper-right.png")), 64, 64);
    static Sprite lower_left = new Sprite(new Texture(Gdx.files.internal("img/lower-left.png")), 64, 64);
    static Sprite lower_right = new Sprite(new Texture(Gdx.files.internal("img/lower-right.png")), 64, 64);
    static int dialog_height = 50;

    // Draws a tooltip rectangle at the coordinates with width/height, with a pointer pointing to the location noted
    // So far tip is only attached to bottom of tooltip border, may add option later
    public static void newTip (int x, int y, int width, int height, int pointer_x, int pointer_y, Color fill_color, Color border_color, boolean thought, ShapeRenderer shapes) {

        shapes.setColor(border_color);
        shapes.rect(x - 20, y - 20, width + 40, height + 40);
        int center_x = x + width/2;
        int center_y = y + height/2;
        if (thought) {
            //draws ellipses to the pointer x,y from center of x,y
            for (int i = 1; i < 6; i++) {
                shapes.ellipse(pointer_x + ((center_x - pointer_x)*i/6) - 30, pointer_y + ((center_y - pointer_y)*i/6) - 15, 30, 20);
            }
        }
        else {
            shapes.triangle(x + width/2, y + height/2 - 30, x + width/2, y + height/2 + 30, pointer_x, pointer_y);
        }
        shapes.setColor(fill_color);
        shapes.rect(x - 15, y - 15, width + 30, height + 30);
        if (thought) {
            //draws ellipses to the pointer x,y from center of x,y
            for (int i = 1; i < 6; i++) {
                shapes.ellipse(pointer_x + ((center_x - pointer_x)*i/6) - 25, pointer_y + ((center_y - pointer_y)*i/6) - 12, 20, 15);
            }
        }
        else {
            shapes.triangle(x + width/2, y + height/2 - 15, x + width/2, y + height/2 + 15, pointer_x, pointer_y);
        }
    }

    //TODO: These two methods do NOT belong here.  Move it.
    // Draw a dialog area tooltip
    public static void drawDialogBox(ShapeRenderer shapes) {
        Tooltip.newTip(DiscGame.screen_width/2 - 120, dialog_height, 240, 100,
                200, 250, dark_grey, light_grey, true, shapes);
    }
    public static void drawDialogWidgets(int x, int y, int width, int height, SpriteBatch batch) {
        upper_left.setPosition(x - 30, y + height - 32);
        upper_right.setPosition(x + width - 21,  y + height - 21);
        lower_left.setPosition(x - 30, y - 32);
        lower_right.setPosition(x + width - 32, y - 30);
        upper_left.draw(batch);
        upper_right.draw(batch);
        lower_left.draw(batch);
        lower_right.draw(batch);

    }
}
