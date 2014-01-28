package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 12/18/13.
 */
public class Contestant extends Entity {
    //Don't store board_x, board_y; grab from Cell
    Cell cell;
    int confidence;
    int inspiration;
    int conf_max;
    int insp_max;
    Hashtable<String, Integer> log_stats;
    Hashtable<String, Integer> eth_stats;
    Hashtable<String, Integer> inm_stats;
    Hashtable<String, Integer> ing_stats;
    int bars_coordinate;
    boolean player;
    Contestant opponent;
    ArrayList<Cell> adjacent;
    Combo combo;

    // TODO: pass in a hash or something.  this argument list is getting confusingly gnarly as fuck
    public Contestant(Combo combo, int board_x, int board_y, Hashtable log_stats, Hashtable eth_stats, Hashtable inm_stats, Hashtable ing_stats, int conf_max, int insp_max, int coordinate, boolean isPlayer, Cell cell) {
        super(DiscGame.screen_width - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (board_x)),
                DiscGame.screen_height - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (board_y)),
                Board.CELL_EDGE_SIZE,
                Board.CELL_EDGE_SIZE);
        this.cell = cell;
        this.conf_max = conf_max;
        this.insp_max = insp_max;
        confidence = conf_max;
        inspiration = 50;
        this.log_stats = log_stats;
        this.eth_stats = eth_stats;
        this.inm_stats = inm_stats;
        this.ing_stats = ing_stats;
        bars_coordinate = coordinate;
        player = isPlayer;
        adjacent = new ArrayList();
        this.combo = combo;

    }
    public void draw(SpriteBatch batch) {
        img.draw(batch);
    }

    public void draw_confidence(ShapeRenderer shapes) {
        shapes.setColor(0.0547f, 0.273f, 0.129f, 1);
        shapes.rect(bars_coordinate, 380, 40, 320);
        shapes.setColor(0.1f, 0.69f, 0.298f, 1);
        shapes.rect(bars_coordinate, 380, 40, ((float) confidence / conf_max) * 320);
    }

    public void draw_inspiration(ShapeRenderer shapes) {
        shapes.setColor(0.039f, 0.18f, 0.258f, 1);
        shapes.rect(bars_coordinate + 40, 380, 40, 320);
        shapes.setColor(0.129f, 0.506f, 0.725f, 1);
        shapes.rect(bars_coordinate + 40, 380, 40, ((float) inspiration/insp_max) * 320);
    }

    public void draw_stats(SpriteBatch batch, int hover_x, int hover_y) {
        DiscGame.movestats_font.drawMultiLine(batch, "    Logical" + "\n" + "    Ethical" + "\n" + "Interrogate" + "\n" + " Intimidate", hover_x, hover_y);
        DiscGame.movestats_font.drawMultiLine(batch, log_stats.get("power") + "\n" + eth_stats.get("power") + "\n" + ing_stats.get("power") + "\n" + inm_stats.get("power"),
                hover_x + 105, hover_y);
        DiscGame.movestats_font.drawMultiLine(batch, log_stats.get("conf_plus") + "\n" + eth_stats.get("conf_plus") + "\n" + ing_stats.get("conf_plus") + "\n" + inm_stats.get("conf_plus"),
                hover_x + 160, hover_y);
        DiscGame.movestats_font.drawMultiLine(batch, log_stats.get("conf_minus") + "\n" + eth_stats.get("conf_minus") + "\n" + ing_stats.get("conf_minus") + "\n" + inm_stats.get("conf_minus"),
                hover_x + 210, hover_y);
        DiscGame.movestats_font.drawMultiLine(batch, log_stats.get("ins_plus") + "\n" + eth_stats.get("ins_plus") + "\n" + ing_stats.get("ins_plus") + "\n" + inm_stats.get("ins_plus"),
                hover_x + 270, hover_y);
        DiscGame.movestats_font.drawMultiLine(batch, log_stats.get("ins_minus") + "\n" + eth_stats.get("ins_minus") + "\n" + ing_stats.get("ins_minus") + "\n" + inm_stats.get("ins_minus"),
                hover_x + 320, hover_y);
    }

    public void draw_bar_counters(SpriteBatch batch) {
        DiscGame.header_font.draw(batch, String.valueOf(confidence), bars_coordinate + 5, 400);
        DiscGame.header_font.draw(batch, String.valueOf(inspiration), bars_coordinate + 45, 400);
    }

    public void update_dialog_options() {
        // Clear all cells that dialog options are associated with
        for (DialogOption option : DiscGame.dialog_options) {
            option.cell = null;
        }
        // lazy and shitty as fuck but I'm sick so fuckitol
        int i = 0;
        for (Cell adjacent_cell : adjacent) {
            // Note: this will go out of bounds with a non-rectangular grid (hexes, etc) since it then becomes possible
            // for this to be adjacent to more than 4 cells
            DiscGame.dialog_options[i].cell = adjacent_cell;
            adjacent_cell.dialog_option = DiscGame.dialog_options[i];
            i++;
        }
    }

    public boolean is_adjacent_to(Cell origin) {
        return (adjacent.contains(origin)) ? true : false;
    }

    public void update_position(Cell cell) {
        // Record current values of stats - player and opponent before effects
        State.previousPower = DiscGame.dealpower.dp;
        State.previousPlayerConf = DiscGame.yi.confidence;
        State.previousPlayerIns = DiscGame.yi.inspiration;
        State.previousOpponentConf = DiscGame.arlene.confidence;
        State.previousOpponentIns = DiscGame.arlene.inspiration;

        // mark current cell as consumed
        this.cell.consumed = true;
        this.cell.occupied = false;
        this.cell.setImg(Cell.consume);

        //give combo bonus if legit combo for character and new cell is not consumed
        if (this.combo.checkCombo(this.cell.type.toString(), cell.type.toString()) && cell.consumed == false) {
            // Get all the benefits of the previous cell except DP
            update_stats(this.cell, true);
            State.combo = true;
        }
        else {
            State.combo = false;
        }

        // set new cell to destination
        this.cell = cell;
        cell.occupied = true;

        update_stats(cell, false);

        // If new cell is already consumed, lose deal power, confidence, AND inspiration
        if (cell.consumed) {
           // TODO : FILL ME IN THIS IS THE NEXT PART 1/3/14
        }

        // update image position
        x = DiscGame.screen_width - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
        y = DiscGame.screen_height - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
        img.setPosition(x, y);

        // Trigger dialog
        State.currentState = State.states.InDialog;
        State.currentSpeaker = this;

        // Show bonus
        State.animation_counter = 0;
        State.animation_max = 30;
    }

    public void update_stats(Cell cell, boolean combo) {
        // No DP gain with combos
        if (!combo) {
            // Update Deal Power Gain/Loss
            switch (cell.type) {
                case Logical: DiscGame.dealpower.update(log_stats.get("power"), player, cell.consumed); break;
                case Ethical: DiscGame.dealpower.update(eth_stats.get("power"), player, cell.consumed); break;
                case Interrogate: DiscGame.dealpower.update(ing_stats.get("power"), player, cell.consumed); break;
                case Intimidate: DiscGame.dealpower.update(inm_stats.get("power"), player, cell.consumed); break;
            }
        }
        // Update Confidence Gain/Loss
        switch (cell.type) {
            case Logical:
                confidence = Math.min(confidence + log_stats.get("conf_plus"), conf_max);
                opponent.confidence = Math.max(opponent.confidence - log_stats.get("conf_minus"), 0);
                break;
            case Ethical:
                confidence = Math.min(confidence + eth_stats.get("conf_plus"), conf_max);
                opponent.confidence = Math.max(opponent.confidence - eth_stats.get("conf_minus"), 0);
                break;
            case Interrogate:
                confidence = Math.min(confidence + ing_stats.get("conf_plus"), conf_max);
                opponent.confidence = Math.max(opponent.confidence - ing_stats.get("conf_minus"), 0);
                break;
            case Intimidate:
                confidence = Math.min(confidence + inm_stats.get("conf_plus"), conf_max);
                opponent.confidence = Math.max(opponent.confidence - inm_stats.get("conf_minus"), 0);
                break;
        }

        // Update Inspiration Gain/Loss
        switch (cell.type) {
            case Logical:
                inspiration = Math.min(inspiration + log_stats.get("ins_plus"), insp_max);
                opponent.inspiration = Math.max(opponent.inspiration - log_stats.get("ins_minus"), 0);
                break;
            case Ethical:
                inspiration = Math.min(inspiration + eth_stats.get("ins_plus"), insp_max);
                opponent.inspiration = Math.max(opponent.inspiration - eth_stats.get("ins_minus"), 0);
                break;
            case Interrogate:
                inspiration = Math.min(inspiration + ing_stats.get("ins_plus"), insp_max);
                opponent.inspiration = Math.max(opponent.inspiration - ing_stats.get("ins_minus"), 0);
                break;
            case Intimidate:
                inspiration = Math.min(inspiration + inm_stats.get("ins_plus"), insp_max);
                opponent.inspiration = Math.max(opponent.inspiration - inm_stats.get("ins_minus"), 0);
                break;
        }
    }

    public void initialize_yi() {

    }

    public void initialize_arlene() {

    }

}
