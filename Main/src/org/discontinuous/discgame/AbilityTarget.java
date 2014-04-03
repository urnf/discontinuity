package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.discontinuous.discgame.StateHandling.State;

/**
 * Created by Urk on 2/5/14.
 */
public class AbilityTarget {
    public enum targets {
        self, any_square, adjacent_square_fresh, adjacent_square_any, adjacent_square_consumed
    }

    public static void target_cell_click(Cell cell) {
        switch (DiscGame.yi.ability_selected.target) {
            case adjacent_square_any:
                if (DiscGame.yi.is_adjacent_to(cell)) {
                    DiscGame.yi.ability_selected.effect.apply_effect(DiscGame.yi, cell);
                    StateHandling.set_yi_offset(DiscGame.yi.ability_selected.dialog);
                    StateHandling.currentState = State.AbilityDialog;
                }
                break;
            case adjacent_square_fresh:
                if (DiscGame.yi.is_adjacent_to(cell) && !cell.consumed) {
                    DiscGame.yi.ability_selected.effect.apply_effect(DiscGame.yi, cell);
                    StateHandling.set_yi_offset(DiscGame.yi.ability_selected.dialog);
                    StateHandling.currentState = State.AbilityDialog;
                }
                break;
            case adjacent_square_consumed:
                if (DiscGame.yi.is_adjacent_to(cell) && cell.consumed) {
                    DiscGame.yi.ability_selected.effect.apply_effect(DiscGame.yi, cell);
                    StateHandling.set_yi_offset(DiscGame.yi.ability_selected.dialog);
                    StateHandling.currentState = State.AbilityDialog;
                }
                break;
            case any_square:
                DiscGame.yi.ability_selected.effect.apply_effect(DiscGame.yi, cell);
                StateHandling.set_yi_offset(DiscGame.yi.ability_selected.dialog);
                StateHandling.currentState = State.AbilityDialog;
                break;
        }
        return;
    }

    public static void target_cell_hover(Cell cell, SpriteBatch batch) {
        switch (DiscGame.yi.ability_selected.target) {
            case adjacent_square_any:
                if (DiscGame.yi.is_adjacent_to(cell)) { cell.enlargeCell(batch); }
                break;
            case adjacent_square_fresh:
                if (DiscGame.yi.is_adjacent_to(cell) && !cell.consumed) { cell.enlargeCell(batch); }
                break;
            case adjacent_square_consumed:
                if (DiscGame.yi.is_adjacent_to(cell) && cell.consumed) {cell.enlargeCell(batch); }
                break;
            case any_square:
                cell.enlargeCell(batch);
                break;
        }
        return;
    }

    public static String target_state_string() {
        switch (DiscGame.yi.ability_selected.target) {
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
