package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;
import org.discontinuous.discgame.Tooltip;


/**
 * Created by Urk on 4/2/14.
 */
public class InDialog extends State{

    public static void drawShapes(ShapeRenderer shapes, boolean isPlayer, int screen_width) {
        // Find out who is speaking
        if (isPlayer) {
            Tooltip.newTip(screen_width / 2 - 200, 200, 400, 100, 200, 220, inner_color, outer_color, false, shapes);
            Tooltip.newTip(screen_width/2 - 200, 40, 400, 100, screen_width - 270, 160, inner_color, outer_color, false, shapes);
        }
        else {
            Tooltip.newTip(screen_width/2 - 200, 200, 400, 100, screen_width - 270, 220, inner_color, outer_color, false, shapes);
            Tooltip.newTip(screen_width/2 - 200, 40, 400, 100, 270, 150, inner_color, outer_color, false, shapes);
        }
        //
    }
    public static void drawBatch(SpriteBatch batch, BitmapFont font, boolean isPlayer, String argument, String response, int width_offset, int argument_height_offset, int response_height_offset) {
        StateHandling.animateGain(batch, isPlayer);

        // If player speaking, print line chosen, centered in the box.
        if (isPlayer) {
            font.drawWrapped(batch, argument, width_offset, 200 + argument_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            font.drawWrapped(batch, response, width_offset, 40 + response_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
        else {
            font.drawWrapped(batch, argument, width_offset, 200 + argument_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            font.drawWrapped(batch, response, width_offset, 40 + response_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
    }
}
