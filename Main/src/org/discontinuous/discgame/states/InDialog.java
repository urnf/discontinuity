package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;
import org.discontinuous.discgame.Tooltip;


/**
 * Created by Urk on 4/2/14.
 */
public class InDialog {

    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

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
    public static void drawBatch(SpriteBatch batch, boolean isPlayer) {
        StateHandling.animateGain(batch, isPlayer);

        // If player speaking, print line chosen, centered in the box.
        if (isPlayer) {
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.yi_dialog, StateHandling.dialog_width_offset, 200 + StateHandling.yi_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.arlene_resp_dialog, StateHandling.dialog_width_offset, 40 + StateHandling.arlene_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
        else {
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.arlene_dialog, StateHandling.dialog_width_offset, 200 + StateHandling.arlene_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 200, 400, 100, batch);
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.yi_resp_dialog, StateHandling.dialog_width_offset, 40 + StateHandling.yi_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(StateHandling.dialog_width_offset - 10, 40, 400, 100, batch);
        }
    }
}
