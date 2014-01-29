package org.discontinuous.discgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Urk on 1/28/14.
 */

// TODO: Calculations are done independently of player action, factor in player action at some point.
public class AI {
    Contestant opponent;
    Contestant player;
    // This is how many steps ahead the AI looks for pathfinding.
    // Exponentially takes more time the more iterations there are, so keep this low
    // int total is also instantiated each recursion, so space usage will also start adding up.
    final int iterations = 8;
    Map<Cell, Integer> possible_moves;
    Cell max_cell;
    int max_value;

    // Current AI weights - may change based on personalities, abilities, etc.
    int dp_weight = 1;
    int conf_plus_weight = 1;
    int conf_minus_weight = 2;
    int ins_plus_weight = 2;
    int ins_minus_weight = 2;

    public AI(Contestant opponent, Contestant player) {
        // Set up the Contestant as the opponent
        this.opponent = opponent;
        this.player = player;
        possible_moves = new HashMap<Cell, Integer>();
    }

    public Cell find_next_move() {
        // Reset max_value, reset adjacent hashmap
        // Set max_cell to a legit value later on since we want a fallback to a legit move rather than an exception
        max_value = 0;
        possible_moves.clear();

        opponent.adjacent = opponent.cell.find_adjacent_cells();
        max_cell = opponent.adjacent.get(0);
        for (Cell cell : opponent.adjacent) {
            ArrayList<Cell> previousCells = new ArrayList<Cell>();
            previousCells.add(opponent.cell);
            // if combo, add in bonus
            if (opponent.combo.checkCombo(opponent.cell, cell)) {
                possible_moves.put(cell, sum_move_options(cell, previousCells) + (getBonuses(cell, false)));
            }
            else {
                possible_moves.put(cell, sum_move_options(cell, previousCells));
            }
        }

        // Fuckit, naive implementation of getting largest value.  Should really be sorting.
        // It's O(n) but on four values, nobuddygiffafuk
        for (Map.Entry<Cell, Integer> entry: possible_moves.entrySet()) {
            if (entry.getValue() > max_value) {
                max_cell = entry.getKey();
                max_value = entry.getValue();
            }
        }
        return max_cell;
    }

    // Recursively add the possible score of the moves in a certain direction
    public int sum_move_options(Cell cell, ArrayList<Cell> previousCells) {
        int total = 0;
        if (previousCells.size() - 1 == iterations) {
            for (Cell adjacent : cell.find_adjacent_cells()) {
                // System for weighing the potential - if all the potential in a route is backloaded, as in,
                // all the good squares are x down the line, value them less than those available immediately
                // - This can probably be encoded into AI personalities
                // Current default coefficient is 1 - x/10 where x is number of steps

                // This is going to suck if multithreaded in terms of space taken
                ArrayList<Cell> new_cells = new ArrayList<Cell>(previousCells);
                new_cells.add(adjacent);
                float weight = 0;
                weight = weight(previousCells.size());

                // Factor in combos
                if (opponent.combo.checkCombo(cell, adjacent)) { total += getBonuses(cell, previousCells.contains(adjacent)) * weight ; }

                // Add the score in
                total += sum_move_options(adjacent, new_cells) * weight;
            }
        }
        else {
            for (Cell adjacent : cell.find_adjacent_cells()) {
                total += getBonuses(cell, previousCells.contains(adjacent)) * weight(previousCells.size());
            }
        }
        return total;
    }

    public float weight(int steps) {
        return (1 - (steps/10));
    }

    public int getBonuses(Cell cell, boolean previously_consumed) {
        // Weight conf_plus more if confidence is low, and like so for each of the stats
        // if consumed either in the previous cells list or already marked on board, no bonuses and DP penalty instead.
        switch (cell.type) {
            case Logical:
                if (cell.consumed || previously_consumed) {
                    return DealPower.consume_penalty_coeff * dp_weight * opponent.log_stats.get("power");
                }
                else {
                    return (opponent.log_stats.get("power") * dp_weight +
                    // I am probably messing up Java float/int rounding here, but it doesn't matter given precision.
                    // More confidence means getting confidence is less important
                    opponent.log_stats.get("conf_plus") * conf_plus_weight * (1 - (opponent.confidence/opponent.conf_max)) +
                    // Opponent having more confidence means confidence becomes a higher priority
                    opponent.log_stats.get("conf_minus") * conf_minus_weight * (player.confidence/player.conf_max) +
                    // Likewise for inspiration
                    opponent.log_stats.get("ins_plus") * ins_plus_weight * (1 - (opponent.inspiration/opponent.insp_max)) +
                    opponent.log_stats.get("ins_minus") * ins_minus_weight * (player.inspiration/player.insp_max));
                }
            case Ethical:
                if (cell.consumed || previously_consumed) {
                    return DealPower.consume_penalty_coeff * opponent.eth_stats.get("power");
                }
                else {
                    return (opponent.eth_stats.get("power") * dp_weight +
                    opponent.eth_stats.get("conf_plus") * conf_plus_weight * (1 - (opponent.confidence/opponent.conf_max)) +
                    opponent.eth_stats.get("conf_minus") * conf_minus_weight * (player.confidence/player.conf_max) +
                    opponent.eth_stats.get("ins_plus") * ins_plus_weight * (1 - (opponent.inspiration/opponent.insp_max)) +
                    opponent.eth_stats.get("ins_minus") * ins_minus_weight * (player.inspiration/player.insp_max));
                }
            case Interrogate:
                if (cell.consumed || previously_consumed) {
                    return DealPower.consume_penalty_coeff * opponent.ing_stats.get("power");
                }
                else {
                    return (opponent.ing_stats.get("power") * dp_weight +
                    opponent.ing_stats.get("conf_plus") * conf_plus_weight * (1 - (opponent.confidence/opponent.conf_max)) +
                    opponent.ing_stats.get("conf_minus") * conf_minus_weight * (player.confidence/player.conf_max) +
                    opponent.ing_stats.get("ins_plus") * ins_plus_weight * (1 - (opponent.inspiration/opponent.insp_max)) +
                    opponent.ing_stats.get("ins_minus") * ins_minus_weight * (player.inspiration/player.insp_max));
                }
            case Intimidate:
                if (cell.consumed || previously_consumed) {
                    return DealPower.consume_penalty_coeff * opponent.inm_stats.get("power");
                }
                else {
                    return (opponent.inm_stats.get("power") * dp_weight +
                    opponent.inm_stats.get("conf_plus") * conf_plus_weight * (1 - (opponent.confidence/opponent.conf_max)) +
                    opponent.inm_stats.get("conf_minus") * conf_minus_weight * (player.confidence/player.conf_max) +
                    opponent.inm_stats.get("ins_plus") * ins_plus_weight * (1 - (opponent.inspiration/opponent.insp_max)) +
                    opponent.inm_stats.get("ins_minus") * ins_minus_weight * (player.inspiration/player.insp_max));
                }
        }
        // TODO: Maybe return an exception or something, should never reach return 0
        return 0;
    }
}
