package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
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
    static final float SCALE = 0.75f;
    static final int SCALED_ARG_TEXTURE = (int) (Board.TEXTURE_EDGE * SCALE);

    //Don't store board_x, board_y; grab from Cell
    Cell cell;

    int logical_bar;
    int ethical_bar;
    int intimidate_bar;
    int interrogate_bar;

    int logical_max;
    int ethical_max;
    int intimidate_max;
    int interrogate_max;

    int boards_won;
    int moves_left = 40;

    Hashtable<String, Integer> log_stats;
    Hashtable<String, Integer> eth_stats;
    Hashtable<String, Integer> inm_stats;
    Hashtable<String, Integer> ing_stats;
    int logical_x_coord;
    int ethical_x_coord;
    int interrogate_x_coord;
    int intimidate_x_coord;
    int bars_y_coord;
    boolean player;
    Contestant opponent;
    ArrayList<Cell> adjacent;
    ArrayList<Ability> abilities;
    Ability ability_selected;
    Combo combo;
    Portrait portrait;
    ArrayList<Sprite> logical_imgs;
    ArrayList<Sprite> ethical_imgs;
    ArrayList<Sprite> interrogate_imgs;
    ArrayList<Sprite> intimidate_imgs;

    // Tracking whether this player is in stammer state.
    boolean stammering;

    // TODO: pass in a hash or something.  this argument list is getting confusingly gnarly as fuck
    public Contestant(int board_x,
                      int board_y,
                      Hashtable log_stats,
                      Hashtable eth_stats,
                      Hashtable inm_stats,
                      Hashtable ing_stats,
                      int logical_max,
                      int ethical_max,
                      int intimidate_max,
                      int interrogate_max,
                      boolean isPlayer) {
        super(DiscGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (board_x)),
                DiscGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (board_y)),
                Board.TEXTURE_EDGE,
                Board.TEXTURE_EDGE);

        this.logical_max = logical_max;
        this.ethical_max = ethical_max;
        this.intimidate_max = intimidate_max;
        this.interrogate_max = interrogate_max;

        // Starting at one of each resource
        this.logical_bar = 1;
        this.ethical_bar = 1;
        this.intimidate_bar = 1;
        this.interrogate_bar = 1;

        this.log_stats = log_stats;
        this.eth_stats = eth_stats;
        this.inm_stats = inm_stats;
        this.ing_stats = ing_stats;
        int coordinate;
        if (isPlayer) {
            coordinate = 50;
            //coordinate = DiscGame.DESIRED_WIDTH/2 - (Board.CELL_EDGE_SIZE * board_x/2) - 330;
        }
        else {
            coordinate = DiscGame.DESIRED_WIDTH - (SCALED_ARG_TEXTURE * 4) - 70;
            //coordinate = DiscGame.DESIRED_WIDTH/2 + (Board.CELL_EDGE_SIZE * board_x/2) + 270;
        }
        bars_y_coord = DiscGame.DESIRED_HEIGHT/2 + 60;
        logical_x_coord = coordinate;
        ethical_x_coord = coordinate + SCALED_ARG_TEXTURE;
        interrogate_x_coord = coordinate + SCALED_ARG_TEXTURE * 2;
        intimidate_x_coord = coordinate + SCALED_ARG_TEXTURE * 3;
        player = isPlayer;
        adjacent = new ArrayList();
        abilities = new ArrayList();
        logical_imgs = new ArrayList();
        ethical_imgs = new ArrayList();
        interrogate_imgs = new ArrayList();
        intimidate_imgs = new ArrayList();

        // Set up arrays of each argument type to draw
        for (int i = 0; i < logical_max; i++) {
            Sprite arg_img = setup_argument_sprite(i);
            arg_img.setPosition(logical_x_coord, bars_y_coord + SCALED_ARG_TEXTURE * i);
            if (i < logical_bar) {
                arg_img.setColor(Colors.ColorMap.get("logical_color"));
            }
            else {
                arg_img.setColor(Colors.ColorMap.get("logical_extra_fade"));
            }
            logical_imgs.add(arg_img);
        }
        for (int i = 0; i < ethical_max; i++) {
            Sprite arg_img = setup_argument_sprite(i);
            arg_img.setPosition(ethical_x_coord, bars_y_coord + SCALED_ARG_TEXTURE * i);
            if (i < ethical_bar) {
                arg_img.setColor(Colors.ColorMap.get("ethical_color"));
            }
            else {
                arg_img.setColor(Colors.ColorMap.get("ethical_extra_fade"));
            }
            ethical_imgs.add(arg_img);

        }
        for (int i = 0; i < interrogate_max; i++) {
            Sprite arg_img = setup_argument_sprite(i);
            arg_img.setPosition(interrogate_x_coord, bars_y_coord + SCALED_ARG_TEXTURE * i);
            if (i < interrogate_bar) {
                arg_img.setColor(Colors.ColorMap.get("interrogate_color"));
            }
            else {
                arg_img.setColor(Colors.ColorMap.get("interrogate_extra_fade"));
            }
            interrogate_imgs.add(arg_img);

        }
        for (int i = 0; i < intimidate_max; i++) {
            Sprite arg_img = setup_argument_sprite(i);
            arg_img.setPosition(intimidate_x_coord, bars_y_coord + SCALED_ARG_TEXTURE * i);
            if (i < intimidate_bar) {
                arg_img.setColor(Colors.ColorMap.get("intimidate_color"));
            }
            else {
                arg_img.setColor(Colors.ColorMap.get("intimidate_extra_fade"));
            }
            intimidate_imgs.add(arg_img);

        }
    }

    public Sprite setup_argument_sprite(int i) {
        Sprite arg_img = new Sprite(Cell.tintable, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
        arg_img.setScale(SCALE);
        return arg_img;
    }

    public ArrayList<Ability> get_abilities() { return abilities; }

    public void set_combo(Combo combo) {
        this.combo = combo;
    }

    public void set_portrait(Portrait portrait) { this.portrait = portrait; }

    public void update_boards_won() {
        boards_won = 0;
        for (int i = 0; i < DiscGame.boards.length; i++) {
            for (int j = 0; j < DiscGame.boards[i].length; j++) {
                if (DiscGame.boards[i][j].current_winner() == this && DiscGame.boards[i][j] != DiscGame.current_board) {
                    boards_won++;
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (cell.board.relative_to_current != Board.Direction.UPPER_LEFT ||
                cell.board.relative_to_current != Board.Direction.LOWER_LEFT ||
                cell.board.relative_to_current != Board.Direction.UPPER_RIGHT ||
                cell.board.relative_to_current != Board.Direction.LOWER_RIGHT) {
            animate();
            img.draw(batch);
        }
    }

    public void draw_moves_left(SpriteBatch batch) {
        DiscGame.header_font.draw(batch, "Arguments Left: " + String.valueOf(moves_left), logical_x_coord + 20, DiscGame.screen_height/2 + 40);
    }

    public void draw_boards_won(SpriteBatch batch) {
        DiscGame.header_font.draw(batch, "Boards Won: " + String.valueOf(boards_won), logical_x_coord + 30, DiscGame.screen_height/2 + 60);
    }

    public void draw_logical(SpriteBatch batch) {
        for (int i = 0; i < logical_max; i++) {
            logical_imgs.get(i).draw(batch);
        }
    }

    public void draw_ethical(SpriteBatch batch) {
        for (int i = 0; i < ethical_max; i++) {
            ethical_imgs.get(i).draw(batch);
        }
    }

    public void draw_interrogate(SpriteBatch batch) {
        for (int i = 0; i < interrogate_max; i++) {
            interrogate_imgs.get(i).draw(batch);
        }
    }

    public void draw_intimidate(SpriteBatch batch) {
        for (int i = 0; i < intimidate_max; i++) {
            intimidate_imgs.get(i).draw(batch);
        }
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
        // Commenting out for now - may need later if unclear
        //DiscGame.header_font.draw(batch, String.valueOf(confidence), confidence_x_coord + 5, 400);
        //DiscGame.header_font.draw(batch, String.valueOf(inspiration), inspiration_x_coord + 5, 400);
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

    private void record_previous_stats() {
        // Record current values of stats - player and opponent before effects
        //StateHandling.previousPower = DiscGame.dealpower.dp;
        StateHandling.previousPlayerLog = DiscGame.player.logical_bar;
        StateHandling.previousPlayerEth = DiscGame.player.ethical_bar;
        StateHandling.previousPlayerIng = DiscGame.player.interrogate_bar;
        StateHandling.previousPlayerInt = DiscGame.player.intimidate_bar;

        StateHandling.previousComputerLog = DiscGame.computer.logical_bar;
        StateHandling.previousComputerEth = DiscGame.computer.ethical_bar;
        StateHandling.previousComputerIng = DiscGame.computer.interrogate_bar;
        StateHandling.previousComputerInt = DiscGame.computer.intimidate_bar;
    }

    public void update_position(Cell cell) {
        moves_left--;

        record_previous_stats();

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

        // if moving to a new board, enlarge icons and bring opponent along
        if (this.cell.board != cell.board) {
            // If current board is not completely consumed, institute a -1 to all penalty
            if (!this.cell.board.is_consumed()) {
                minus_all_arguments(1);
            }

            // Update both player and opponent positions
            opponent.update_only_position(cell.board.cells[opponent.cell.board_x][opponent.cell.board_y]);
            this.cell = cell;
            // Make sure we move onto the new board (if computer decides to swap)
            DiscGame.current_board.reset_board_positions();
            cell.board.set_current_board();
        }
        else {
            // set new cell to destination
            this.cell = cell;
            // update image position
            cell.board.position_board_entity(null, this, cell, cell.board_x, cell.board_y);

            //x = DiscGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
            //y = DiscGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
            //img.setPosition(x, y);
        }

        cell.occupied = true;

        update_stats(cell, false);
        update_boards_won();
        opponent.update_boards_won();

        // Trigger dialog
        StateHandling.currentSpeaker = this;
        if (StateHandling.currentSpeaker.player) {
            StateHandling.set_player_offset(DiscGame.player.cell.player_dialog);
            //StateHandling.set_computer_offset(DiscGame.player.cell.computer_resp_dialog);
        }
        else {
            //StateHandling.set_player_offset(DiscGame.computer.cell.player_resp_dialog);
            StateHandling.set_computer_offset(DiscGame.computer.cell.computer_dialog);
        }
        StateHandling.currentState = State.InDialog;

        // End game this if we're at 5 boards won for a player or 0 moves left
        if (boards_won >= 5 || moves_left == 0) {
            // TODO: PROPERLY End game this
            StateHandling.setState(State.PostGameSelect);
        }

        // Show bonus
        StateHandling.animation_counter = 0;
        StateHandling.animation_max = 30;
    }

    public void update_abilities() {
        for (Ability ability : abilities) {
            ability.update_usability(logical_bar, ethical_bar, interrogate_bar, intimidate_bar);
        }
    }

    public void update_stats(Cell cell, boolean combo) {
        /*
        Hashtable<String, Integer> temp_stats;
        switch (cell.type) {
            case Logical: temp_stats = log_stats; break;
            case Ethical: temp_stats = eth_stats; break;
            case Interrogate: temp_stats = ing_stats; break;
            case Intimidate: temp_stats = inm_stats; break;
            // Will lead to exceptions - intentional
            default: temp_stats = null; break;
        }
        */

        // No DP gain with combos
        if (!combo) {
            // Update Deal Power Gain/Loss
            // THIS IS NO LONGER RELEVANT AS PART OF SYMPOSIUM
            //DiscGame.dealpower.update(temp_stats.get("power"), player, cell.consumed);
        }
        // TODO: Update this so that combos can continue past one
        // No bonuses for a consumed cell, but ok if it's a combo
        // Can combo over consumed arguments, the logic being you're taking a penalty to set up for new arguments
        if ((!cell.consumed) || (cell.consumed && combo))
        {
            switch(cell.type) {
                case Logical:
                    logical_bar = Math.min(logical_bar + 1, logical_max);
                    break;
                case Ethical:
                    ethical_bar = Math.min(ethical_bar + 1, ethical_max);
                    break;
                case Interrogate:
                    interrogate_bar = Math.min(interrogate_bar + 1, interrogate_max);
                    break;
                case Intimidate:
                    intimidate_bar = Math.min(intimidate_bar + 1, intimidate_max);
                    break;
            }
            /*
            confidence = Math.min(confidence + temp_stats.get("conf_plus"), conf_max);
            opponent.confidence = Math.max(opponent.confidence - temp_stats.get("conf_minus"), 0);
            inspiration = Math.min(inspiration + temp_stats.get("ins_plus"), insp_max);
            opponent.inspiration = Math.max(opponent.inspiration - temp_stats.get("ins_minus"), 0);
            */

            int gain = 0;
            switch (cell.type) {
                case Logical: gain = logical_bar; break;
                case Ethical: gain = ethical_bar; break;
                case Interrogate: gain = interrogate_bar; break;
                case Intimidate: gain = intimidate_bar; break;
            }

            // Update score on the board
            if (player) {
                // Round down then add gain in case it's been tiebreak boosted
                cell.board.player_score = (int) cell.board.player_score + gain;
                // Tiebreaking, opponent got there first so add 0.5 to opponent so that it evaluates to larger than this player
                if (cell.board.player_score == cell.board.computer_score) {
                    cell.board.computer_score += 0.5;
                }
                cell.board.player_score_string = "~ " + String.valueOf((int) cell.board.player_score) + " ~";
            }
            else {
                cell.board.computer_score = (int) cell.board.computer_score + gain;
                if (cell.board.computer_score == cell.board.player_score) {
                    cell.board.player_score += 0.5;
                }
                cell.board.computer_score_string = "~ " + String.valueOf((int) cell.board.computer_score) + " ~";
            }
        }
        // lose one of each type if consumed
        else {
            minus_all_arguments(1);
            //confidence = Math.max(confidence - 50, 0);
            //inspiration = Math.max(inspiration - 50, 0);
        }
        refresh_argument_bars();
    }

    private void minus_all_arguments(int amount) {
        if (ethical_bar > 0) {
            ethical_bar -= amount;
        }
        else {
            stammering = true;
            moves_left--;
        }
        if (logical_bar > 0) {
            logical_bar -= amount;
        }
        else {
            stammering = true;
            moves_left--;
        }
        if (interrogate_bar > 0) {
            interrogate_bar -= amount;
        }
        else {
            stammering = true;
            moves_left--;
        }
        if (intimidate_bar > 0) {
            intimidate_bar -= amount;
        }
        else {
            stammering = true;
            moves_left--;
        }
    }

    private void refresh_argument_bars() {
        for (int i = 0; i < logical_max; i++) {
            if (i < logical_bar) {
                logical_imgs.get(i).setColor(Colors.ColorMap.get("logical_color"));
            }
            else {
                logical_imgs.get(i).setColor(Colors.ColorMap.get("logical_extra_fade"));
            }
        }
        for (int i = 0; i < ethical_max; i++) {
            if (i < ethical_bar) {
                ethical_imgs.get(i).setColor(Colors.ColorMap.get("ethical_color"));
            }
            else {
                ethical_imgs.get(i).setColor(Colors.ColorMap.get("ethical_extra_fade"));
            }
        }
        for (int i = 0; i < interrogate_max; i++) {
            if (i < interrogate_bar) {
                interrogate_imgs.get(i).setColor(Colors.ColorMap.get("interrogate_color"));
            }
            else {
                interrogate_imgs.get(i).setColor(Colors.ColorMap.get("interrogate_extra_fade"));
            }
        }
        for (int i = 0; i < intimidate_max; i++) {
            if (i < intimidate_bar) {
                intimidate_imgs.get(i).setColor(Colors.ColorMap.get("intimidate_color"));
            }
            else {
                intimidate_imgs.get(i).setColor(Colors.ColorMap.get("intimidate_extra_fade"));
            }

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
            StateHandling.set_player_offset(dialog);
            StateHandling.setState(State.AbilityDialog);
        }
        // If ability target is not self, go to ability targeting
        else {
            StateHandling.set_player_offset(AbilityTarget.target_state_string(ability));
            StateHandling.setState(State.AbilityTargeting);
        }
        // Remove abilities from hover and click handling
        Ability.remove_ability_response(DiscGame.hover_list, DiscGame.click_list, abilities);
    }

    public void apply_effect(AbilityEffect effect, Cell new_cell) {
        // Record current values of stats - player and opponent before effects
        record_previous_stats();

        // TODO: Ugly.  Pass arguments in as a hash, maybe break apart.
        HashMap<String, Integer> effects = effect.apply_effect(
                this,
                ability_selected,
                cell.type,
                logical_bar,
                ethical_bar,
                interrogate_bar,
                intimidate_bar,
                opponent.logical_bar,
                opponent.ethical_bar,
                opponent.interrogate_bar,
                opponent.intimidate_bar,
                //log_stats,
                //eth_stats,
                //ing_stats,
                //inm_stats,
                logical_max,
                ethical_max,
                interrogate_max,
                intimidate_max,
                opponent.cell,
                new_cell);

        //DiscGame.dealpower.update(effects.get("power"), player, cell.consumed);

        logical_bar = effects.get("player_logical_bar");
        ethical_bar = effects.get("player_ethical_bar");
        interrogate_bar = effects.get("player_interrogate_bar");
        intimidate_bar = effects.get("player_intimidate_bar");

        opponent.logical_bar = effects.get("opponent_logical_bar");
        opponent.ethical_bar = effects.get("opponent_ethical_bar");
        opponent.interrogate_bar = effects.get("opponent_interrogate_bar");
        opponent.intimidate_bar = effects.get("opponent_intimidate_bar");

        /*
        confidence = effects.get("player_confidence");
        opponent.confidence = effects.get("opponent_confidence");
        inspiration = effects.get("player_inspiration");
        opponent.inspiration = effects.get("opponent_inspiration");
        */

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
