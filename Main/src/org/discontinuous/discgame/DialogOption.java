package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 1/12/14.
 */
public class DialogOption extends Entity {
    // the associated cell with this dialog option
    Cell cell;
    // Need to offset the text to match with hover
    int y_offset;
    final int dialog_y_offset = 4;
    static Color dark_grey = new Color(0.15f, 0.15f, 0.15f, 1);
    static Color light_grey = new Color(0.7f, 0.7f, 0.7f, 1);
    static Color dark_green = new Color(0.0664f, 0.4336f, 0.1523f, 1);
    static Color dark_red = new Color(0.4336f, 0.1172f, 0.0664f, 1);
    // flag to check if will combo, so doesn't run the combo check over and over
    boolean will_combo;

    public DialogOption (int x, int y, int width, int height){
        super(x, y, width, height);
        //DiscGame.hover_list.add(this);
        //DiscGame.click_list.add(this);
        y_offset = y + height;
        // Note - cell isn't initialized here since that's the purview of Contestant
    }

    public void drawDialogOption(SpriteBatch batch) {
    // TODO: Stubbed these out, need a full sheet for options

        if (null != cell){
            DiscGame.dialog_font.drawWrapped(batch, cell.player_dialog, x, y_offset, width);
            //setup_dialog_hover(cell);
            //setup_dialog_click(cell);
        }
    }

    private static void setup_dialog_hover(Cell cell, int x, int y, int height, int width) {
    // Highlight option if hovered over on board or directly

    }

    private static void setup_dialog_click(Cell cell) {

    }

    public void drawShapeHover(ShapeRenderer shapes) {

        if (null != cell && StateHandling.checkState(State.SelectDialog)) {
            // if backtracking, highlight red
            if (cell.consumed) { shapes.setColor(dark_red); }
            // if can combo into this option, highlight green
            else if (will_combo) { shapes.setColor(dark_green); }
            else { shapes.setColor(light_grey); }
            shapes.rect(x - 10, y - 5 + dialog_y_offset, width + 20, height + 10);
            shapes.setColor(dark_grey);
            shapes.rect(x - 5, y - 1 + dialog_y_offset, width + 10, height + 2);
        }

    }

    public void drawHover(SpriteBatch batch) {
        //DiscGame.text_font_small.draw(batch, "X: " + Gdx.input.getX() + " Y: " + Gdx.input.getY() + " Hover item: " + DiscGame.hover.toString(), x, y + 30);
        /*
        if (null != cell && StateHandling.checkState(State.SelectDialog)) {
            cell.drawHover(batch);
            //Redraw so that hover shape doesn't cover
            drawDialogOption(batch);
        }
        */
    }

    public void clickHandler() {
        /*
        if (null != cell && StateHandling.checkState(State.SelectDialog)){
            cell.clickHandler();
        }
        */
    }
}
