package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.Tooltip;
import org.discontinuous.discgame.Colors;

/**
 * Created by Urk on 4/2/14.
 */
public class AbilityTargeting extends State {

    public static void drawShapes(ShapeRenderer shapes, int screen_width) {
        Tooltip.newTip(screen_width / 2 - 200, 200, 400, 100, screen_width / 2 - 180, 250, inner_color, outer_color, false, shapes);
    }
    public static void drawBatch(SpriteBatch batch, BitmapFont font, String player_text, int dialog_width_offset, int player_dialog_height_offset) {
        font.drawWrapped(batch, player_text, dialog_width_offset, 200 + player_dialog_height_offset, 380);
    }
}