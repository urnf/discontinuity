package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Ability;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.Tooltip;

/**
 * Created by Urk on 4/2/14.
 */
public class SelectAbility {
    public static void drawShapes(ShapeRenderer shapes) {
        Tooltip.drawDialogBox(shapes);
    }
    public static void drawBatch(SpriteBatch batch, int screen_width) {
        for (Ability ability : DiscGame.yi.abilities) {
            ability.draw(batch);
        }
        Tooltip.drawDialogWidgets(screen_width/2 - 230, 50, 450, 200, batch);
    }
}
