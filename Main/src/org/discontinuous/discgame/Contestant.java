package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.StateHandling.State;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.abilities.AbilityEffect;
import org.discontinuous.discgame.abilities.AbilityTarget;

import java.util.ArrayList;
import java.util.HashMap;
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
    int confidence_x_coord;
    int inspiration_x_coord;
    int bars_y_coord;
    boolean player;
    Contestant opponent;
    ArrayList<Cell> adjacent;
    ArrayList<Ability> abilities;
    Ability ability_selected;
    Combo combo;
    Portrait portrait;

    // TODO: pass in a hash or something.  this argument list is getting confusingly gnarly as fuck
    public Contestant(int board_x,
                      int board_y,
                      Hashtable log_stats,
                      Hashtable eth_stats,
                      Hashtable inm_stats,
                      Hashtable ing_stats,
                      int conf_max,
                      int insp_max,
                      int coordinate,
                      boolean isPlayer,
                      Cell cell) {
        super(DiscGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (board_x)),
                DiscGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (board_y)),
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
        bars_y_coord = DiscGame.DESIRED_HEIGHT/2 - 80;
        confidence_x_coord = coordinate;
        inspiration_x_coord = coordinate + 40;
        player = isPlayer;
        adjacent = new ArrayList();
        abilities = new ArrayList();
    }

    public ArrayList<Ability> get_abilities() { return abilities; }

    public void set_combo(Combo combo) {
        this.combo = combo;
    }

    public void set_portrait(Portrait portrait) { this.portrait = portrait; }

    public void draw(SpriteBatch batch) {
        img.draw(batch);
    }

    public void draw_confidence(ShapeRenderer shapes) {
        shapes.setColor(0.0547f, 0.273f, 0.129f, 1);
        shapes.rect(confidence_x_coord, bars_y_coord, 40, 320);
        shapes.setColor(0.1f, 0.69f, 0.298f, 1);
        shapes.rect(confidence_x_coord, bars_y_coord, 40, ((float) confidence / conf_max) * 320);
    }

    public void draw_inspiration(ShapeRenderer shapes) {
        shapes.setColor(0.039f, 0.18f, 0.258f, 1);
        shapes.rect(inspiration_x_coord, bars_y_coord, 40, 320);
        shapes.setColor(0.129f, 0.506f, 0.725f, 1);
        shapes.rect(inspiration_x_coord, bars_y_coord, 40, ((float) inspiration/insp_max) * 320);
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
        DiscGame.header_font.draw(batch, String.valueOf(confidence), confidence_x_coord + 5, 400);
        DiscGame.header_font.draw(batch, String.valueOf(inspiration), inspiration_x_coord + 5, 400);
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
            DiscGame.dialog_options[i].will_combo = DiscGame.player.combo.checkCombo(DiscGame.player.cell, adjacent_cell);
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
        x = DiscGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
        y = DiscGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
        img.setPosition(x, y);
    }

    public void update_position(Cell cell) {
        // Record current values of stats - player and opponent before effects
        StateHandling.previousPower = DiscGame.dealpower.dp;
        StateHandling.previousPlayerConf = DiscGame.player.confidence;
        StateHandling.previousPlayerIns = DiscGame.player.inspiration;
        StateHandling.previousComputerConf = DiscGame.computer.confidence;
        StateHandling.previousComputerIns = DiscGame.computer.inspiration;

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
        x = DiscGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
        y = DiscGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
        img.setPosition(x, y);

        // Trigger dialog
        StateHandling.currentSpeaker = this;
        if (StateHandling.currentSpeaker.player) {
            StateHandling.set_yi_offset(DiscGame.player.cell.player_dialog);
            StateHandling.set_arlene_offset(DiscGame.player.cell.computer_resp_dialog);
        }
        else {
            StateHandling.set_yi_offset(DiscGame.computer.cell.player_resp_dialog);
            StateHandling.set_arlene_offset(DiscGame.computer.cell.computer_dialog);
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

    // Called by an ability's click handler
    public void ability_click(Ability ability, AbilityEffect effect, AbilityTarget.targets target, String tooltip, String dialog) {
        ability_selected = ability;
        // If ability target is self, apply effect immediately
        if (target == AbilityTarget.targets.self) {
            // SPECIAL CASE FOR TABLEFLIP WOOOOOOO
            // TODO: Fix this terrible special case
            if (tooltip.contains("Tableflip")) { dialog = "Please be careful, I am about to flip my shit.\nRargh.\n(Yi hurls the table " + ((int)(Math.random() * 100) + 10) + " meters)"; }

            apply_effect(effect, null);
            StateHandling.set_yi_offset(dialog);
            StateHandling.setState(State.AbilityDialog);
        }
        // If ability target is not self, go to ability targeting
        else {
            StateHandling.set_yi_offset(AbilityTarget.target_state_string(ability));
            StateHandling.setState(State.AbilityTargeting);
        }
        // Remove abilities from hover and click handling
        Ability.remove_ability_response(DiscGame.hover_list, DiscGame.click_list, abilities);
    }

    public void apply_effect(AbilityEffect effect, Cell new_cell) {
        // Record current values of stats - player and opponent before effects
        StateHandling.previousPower = DiscGame.dealpower.dp;
        StateHandling.previousPlayerConf = DiscGame.player.confidence;
        StateHandling.previousPlayerIns = DiscGame.player.inspiration;
        StateHandling.previousComputerConf = DiscGame.computer.confidence;
        StateHandling.previousComputerIns = DiscGame.computer.inspiration;

        // TODO: Ugly.  Pass arguments in as a hash, maybe break apart.
        HashMap<String, Integer> effects = effect.apply_effect(
                this,
                ability_selected,
                cell.type,
                confidence,
                inspiration,
                opponent.confidence,
                opponent.inspiration,
                log_stats,
                eth_stats,
                ing_stats,
                inm_stats,
                conf_max,
                insp_max,
                opponent.cell,
                new_cell);

        DiscGame.dealpower.update(effects.get("power"), player, cell.consumed);
        confidence = effects.get("player_confidence");
        opponent.confidence = effects.get("opponent_confidence");
        inspiration = effects.get("player_inspiration");
        opponent.inspiration = effects.get("opponent_inspiration");

        // Show bonus
        StateHandling.animation_counter = 0;
        StateHandling.animation_max = 30;

    }

    public void refresh_consume_effect(Cell new_cell) {
        cell.consumed = false;

        //give combo bonus if legit combo for character and new cell is not consumed
        if (combo.checkCombo(cell, new_cell)) {
            // Get all the benefits of the previous cell except DP
            update_stats(cell, true);
            StateHandling.combo = true;
        }
        else {
            StateHandling.combo = false;
        }

        update_stats(new_cell, false);
    }
}
