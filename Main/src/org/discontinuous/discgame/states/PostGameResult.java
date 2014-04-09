package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.EndGameOption;
import org.discontinuous.discgame.Tooltip;

import java.util.ArrayList;

/**
 * Created by Urk on 4/3/14.
 */
public class PostGameResult extends State {

    public static void drawShapes(ShapeRenderer shapes, int screen_width)  {
        Tooltip.newTip(screen_width/2 - 200, 200, 400, 100, 260, 260, inner_color, outer_color, false, shapes);
        Tooltip.newTip(screen_width/2 - 200, 40, 400, 100, screen_width - 270, 160, inner_color, outer_color, false, shapes);
    }

    public static void drawBatch(SpriteBatch batch, EndGameOption selected_endgame_option, int dialog_width_offset, int player_height_offset, int opponent_height_offset) {
        selected_endgame_option.font.drawWrapped(batch, selected_endgame_option.yi_text, dialog_width_offset, 200 + player_height_offset, 380);
        Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
        selected_endgame_option.font.drawWrapped(batch, selected_endgame_option.arlene_text, dialog_width_offset, 40 + opponent_height_offset, 380);
        Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);

    }
}