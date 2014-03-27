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

    static final int CELL_EDGE_SIZE = 48;

    // Asset texture size, independent of how large we want it
    static final int TEXTURE_EDGE = 64;

    static final int WIDTH_OFFSET = DiscGame.screen_width/2 - (CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 10;
    static final int HEIGHT_OFFSET = 40;

    int[] player_position;
    int[] opponent_position;

    // Adjacent boards - on a rotating 3x3 setup so moving to any other board
    // Will still be surrounded by a board on the up, down, left, right
    Board up;
    Board down;
    Board left;
    Board right;

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
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(concepts[k], false, false, i, j,
                        screen_width - WIDTH_OFFSET - (CELL_EDGE_SIZE * (i + 1)),
                        screen_height - HEIGHT_OFFSET - (CELL_EDGE_SIZE * (j + 1)),
                        CELL_EDGE_SIZE);
                k++;
            }
        }

        setup_graph(cells);
    }

    private void setup_graph(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].createAdjacentList(cells);
            }
        }
    }

    public static void link_boards(Board[][] boards) {
        for (int i = 0; i < boards.length; i++) {
            for (int j = 0; j < boards[i].length; j++) {
                boards[i][j].link(boards, i, j);
            }
        }
    }

    public void set_current_board() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].add_handlers();
            }
        }
        DiscGame.current_board = this;
    }

    private void link(Board[][] boards, int x, int y) {
        // Wrap around so that all boards are linked with a board in each cardinal direction
        up = (y == boards[x].length - 1) ?  boards[x][0] : boards[x][y + 1];
        down = (y == 0) ? boards[x][boards[x].length - 1] : boards[x][y - 1];
        left = (x == 0) ? boards[boards.length - 1][y] : boards[x - 1][y];
        right = (x == boards.length - 1) ? boards[0][y] : boards[x + 1][y];
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
        DiscGame.arlene.update_position(DiscGame.arlene_ai.find_next_move());
    }
}
