package org.discontinuous.discgame;

import java.util.ArrayList;

/**
 * Created by Urk on 1/28/14.
 */
public class AI {
    Contestant opponent;
    ArrayList<Cell> adjacent;

    public AI(Contestant opponent) {
        // Set up the Contestant as the opponent
    }
    // Recursively search for the next best move
    public void find_next_move(Cell cell, int iteration) {
        // It's fine to recursively re-use adjacent array since
        opponent.adjacent = opponent.cell.find_adjacent_cells();
        for (Cell adjacent : opponent.adjacent) {
            // Score each option by adding the score with the
        }
    }
}
