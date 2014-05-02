package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.discontinuous.discgame.Cell;
import org.discontinuous.discgame.Contestant;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 2/5/14.
 */
public class AbilityTarget {
    public enum targets {
        self, any_square, adjacent_square_fresh, adjacent_square_any, adjacent_square_consumed
    }

    public static void target_cell_click(Contestant contestant, Ability ability_selected, Cell cell, boolean cell_consumed) {
        switch (ability_selected.target) {
            case adjacent_square_any:
                if (contestant.is_adjacent_to(cell)) {
                    contestant.apply_effect(ability_selected.effect, cell);
                    StateHandling.set_player_offset(ability_selected.dialog);
                    StateHandling.setState(State.AbilityDialog);
                }
                break;
            case adjacent_square_fresh:
                if (contestant.is_adjacent_to(cell) && !cell_consumed) {
                    contestant.apply_effect(ability_selected.effect, cell);
                    StateHandling.set_player_offset(ability_selected.dialog);
                    StateHandling.setState(State.AbilityDialog);
                }
                break;
            case adjacent_square_consumed:
                if (contestant.is_adjacent_to(cell) && cell_consumed) {
                    contestant.apply_effect(ability_selected.effect, cell);
                    StateHandling.set_player_offset(ability_selected.dialog);
                    StateHandling.setState(State.AbilityDialog);
                }
                break;
            case any_square:
                contestant.apply_effect(ability_selected.effect, cell);
                StateHandling.set_player_offset(ability_selected.dialog);
                StateHandling.setState(State.AbilityDialog);
                break;
        }
        return;
    }

    public static void target_cell_hover(Contestant contestant, Ability ability_selected, Cell cell, boolean cell_consumed, SpriteBatch batch) {
        switch (ability_selected.target) {
            case adjacent_square_any:
                if (contestant.is_adjacent_to(cell)) { cell.enlargeCell(batch); }
                break;
            case adjacent_square_fresh:
                if (contestant.is_adjacent_to(cell) && !cell_consumed) { cell.enlargeCell(batch); }
                break;
            case adjacent_square_consumed:
                if (contestant.is_adjacent_to(cell) && cell_consumed) {cell.enlargeCell(batch); }
                break;
            case any_square:
                cell.enlargeCell(batch);
                break;
        }
        return;
    }

    public static String target_state_string(Ability ability_selected) {
        switch (ability_selected.target) {
            case adjacent_square_any:
                return "Select any adjacent square to be the target for this ability";
            case adjacent_square_fresh:
                return "Select any adjacent non-consumed square to be the target for this ability";
            case adjacent_square_consumed:
                return "Select any adjacent consumed square to be the target for this ability";
            case any_square:
                return "Select any square on the board to be the target for this ability.";
            default: return "";
        }
    }
}
