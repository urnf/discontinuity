package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;
import org.discontinuous.discgame.Tooltip;
import org.discontinuous.discgame.Colors;

/**
 * Created by Urk on 4/2/14.
 */
public class AbilityDialog {

    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

    public static void drawShapes(ShapeRenderer shapes, int screen_width) {
        Tooltip.newTip(screen_width/2 - 200, 200, 400, 100, 200, 220, inner_color, outer_color, false, shapes);
    }
    public static void drawBatch(SpriteBatch batch, String player_text, int dialog_width_offset, int player_dialog_height_offset) {
        StateHandling.animateGain(batch, true);
        DiscGame.movestats_font.drawWrapped(batch, player_text, dialog_width_offset, 200 + player_dialog_height_offset, 380);
        Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
    }
}