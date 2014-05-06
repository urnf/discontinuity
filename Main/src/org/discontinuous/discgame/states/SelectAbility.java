package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.Colors;
import org.discontinuous.discgame.Contestant;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.Tooltip;

import java.util.ArrayList;

/**
 * Created by Urk on 4/2/14.
 */
public class SelectAbility extends State {

    public static void drawShapes(ShapeRenderer shapes) {
        Tooltip.newTip (30, 30, 330, 60, 170, 85, Colors.ColorMap.get("dark_grey"), Colors.ColorMap.get("light_grey"), false, shapes);
        //Tooltip.drawDialogBox(shapes, x, y);
    }
    public static void drawBatch(SpriteBatch batch, ArrayList<Ability> abilities, int screen_width) {
        for (Ability ability : abilities) {
            ability.draw(batch);
        }
        //Tooltip.drawDialogWidgets(screen_width/2 - 230, 50, 450, 200, batch);
    }
}
