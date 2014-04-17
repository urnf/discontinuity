package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Contestant;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.Tooltip;

import java.util.ArrayList;

/**
 * Created by Urk on 4/2/14.
 */
public class SelectAbility extends State {

    static int x;
    public static void setTooltipX(int x) {
        SelectAbility.x = x;
    }

    public static void drawShapes(ShapeRenderer shapes) {
        Tooltip.drawDialogBox(shapes, x);
    }
    public static void drawBatch(SpriteBatch batch, ArrayList<Ability> abilities, int screen_width) {
        for (Ability ability : abilities) {
            ability.draw(batch);
        }
        Tooltip.drawDialogWidgets(screen_width/2 - 230, 50, 450, 200, batch);
    }
}
