package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/8/14.
 */
public class Tooltip {
    // No constructor, this is a utility class
    static Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    static Color light_grey = new Color(0.8f, 0.8f, 0.8f, 1);

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
            shapes.triangle(x + width/2 - 50, y, x + width/2 + 50, y, pointer_x, pointer_y);
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
            shapes.triangle(x + width/2 - 30, y, x + width/2 + 30, y, pointer_x, pointer_y);
        }
    }

    // Draw a dialog area tooltip
    public static void drawDialogTooltip(ShapeRenderer shapes) {
        Tooltip.newTip(DiscGame.screen_width/2 - 230, 30, 450, 200,
                200, 250, dark_grey, light_grey, true, shapes);
    }
}
