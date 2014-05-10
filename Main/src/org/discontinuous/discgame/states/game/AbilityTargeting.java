package org.discontinuous.discgame.states.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Tooltip;

/**
 * Created by Urk on 4/2/14.
 */
public class AbilityTargeting extends GameState {

    public static void drawShapes(ShapeRenderer shapes, int screen_width) {
        Tooltip.newTip(30, 30, 400, 50, 30, 30, inner_color, outer_color, false, shapes);
    }
    public static void drawBatch(SpriteBatch batch, BitmapFont font, String player_text, int dialog_width_offset, int player_dialog_height_offset) {
        font.drawWrapped(batch, player_text, 30, 10 + player_dialog_height_offset, 380);
    }
}