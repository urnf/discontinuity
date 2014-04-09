package org.discontinuous.discgame.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.DialogOption;
import org.discontinuous.discgame.Tooltip;

/**
 * Created by Urk on 4/2/14.
 */
public class SelectDialog extends State {

    public static void drawShapes(ShapeRenderer shapes) {
        Tooltip.drawDialogBox(shapes);
    }
    public static void drawBatch(SpriteBatch batch, DialogOption[] dialog_options, int screen_width) {
        // Draw Dialog Options which need to overlap the underlying element
        for (DialogOption option : dialog_options) {
            option.drawDialogOption(batch);
        }
        Tooltip.drawDialogWidgets(screen_width/2 - 1200, 50, 240, 100, batch);
    }
}
