package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;
import org.discontinuous.discgame.Tooltip;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.abilities.Ability;

/**
 * Created by Urk on 4/2/14.
 */
public class AbilityDialog extends State {

    public static void drawShapes(ShapeRenderer shapes, int screen_width) {
        Tooltip.newTip(30, 30, 400, 100, 200, 220, inner_color, outer_color, false, shapes);
    }
    public static void drawBatch(SpriteBatch batch, Ability ability_selected, int dialog_width_offset, int player_dialog_height_offset) {
        //StateHandling.animateGain(batch, true);
        ability_selected.drawDialog(batch, 30, 30 + player_dialog_height_offset, 380);
        //Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
    }
}