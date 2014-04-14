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
    String topic;
    Cell[][] cells;

    enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    public static final int CELL_EDGE_SIZE = 48;

    // Asset texture size, independent of how large we want it
    static final int TEXTURE_EDGE = 64;

    static final int WIDTH_OFFSET = DiscGame.screen_width/2 - (CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 10;
    static final int HEIGHT_OFFSET = 120;

    int[] player_position;
    int[] opponent_position;

    // Adjacent boards - on a rotating 3x3 setup so moving to any other board
    // Will still be surrounded by a board on the up, down, left, right
    Board up;
    Board down;
    Board left;
    Board right;

    public Board (int board_height, int board_width, String topic){
        width = board_width;
        height = board_height;
        screen_width = DiscGame.screen_width;
        screen_height = DiscGame.screen_height;
        this.topic = topic;

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
                        CELL_EDGE_SIZE, this);
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

    // Run right before a set_current_board to make sure scaling doesn't stack infinitely
    public void reset_board_positions(){
        reset_board(left);
        reset_board(right);
        reset_board(up);
        reset_board(down);
    }

    private void reset_board(Board board) {
        Cell cell;
        for (int i = 0; i < board.cells.length; i++) {
            for (int j = 0; j < board.cells[i].length; j++) {
                cell = board.cells[i][j];
                cell.x = cell.center_x;
                cell.y = cell.center_y;
                cell.img.setPosition(cell.x, cell.y);
                cell.img.scale(0.4f);
            }
        }
    }

    public void set_current_board() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].img.setPosition(cells[i][j].center_x, cells[i][j].center_y);
            }
        }
        DiscGame.current_board = this;

        // Make the other boards smaller
        resize_board(Direction.LEFT, left);
        resize_board(Direction.RIGHT, right);
        resize_board(Direction.UP, up);
        resize_board(Direction.DOWN, down);

    }

    private void resize_board(Direction direction, Board board) {
        Cell cell;
        for (int i = 0; i < board.cells.length; i++) {
            for (int j = 0; j < board.cells[i].length; j++) {
                cell = board.cells[i][j];
                switch(direction) {
                    case LEFT:
                        cell.x = cell.center_x - 400 - (board.cells.length/2 - i) * 26;
                        cell.y = cell.center_y - (board.cells.length/2 - j) * 26;
                        break;
                    case RIGHT:
                        cell.x = cell.center_x + 400 - (right.cells.length/2 - i) * 26;
                        cell.y = cell.center_y - (right.cells.length/2 - j) * 26;
                        break;
                    case UP:
                        cell.x = cell.center_x - (up.cells.length/2 - i) * 26;
                        cell.y = cell.center_y + 200 - (up.cells.length/2 - j) * 26;
                    case DOWN:
                        cell.x = cell.center_x - (down.cells.length/2 - i) * 26;
                        cell.y = cell.center_y - 200 - (down.cells.length/2 - j) * 26;
                }
                cell.img.setPosition(cell.x, cell.y);
                cell.img.scale(-0.4f);
            }
        }
    }

    private void link(Board[][] boards, int x, int y) {
        // Wrap around so that all boards are linked with a board in each cardinal direction
        up = (y == boards[x].length - 1) ?  boards[x][0] : boards[x][y + 1];
        down = (y == 0) ? boards[x][boards[x].length - 1] : boards[x][y - 1];
        left = (x == 0) ? boards[boards.length - 1][y] : boards[x - 1][y];
        right = (x == boards.length - 1) ? boards[0][y] : boards[x + 1][y];
    }

    public void draw(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][ j].draw(batch);
            }
        }
        // Draw topic
        DiscGame.header_font.draw(batch, topic, screen_width/2 - DiscGame.header_font.getBounds(topic).width/2, screen_height - 12);
    }

    public void draw_left(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < left.cells.length; i++) {
            for (int j = 0; j < left.cells[i].length; j++) {
                left.cells[i][j].draw(batch);
            }
        }
        // Draw topic
        DiscGame.header_font.draw(batch, left.topic, left.cells[0][0].img.getX() - DiscGame.header_font.getBounds(left.topic).width/2, screen_height - 12);
    }

    public void draw_right(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < right.cells.length; i++) {
            for (int j = 0; j < right.cells[i].length; j++) {
                right.cells[i][ j].draw(batch);
            }
        }
        // Draw topic
        DiscGame.header_font.draw(batch, right.topic, right.cells[0][0].img.getX() - DiscGame.header_font.getBounds(right.topic).width/2, screen_height - 12);
    }

    public void draw_up(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < up.cells.length; i++) {
            for (int j = 0; j < up.cells[i].length; j++) {
                up.cells[i][ j].draw(batch);
            }
        }
    }

    public void draw_down(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < down.cells.length; i++) {
            for (int j = 0; j < down.cells[i].length; j++) {
                down.cells[i][ j].draw(batch);
            }
        }
    }

    public void move_computer() {
        DiscGame.computer.update_position(DiscGame.computer_ai.find_next_move());
    }
}
