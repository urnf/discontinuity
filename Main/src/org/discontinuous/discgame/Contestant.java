package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.discontinuous.discgame.StateHandling.State;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.abilities.AbilityEffect;
import org.discontinuous.discgame.abilities.AbilityTarget;
import org.discontinuous.discgame.states.game.GameState;

import java.util.ArrayList;
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
    public Portrait portrait;
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
        super(SympGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (board_x)),
                SympGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (board_y)),
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
            //coordinate = SympGame.DESIRED_WIDTH/2 - (Board.CELL_EDGE_SIZE * board_x/2) - 330;
        }
        else {
            coordinate = SympGame.DESIRED_WIDTH - (SCALED_ARG_TEXTURE * 4) - 70;
            //coordinate = SympGame.DESIRED_WIDTH/2 + (Board.CELL_EDGE_SIZE * board_x/2) + 270;
        }
        bars_y_coord = SympGame.DESIRED_HEIGHT/2 + 60;
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

    public void set_cell(Board board, int cell_x, int cell_y) {
        cell = board.cells[cell_x][cell_y];
    }

    public void set_opponent(Contestant contestant) {
        opponent = contestant;
    }

    public void setup_adjacent() {
        adjacent = cell.unoccupied_cells();
    }

    public void set_portrait(Portrait portrait) { this.portrait = portrait; }

    public void update_boards_won() {
        boards_won = 0;
        for (int i = 0; i < GameState.boards.length; i++) {
            for (int j = 0; j < GameState.boards[i].length; j++) {
                if (GameState.boards[i][j].current_winner() == this && GameState.boards[i][j] != GameState.current_board) {
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
        SympGame.header_font.draw(batch, "Arguments Left: " + String.valueOf(moves_left), logical_x_coord + 20, SympGame.DESIRED_HEIGHT/2 + 40);
    }

    public void draw_boards_won(SpriteBatch batch) {
        SympGame.header_font.draw(batch, "Boards Won: " + String.valueOf(boards_won), logical_x_coord + 30, SympGame.DESIRED_HEIGHT/2 + 60);
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
        SympGame.movestats_font.drawMultiLine(batch, "    Logical" + "\n" + "    Ethical" + "\n" + "Interrogate" + "\n" + " Intimidate", hover_x, hover_y);
        SympGame.movestats_font.drawMultiLine(batch, log_stats.get("power") + "\n" + eth_stats.get("power") + "\n" + ing_stats.get("power") + "\n" + inm_stats.get("power"),
                hover_x + 105, hover_y);
        SympGame.movestats_font.drawMultiLine(batch, log_stats.get("conf_plus") + "\n" + eth_stats.get("conf_plus") + "\n" + ing_stats.get("conf_plus") + "\n" + inm_stats.get("conf_plus"),
                hover_x + 160, hover_y);
        SympGame.movestats_font.drawMultiLine(batch, log_stats.get("conf_minus") + "\n" + eth_stats.get("conf_minus") + "\n" + ing_stats.get("conf_minus") + "\n" + inm_stats.get("conf_minus"),
                hover_x + 210, hover_y);
        SympGame.movestats_font.drawMultiLine(batch, log_stats.get("ins_plus") + "\n" + eth_stats.get("ins_plus") + "\n" + ing_stats.get("ins_plus") + "\n" + inm_stats.get("ins_plus"),
                hover_x + 270, hover_y);
        SympGame.movestats_font.drawMultiLine(batch, log_stats.get("ins_minus") + "\n" + eth_stats.get("ins_minus") + "\n" + ing_stats.get("ins_minus") + "\n" + inm_stats.get("ins_minus"),
                hover_x + 320, hover_y);
    }

    public void draw_bar_counters(SpriteBatch batch) {
        // Commenting out for now - may need later if unclear
        //SympGame.header_font.draw(batch, String.valueOf(confidence), confidence_x_coord + 5, 400);
        //SympGame.header_font.draw(batch, String.valueOf(inspiration), inspiration_x_coord + 5, 400);
    }

    public void update_dialog_options() {
        // Clear all cells that dialog options are associated with
        for (DialogOption option : GameState.dialog_options) {
            option.cell = null;
        }
        // lazy and shitty as fuck but I'm sick so fuckitol
        int i = 0;
        for (Cell adjacent_cell : adjacent) {
            // Note: this will go out of bounds with a non-rectangular grid (hexes, etc) since it then becomes possible
            // for this to be adjacent to more than 4 cells
            GameState.dialog_options[i].cell = adjacent_cell;
            // Update combo status
            GameState.dialog_options[i].will_combo = GameState.player.combo.checkCombo(GameState.player.cell, adjacent_cell);
            adjacent_cell.dialog_option = GameState.dialog_options[i];
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

        // if opponent is on a different board
        if (opponent.cell.board != cell.board) {
            // Update opponent positions
            opponent.cell.consume();
            opponent.cell.occupied = false;
            opponent.cell = cell.board.cells[opponent.cell.board_x][opponent.cell.board_y];
            opponent.cell.occupied = true;
            opponent.cell.board.position_board_entity(null, opponent, opponent.cell, opponent.cell.board_x, opponent.cell.board_y);
        }

        if (GameState.current_board != cell.board) {
            // Make sure we move onto the new board (if computer decides to swap)
            GameState.current_board.reset_board_positions();
            cell.board.set_current_board();
        }

        // update image position
        cell.board.position_board_entity(null, this, cell, cell.board_x, cell.board_y);
    }

    private void record_previous_stats() {
        // Record current values of stats - player and opponent before effects
        //StateHandling.previousPower = SympGame.dealpower.dp;
        StateHandling.previousPlayerLog = GameState.player.logical_bar;
        StateHandling.previousPlayerEth = GameState.player.ethical_bar;
        StateHandling.previousPlayerIng = GameState.player.interrogate_bar;
        StateHandling.previousPlayerInt = GameState.player.intimidate_bar;

        StateHandling.previousComputerLog = GameState.computer.logical_bar;
        StateHandling.previousComputerEth = GameState.computer.ethical_bar;
        StateHandling.previousComputerIng = GameState.computer.interrogate_bar;
        StateHandling.previousComputerInt = GameState.computer.intimidate_bar;
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
                minus_all_arguments(1, 1, 1, 1);
            }

            // Update both player and opponent positions
            this.cell = cell;
            opponent.update_only_position(cell.board.cells[opponent.cell.board_x][opponent.cell.board_y]);

            // Make sure we move onto the new board (if computer decides to swap)
            GameState.current_board.reset_board_positions();
            cell.board.set_current_board();
        }
        else {
            // set new cell to destination
            this.cell = cell;
            // update image position
            cell.board.position_board_entity(null, this, cell, cell.board_x, cell.board_y);

            //x = SympGame.DESIRED_WIDTH - Board.WIDTH_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_x + 1));
            //y = SympGame.DESIRED_HEIGHT - Board.HEIGHT_OFFSET - (Board.CELL_EDGE_SIZE * (cell.board_y + 1));
            //img.setPosition(x, y);
        }

        cell.occupied = true;

        update_stats(cell, false);
        update_boards_won();
        opponent.update_boards_won();

        // Trigger dialog
        StateHandling.currentSpeaker = this;
        if (StateHandling.currentSpeaker.player) {
            StateHandling.set_player_offset(GameState.player.cell.player_dialog);
            //StateHandling.set_computer_offset(SympGame.player.cell.computer_resp_dialog);
        }
        else {
            //StateHandling.set_player_offset(SympGame.computer.cell.player_resp_dialog);
            StateHandling.set_computer_offset(GameState.computer.cell.computer_dialog);
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
            //SympGame.dealpower.update(temp_stats.get("power"), player, cell.consumed);
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

            update_board_score(gain);
        }
        // lose one of each type if consumed
        else {
            minus_all_arguments(1, 1, 1, 1);
            //confidence = Math.max(confidence - 50, 0);
            //inspiration = Math.max(inspiration - 50, 0);
        }
        refresh_argument_bars();
    }

    public void update_board_score(int gain) {
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

    public void minus_all_arguments(int log_amount, int eth_amount, int ing_amount, int int_amount) {
        ethical_bar -= eth_amount;
        logical_bar -= log_amount;
        interrogate_bar -= ing_amount;
        intimidate_bar -= int_amount;

        if (ethical_bar < 0) {
            stammering = true;
            moves_left--;
            ethical_bar = 0;
        }
        if (logical_bar < 0) {
            stammering = true;
            moves_left--;
            logical_bar = 0;
        }
        if (interrogate_bar < 0) {
            stammering = true;
            moves_left--;
            interrogate_bar = 0;
        }
        if (intimidate_bar < 0) {
            stammering = true;
            moves_left--;
            intimidate_bar = 0;
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
        Ability.remove_ability_response(SympGame.hover_list, SympGame.click_list, abilities);
    }

    public void apply_effect(AbilityEffect effect, Cell new_cell) {
        // Record current values of stats - player and opponent before effects
        //record_previous_stats();

        // TODO: Ugly.  Pass arguments in as a hash, maybe break apart.
        effect.apply_effect(
                this,
                /*
                this.opponent,
                ability_selected,
                */
                new_cell.type,
                /*
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
                */
                opponent.cell,
                new_cell);

        // Deduct the ability cost
        logical_bar -= ability_selected.logical_cost;
        ethical_bar -= ability_selected.ethical_cost;
        interrogate_bar -= ability_selected.interrogate_cost;
        intimidate_bar -= ability_selected.intimidate_cost;

        //SympGame.dealpower.update(effects.get("power"), player, cell.consumed);

        //logical_bar = effects.get("player_logical_bar");
        //ethical_bar = effects.get("player_ethical_bar");
        //interrogate_bar = effects.get("player_interrogate_bar");
        //intimidate_bar = effects.get("player_intimidate_bar");

        //opponent.logical_bar = effects.get("opponent_logical_bar");
        //opponent.ethical_bar = effects.get("opponent_ethical_bar");
        //opponent.interrogate_bar = effects.get("opponent_interrogate_bar");
        //opponent.intimidate_bar = effects.get("opponent_intimidate_bar");

        refresh_argument_bars();
        opponent.refresh_argument_bars();

        /*
        confidence = effects.get("player_confidence");
        opponent.confidence = effects.get("opponent_confidence");
        inspiration = effects.get("player_inspiration");
        opponent.inspiration = effects.get("opponent_inspiration");
        */

        // Show bonus
        //StateHandling.animation_counter = 0;
        //StateHandling.animation_max = 30;

    }

    public void multiply_effect(Cell.concepts type, int magnitude) {
        switch (type) {
            case Logical:
                logical_bar = Math.min(logical_bar + magnitude, logical_max);
                update_board_score(logical_bar * magnitude);
                break;
            case Ethical:
                ethical_bar = Math.min(ethical_bar + magnitude, ethical_max);
                update_board_score(ethical_bar * magnitude);
                break;
            case Interrogate:
                interrogate_bar = Math.min(interrogate_bar + magnitude, interrogate_max);
                update_board_score(interrogate_bar * magnitude);
                break;
            case Intimidate:
                intimidate_bar = Math.min(intimidate_bar + magnitude, intimidate_max);
                update_board_score(intimidate_bar * magnitude);
                break;
                    /*
                    case Logical: stats = log_stats; break;
                    case Ethical: stats = eth_stats; break;
                    case Interrogate: stats = ing_stats; break;
                    case Intimidate: stats = inm_stats; break;
                    // Will lead to exceptions - intentional
                    default: stats = null; break;
                    */
        }
    }

    public void damage_effect(Cell.concepts type, int magnitude) {
        switch (type) {
            case Logical:
                opponent.minus_all_arguments(magnitude, 0, 0, 0);
                //opponent_logical_bar = Math.max(opponent_logical_bar - magnitude, 0);
                break;
            case Ethical:
                opponent.minus_all_arguments(0, magnitude, 0, 0);
                //opponent_ethical_bar = Math.max(opponent_ethical_bar - magnitude, 0);
                break;
            case Interrogate:
                opponent.minus_all_arguments(0, 0, magnitude, 0);
                //opponent_interrogate_bar = Math.max(opponent_interrogate_bar - magnitude, 0);
                break;
            case Intimidate:
                opponent.minus_all_arguments(0, 0, 0, magnitude);
                //opponent_intimidate_bar = Math.max(opponent_intimidate_bar - magnitude, 0);
                break;
        }
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
        update_boards_won();
        opponent.update_boards_won();
    }
}
