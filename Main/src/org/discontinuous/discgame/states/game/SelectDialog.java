package org.discontinuous.discgame.states.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.DialogOption;

/**
 * Created by Urk on 4/2/14.
 */
public class SelectDialog extends GameState {

    static int x;
    static int y;

    public static void setTooltipX(int x) {
        SelectDialog.x = x;
    }
    public static void setTooltipY(int y) { SelectDialog.y = y; }

    public static void drawShapes(ShapeRenderer shapes) {
        //Tooltip.drawDialogBox(shapes, x, y);
    }
    public static void drawBatch(SpriteBatch batch, DialogOption[] dialog_options, int screen_width) {
        // Draw Dialog Options which need to overlap the underlying element
        //for (DialogOption option : dialog_options) {
        //    option.drawDialogOption(batch);
        //}
        //Tooltip.drawDialogWidgets(screen_width/2 - 1200, 50, 240, 100, batch);
    }
}
