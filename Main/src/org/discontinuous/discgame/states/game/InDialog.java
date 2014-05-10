package org.discontinuous.discgame.states.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Tooltip;


/**
 * Created by Urk on 4/2/14.
 */
public class InDialog extends GameState {

    static int x;

    public static void setTooltipX(int x) {
        InDialog.x = x;
    }

    public static void drawShapes(ShapeRenderer shapes, boolean isPlayer, int screen_width) {
        // Find out who is speaking
        if (isPlayer) {
            Tooltip.newTip(x, 40, 355, 75, 190, 130, inner_color, outer_color, false, shapes);
            //Tooltip.newTip(x, 40, 400, 100, screen_width - 270, 160, inner_color, outer_color, false, shapes);
        }
        else {
            Tooltip.newTip(x, 40, 355, 75, screen_width - 190, 130, inner_color, outer_color, false, shapes);
            //Tooltip.newTip(x, 40, 400, 100, 270, 150, inner_color, outer_color, false, shapes);
        }
        //
    }
    public static void drawBatch(SpriteBatch batch, BitmapFont font, boolean isPlayer, String argument, String response, int width_offset, int argument_height_offset, int response_height_offset) {
        //StateHandling.animateGain(batch, isPlayer);

        // If player speaking, print line chosen, centered in the box.
        if (isPlayer) {
            font.drawWrapped(batch, argument, width_offset + 20, 40 + argument_height_offset, 350);
            //Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            //font.drawWrapped(batch, response, width_offset, 40 + response_height_offset, 380);
            //Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
        else {
            font.drawWrapped(batch, argument, width_offset + 20, 40 + argument_height_offset, 350);
            //Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            //font.drawWrapped(batch, response, width_offset, 40 + response_height_offset, 380);
            //Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
    }
}
