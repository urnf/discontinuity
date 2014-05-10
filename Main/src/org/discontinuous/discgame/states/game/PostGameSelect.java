package org.discontinuous.discgame.states.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.EndGameOption;
import org.discontinuous.discgame.Tooltip;

import java.util.ArrayList;

/**
 * Created by Urk on 4/3/14.
 */
public class PostGameSelect extends GameState {

    public static void drawShapes(ShapeRenderer shapes, int options_size, int screen_width)  {
        for (int i = 0; i < options_size; i++) {
            Tooltip.newTip(screen_width/2 - 200, 600 - 100 * i, 400, 50, screen_width/2 - 200, 600 - 100 * i, inner_color, outer_color, false, shapes);
        }
    }

    public static void drawBatch(SpriteBatch batch, BitmapFont font, ArrayList<EndGameOption> endgame_options) {
        for (EndGameOption option: endgame_options) {
            // TODO: Fix this design.  Doing this requires making cell elements public!
            font.drawWrapped(batch, Integer.toString(option.dp_cost), option.x + 10, option.y + 75, option.width - 40);
            option.font.drawWrapped(batch, option.option_text, option.x + 70, option.y + 75, option.width - 70);
        }
    }
}