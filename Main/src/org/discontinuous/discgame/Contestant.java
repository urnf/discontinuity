package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.StateHandling.State;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 12/18/13.
 */
public class Contestant extends Entity {
    //Don't store board_x, board_y; grab from Cell
    public Cell cell;
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
    ArrayList<Ability> abilities;
    Ability ability_selected;
    Combo combo;

    // TODO: pass in a hash or something.  this argument list is getting confusingly gnarly as fuck
    public Contestant(Combo combo, int board_x, int board_y, Hashtable log_stats, Hashtable eth_stats, Hashtable inm_stats, Hashtable ing_stats, int conf_max, int insp_max, int coordinate, boolean isPlayer, Cell cell) {
        super(DiscGame.screen_width - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (board_x)),
                DiscGame.screen_height - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (board_y)),
                Board.TEXTURE_EDGE,
                Board.TEXTURE_EDGE);
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
        abilities = new ArrayList();
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
            // Update combo status
            DiscGame.dialog_options[i].will_combo = DiscGame.yi.combo.checkCombo(DiscGame.yi.cell, adjacent_cell);
            adjacent_cell.dialog_option = DiscGame.dialog_options[i];
            i++;
        }
    }

    public boolean is_adjacent_to(Cell origin) {
        return cell.unoccupied_cells().contains(origin);
    }

    public void update_only_position(Cell cell) {
        // mark current cell as consumed
        this.cell.consume();
        this.cell.occupied = false;

        // set new cell to destination
        this.cell = cell;
        cell.occupied = true;

        // update image position
        x = DiscGame.screen_width - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
        y = DiscGame.screen_height - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
        img.setPosition(x, y);
    }

    public void update_position(Cell cell) {
        // Record current values of stats - player and opponent before effects
        StateHandling.previousPower = DiscGame.dealpower.dp;
        StateHandling.previousPlayerConf = DiscGame.yi.confidence;
        StateHandling.previousPlayerIns = DiscGame.yi.inspiration;
        StateHandling.previousOpponentConf = DiscGame.arlene.confidence;
        StateHandling.previousOpponentIns = DiscGame.arlene.inspiration;

        this.cell.consume();
        this.cell.occupied = false;

        //give combo bonus if legit combo for character and new cell is not consumed
        if (this.combo.checkCombo(this.cell, cell) && cell.consumed == false) {
            // Get all the benefits of the previous cell except DP
            update_stats(this.cell, true);
            StateHandling.combo = true;
        }
        else {
            StateHandling.combo = false;
        }

        // set new cell to destination
        this.cell = cell;
        cell.occupied = true;

        update_stats(cell, false);

        // update image position
        x = DiscGame.screen_width - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
        y = DiscGame.screen_height - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
        img.setPosition(x, y);

        // Trigger dialog
        StateHandling.currentSpeaker = this;
        if (StateHandling.currentSpeaker.player) {
            StateHandling.set_yi_offset(DiscGame.yi.cell.yi_dialog);
            StateHandling.set_arlene_offset(DiscGame.yi.cell.arlene_resp_dialog);
        }
        else {
            StateHandling.set_yi_offset(DiscGame.arlene.cell.yi_resp_dialog);
            StateHandling.set_arlene_offset(DiscGame.arlene.cell.arlene_dialog);
        }
        StateHandling.currentState = State.InDialog;


        // Show bonus
        StateHandling.animation_counter = 0;
        StateHandling.animation_max = 30;
    }

    public void update_abilities() {
        for (Ability ability : abilities) {
            ability.update_usability(inspiration);
        }
    }

    public void update_stats(Cell cell, boolean combo) {
        Hashtable<String, Integer> temp_stats;
        switch (cell.type) {
            case Logical: temp_stats = log_stats; break;
            case Ethical: temp_stats = eth_stats; break;
            case Interrogate: temp_stats = ing_stats; break;
            case Intimidate: temp_stats = inm_stats; break;
            // Will lead to exceptions - intentional
            default: temp_stats = null; break;
        }

        // No DP gain with combos
        if (!combo) {
            // Update Deal Power Gain/Loss
            DiscGame.dealpower.update(temp_stats.get("power"), player, cell.consumed);
        }
        // No bonuses for a consumed cell, but ok if it's a combo
        // Can combo over consumed arguments, the logic being you're taking a penalty to set up for new arguments
        if ((!cell.consumed) || (cell.consumed && combo))
        {
            confidence = Math.min(confidence + temp_stats.get("conf_plus"), conf_max);
            opponent.confidence = Math.max(opponent.confidence - temp_stats.get("conf_minus"), 0);
            inspiration = Math.min(inspiration + temp_stats.get("ins_plus"), insp_max);
            opponent.inspiration = Math.max(opponent.inspiration - temp_stats.get("ins_minus"), 0);
        }
        // lose a load of confidence and inspiration instead if consumed
        else {
            confidence = Math.max(confidence - 50, 0);
            inspiration = Math.max(inspiration - 50, 0);
        }
    }
}
