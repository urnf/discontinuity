package org.discontinuous.discgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Opponent AI for the Discontinuity Dialog Conflict Resolution game.
 * http://www.discontinuous.org/blog/?p=28
 * Creates a partial game tree of moves for JUST the opponent at the moment.
 * Refreshing myself on Java, feel like I'm trying to make it work like Ruby.
 * By Kevin Lai
 */

public class AI {
    Contestant opponent;
    Contestant player;
    // This is how many steps ahead the AI looks for pathfinding.
    // Exponentially takes more time the more iterations there are, so keep this low
    final int iterations = 8;
    Map<Cell, Float> possible_moves;
    Cell max_cell;
    float max_value;

    // Current AI weights - may change based on personalities, abilities, etc.
    // TODO: Move out and read from game variables yml
    int dp_weight = 1;
    int backtrack_dp_weight = 3;
    int conf_plus_weight = 2;
    int conf_minus_weight = 2;
    int ins_plus_weight = 2;
    int ins_minus_weight = 2;

    public AI(Contestant opponent, Contestant player) {
        // Set up the Contestant as the opponent
        this.opponent = opponent;
        this.player = player;
        possible_moves = new HashMap<Cell, Float>();
    }

    public Cell find_next_move() {
        // Reset max_value, reset adjacent hashmap
        // Resetting max_cell to null since we want exception rather than hiding errors with random legal move
        max_value = 0;
        max_cell = null;
        possible_moves.clear();

        opponent.adjacent = opponent.cell.find_adjacent_cells();
        for (Cell cell : opponent.adjacent) {
            ArrayList<Cell> previousCells = new ArrayList<Cell>();
            previousCells.add(opponent.cell);
            // If combo, add in bonus - regardless of cell consumption state
            if (opponent.combo.checkCombo(opponent.cell, cell)) {
                possible_moves.put(cell, sum_move_options(cell, previousCells) + (getBonuses(opponent.cell)));
            }
            else {
                possible_moves.put(cell, sum_move_options(cell, previousCells));
            }
        }

        // Naive implementation of getting largest value, it's O(n) on four values, fine for prototype
        // TODO: Should be sorting.
        for (Map.Entry<Cell, Float> entry: possible_moves.entrySet()) {
            // Max_cell null ensures that if all values go negative, as in
            // when AI is surrounded by bad squares, will still pick best route
            if (entry.getValue() > max_value || null == max_cell) {
                max_cell = entry.getKey();
                max_value = entry.getValue();
            }
        }
        return max_cell;
    }

    // Recursively add the possible score of the moves in a certain direction
    public float sum_move_options(Cell cell, ArrayList<Cell> previousCells) {
        float total = 0;

        // System for weighing the potential - if all the potential in a route is backloaded, as in,
        // all the good squares are x down the line, value them less than those available immediately
        // TODO: This can probably be encoded into AI personalities
        // Current default valuation coefficient is 1 * 0.8^x where x is number of steps

        float weight;
        weight = weight(previousCells.size());

        ArrayList<Cell> adjacent_cells = cell.find_adjacent_cells();
        if (previousCells.size() < iterations) {
            for (Cell adjacent : adjacent_cells) {

                // This is going to suck if multithreaded in terms of space taken
                ArrayList<Cell> new_cells = new ArrayList<Cell>(previousCells);
                new_cells.add(adjacent);

                // Factor in combos if not consumed and not previously shown up in recursion
                if (opponent.combo.checkCombo(cell, adjacent) && !previousCells.contains(adjacent)) { total += getBonuses(cell) * weight ; }

                // Recursively add the score in
                total += sum_move_options(adjacent, new_cells);
            }
        }
        else {
            for (Cell adjacent : adjacent_cells) {
                // Factor in combos if not consumed
                if (opponent.combo.checkCombo(cell, adjacent)) { total += getBonusOrPenalty(cell, previousCells.contains(adjacent)) * weight ; }
                total += getBonusOrPenalty(adjacent, previousCells.contains(adjacent)) * weight;
            }
        }
        // Attempt to normalize bad moves made by the AI by averaging both success and penalties by number of moves considered
        // This means that corner/edge positions no longer have an advantage when unconsumed or disadvantage when consumed.
        // Add the value of the cell itself to the total.
        return total/adjacent_cells.size() + getBonusOrPenalty(cell, previousCells.contains(cell)) * weight(previousCells.size() - 1);
    }

    public float weight(int steps) {
        return (1 * (float) Math.pow(0.8,steps));
    }

    public float getBonusOrPenalty(Cell cell, boolean previously_consumed) {
        // if consumed either in the previous cells list or already marked on board, no bonuses and DP penalty instead.
        if (cell.consumed || previously_consumed) {
            return -1 * DealPower.consume_penalty * backtrack_dp_weight +
                    -50 * conf_plus_weight * (1 - ((float) opponent.confidence/opponent.conf_max)) +
                    -50 * ins_plus_weight * (1 - ((float) opponent.inspiration/opponent.insp_max));
        }
        return getBonuses(cell);
    }

    public float getBonuses(Cell cell) {
        int power = 0;
        int conf_plus = 0;
        int conf_minus = 0;
        int ins_plus = 0;
        int ins_minus = 0;

        // Weight conf_plus more if confidence is low, and like so for each of the stats
        switch (cell.type) {
            case Logical:
                power = opponent.log_stats.get("power");
                conf_plus = opponent.log_stats.get("conf_plus");
                conf_minus = opponent.log_stats.get("conf_minus");
                ins_plus = opponent.log_stats.get("ins_plus");
                ins_minus = opponent.log_stats.get("ins_minus");
                break;
            case Ethical:
                power = opponent.eth_stats.get("power");
                conf_plus = opponent.eth_stats.get("conf_plus");
                conf_minus = opponent.eth_stats.get("conf_minus");
                ins_plus = opponent.eth_stats.get("ins_plus");
                ins_minus = opponent.eth_stats.get("ins_minus");
                break;
            case Interrogate:
                power = opponent.ing_stats.get("power");
                conf_plus = opponent.ing_stats.get("conf_plus");
                conf_minus = opponent.ing_stats.get("conf_minus");
                ins_plus = opponent.ing_stats.get("ins_plus");
                ins_minus = opponent.ing_stats.get("ins_minus");
                break;
            case Intimidate:
                power = opponent.inm_stats.get("power");
                conf_plus = opponent.inm_stats.get("conf_plus");
                conf_minus = opponent.inm_stats.get("conf_minus");
                ins_plus = opponent.inm_stats.get("ins_plus");
                ins_minus = opponent.inm_stats.get("ins_minus");
                break;
        }
        return (power * dp_weight +
                // Weighting: More confidence means getting confidence is less important
                conf_plus * conf_plus_weight * (1 - ((float) opponent.confidence/opponent.conf_max)) +
                // Opponent having more confidence means confidence becomes a higher priority
                conf_minus * conf_minus_weight * ((float) player.confidence/player.conf_max) +
                // Likewise for inspiration
                ins_plus * ins_plus_weight * (1 - ((float) opponent.inspiration/opponent.insp_max)) +
                ins_minus * ins_minus_weight * ((float) player.inspiration/player.insp_max));
    }
}
