package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

/**
 * Created by Urk on 12/17/13.
 */
public class Board {
    int width;
    int height;
    int screen_width;
    int screen_height;
    Cell[][] cells;

    static final int WIDTH_OFFSET = 270;
    static final int HEIGHT_OFFSET = 5;
    static final int CELL_EDGE_SIZE = 64;

    int[] player_position;
    int[] opponent_position;

    public Board (int board_height, int board_width){
        width = board_width;
        height = board_height;
        screen_width = DiscGame.screen_width;
        screen_height = DiscGame.screen_height;

        // Multiply width by height
        int cell_count = width * height;

        // Create our array of enums (going to use ints for now), shuffling it next
        // Not a great solution, but alternative was allocating 4 temporary arrays and summing
        int[] concepts = new int[cell_count];

        // First quarter is going to get "1" indicating Logical arguments
        // Next quarter gets "2" indicating Ethical arguments and so forth
        for (int i = 0; i < 4; i++) {
            for (int j = ((cell_count - (cell_count % 4))/4 * i); j < ((cell_count - (cell_count % 4))/4 * (i + 1)); j++) {
                concepts[j] = i + 1;
            }
        }
        // Assign 1, 2, 3 to the leftover elements in the array
        for (int i = 0; i < cell_count % 4; i++) {
            concepts[i + (cell_count - (cell_count % 4))] = i + 1;
        }

        // Fisher-Yates shuffle of this array
        for (int i = concepts.length - 1; i > 0; i--) {
            // Draw a random element less than
            int j = (int) Math.round(Math.random() * i);
            // Swap these two elements
            int temp = concepts[i];
            concepts[i] = concepts[j];
            concepts[j] = temp;
        }

        // Populate our grid (array of arrays) from this array and create cells
        int k = 0;
        cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(concepts[k], false, false, i, j,
                        screen_width - WIDTH_OFFSET - (CELL_EDGE_SIZE * (i + 1)),
                        screen_height - HEIGHT_OFFSET - (CELL_EDGE_SIZE * (j + 1)),
                        CELL_EDGE_SIZE);
                k++;
            }
        }
    }

    public void draw(SpriteBatch batch, int board_width, int board_height) {
        // Draw the space of ideas
        for (int i = 0; i < board_width; i++) {
            for (int j = 0; j < board_height; j++) {
                cells[i][ j].draw(batch);
            }
        }
    }

    public void move_arlene() {
        /*
        // TODO : Fake AI for now - move Arlene in a random direction
        // TODO: Also doesn't respect rule of not moving onto opponent's current argument
        // Set up arrays of directions in this order: Move Left, Move Right, Move Up, Move Down
        int arlene_x = DiscGame.arlene.cell.board_x;
        int arlene_y = DiscGame.arlene.cell.board_y;
        int[][] arlene_moves;
        int[] move_left = new int[]{arlene_x + 1, arlene_y};
        int[] move_right = new int[]{arlene_x - 1, arlene_y};
        int[] move_up = new int[]{arlene_x, arlene_y - 1};
        int[] move_down = new int[]{arlene_x, arlene_y + 1};

        if (arlene_x == DiscGame.BOARD_WIDTH - 1) {
            if (arlene_y == 0) {
                // If we're in the upper left corner, only have move down and move right in set of possible moves
                arlene_moves = new int[][]{move_down, move_right};
            }
            else if (arlene_y == DiscGame.BOARD_HEIGHT - 1){
                // If we're in the lower left corner, only have move up and move right in set of possible moves
                arlene_moves = new int[][]{move_up, move_right};

            }
            else {
                // We're on the left edge, only have move right, move up, and move down in possible moves
                arlene_moves = new int[][]{move_up, move_down, move_right};
            }
        }
        else if (arlene_x == 0) {
            if (arlene_y == 0) {
                // If we're in the upper right corner, only have move down and move left in set of possible moves
                arlene_moves = new int[][]{move_down, move_left};
            }
            else if (arlene_y == DiscGame.BOARD_HEIGHT - 1){
                // If we're in the lower right corner, only have move up and move left in set of possible moves
                arlene_moves = new int[][]{move_up, move_left};
            }
            else {
                // We're on the right edge, only have move left, move up, and move down in possible moves
                arlene_moves = new int[][]{move_up, move_down, move_left};
            }
        }
        else if (arlene_y == 0) {
            // We're on the top edge, only have move left, move right, and move down in possible moves
            arlene_moves = new int[][]{move_left, move_right, move_down};
        }
        else if (arlene_y == DiscGame.BOARD_HEIGHT - 1) {
            // We're on the bottom edge, only have move left, move right, and move up in possible moves
            arlene_moves = new int[][]{move_left, move_right, move_up};
        }
        else {
            arlene_moves = new int[][]{move_up, move_down, move_left, move_right};
        }
        // Update Arlene's position
        int i = new Random().nextInt(arlene_moves.length);
        DiscGame.arlene.update_position(cells[arlene_moves[i][0]][arlene_moves[i][1]]);*/
        DiscGame.arlene.update_position(DiscGame.arlene_ai.find_next_move());
    }

    public Cell find_cell(int board_x, int board_y) {
        return cells[board_x][board_y];
    }
}
