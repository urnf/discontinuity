package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.Tooltip;


/**
 * Created by Urk on 4/2/14.
 */
public class InDialog {
    public static void drawShapes(ShapeRenderer shapes, boolean isPlayer, int screen_width) {
        // Find out who is speaking
        if (isPlayer) {
            Tooltip.newTip(screen_width / 2 - 200, 200, 400, 100, 200, 220, Colors.ColorMap.get("dark_grey"), Colors.LIGHT_GREY, false, shapes);
            Tooltip.newTip(screen_width/2 - 200, 40, 400, 100, screen_width - 270, 160, Colors.DARK_GREY, Colors.LIGHT_GREY, false, shapes);
        }
        else {
            Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, DiscGame.screen_width - 270, 220, Colors.DARK_GREY, Colors.LIGHT_GREY, false, shapes);
            Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, 270, 150, Colors.DARK_GREY, Colors.LIGHT_GREY, false, shapes);
        }
        //
    }
    public static void drawBatch(SpriteBatch batch) {
        animateGain(batch, currentSpeaker.player);

        // If player speaking, print line chosen, centered in the box.
        if (currentSpeaker.player) {
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.yi_dialog, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.arlene_resp_dialog, dialog_width_offset, 40 + arlene_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
        }
        else {
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.arlene_dialog, dialog_width_offset, 200 + arlene_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
            DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.yi_resp_dialog, dialog_width_offset, 40 + yi_dialog_height_offset, 380);
            Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
        }
    }
}
