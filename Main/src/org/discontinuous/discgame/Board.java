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
        LEFT, RIGHT, UP, DOWN, UPPER_LEFT, UPPER_RIGHT, LOWER_LEFT, LOWER_RIGHT
    }

    public static final int CELL_EDGE_SIZE = 48;

    // Asset texture size, independent of how large we want it
    static final int TEXTURE_EDGE = 48;

    static final int WIDTH_OFFSET = DiscGame.DESIRED_WIDTH/2 - (CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2);
    static final int HEIGHT_OFFSET = DiscGame.DESIRED_HEIGHT/2 - (CELL_EDGE_SIZE * DiscGame.BOARD_HEIGHT/2);

    int[] player_position;
    int[] opponent_position;

    Direction relative_to_current;

    // Adjacent boards - on a rotating 3x3 setup so moving to any other board
    // Will still be surrounded by a board on the up, down, left, right
    Board up;
    Board down;
    Board left;
    Board right;
    Board upper_left;
    Board upper_right;
    Board lower_left;
    Board lower_right;

    public Board (int board_height, int board_width, String topic){
        width = board_width;
        height = board_height;
        screen_width = DiscGame.DESIRED_WIDTH;
        screen_height = DiscGame.DESIRED_HEIGHT;
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
                boards[i][j].setup_graph(boards[i][j].cells);
            }
        }
    }

    // Run right before a set_current_board to make sure scaling doesn't stack infinitely
    public void reset_board_positions(){
        reset_board(this);
        reset_board(left);
        reset_board(right);
        reset_board(up);
        reset_board(down);
        reset_board(upper_left);
        reset_board(upper_right);
        reset_board(lower_left);
        reset_board(lower_right);
        // Reset character sizes
        //DiscGame.player.img.setScale(1);
        //DiscGame.computer.img.setScale(1);
    }

    private void reset_board(Board board) {
        Cell cell;
        for (int i = 0; i < board.cells.length; i++) {
            for (int j = 0; j < board.cells[i].length; j++) {
                cell = board.cells[i][j];
                //cell.x = cell.center_x;
                //cell.y = cell.center_y;
                //cell.img.setPosition(cell.x, cell.y);
                //cell.img.setScale(1);
                DiscGame.hover_list.remove(cell);
                DiscGame.click_list.remove(cell);
            }
        }
    }

    public void set_current_board() {
        resize_board(null);
        DiscGame.current_board = this;
        this.relative_to_current = null;

        // Move the other boards
        left.resize_board(Direction.LEFT);
        right.resize_board(Direction.RIGHT);
        up.resize_board(Direction.UP);
        down.resize_board(Direction.DOWN);

        handler_setup();
        left.handler_setup();
        right.handler_setup();
        up.handler_setup();
        down.handler_setup();

        upper_left.resize_board(Direction.UPPER_LEFT);
        lower_left.resize_board(Direction.LOWER_LEFT);
        upper_right.resize_board(Direction.UPPER_RIGHT);
        lower_right.resize_board(Direction.LOWER_RIGHT);

        // Player/Computer not yet set up
        if (null == DiscGame.player || null == DiscGame.computer) return;

        // Move the player and computer to appropriate board
        position_board_entity(
                DiscGame.player.cell.board.relative_to_current,
                DiscGame.player,
                DiscGame.player.cell,
                DiscGame.player.cell.board_x,
                DiscGame.player.cell.board_y);
        position_board_entity(
                DiscGame.computer.cell.board.relative_to_current,
                DiscGame.computer,
                DiscGame.computer.cell,
                DiscGame.computer.cell.board_x,
                DiscGame.computer.cell.board_y);
    }

    private void resize_board(Direction new_direction) {
        relative_to_current = new_direction;
        if (null != new_direction) {
            switch(new_direction) {
                case LEFT:
                    up.relative_to_current = Direction.UPPER_LEFT;
                    down.relative_to_current = Direction.LOWER_LEFT;
                    break;
                case RIGHT:
                    up.relative_to_current = Direction.UPPER_RIGHT;
                    down.relative_to_current = Direction.LOWER_RIGHT;
                    break;
            }
        }
        Cell cell;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cell = cells[i][j];
                position_board_entity(new_direction, cell, cell, i, j);
            }
        }
    }

    private void handler_setup() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                DiscGame.hover_list.add(cells[i][j]);
                DiscGame.click_list.add(cells[i][j]);
            }
        }
    }
    public boolean is_consumed() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (!cells[i][j].consumed) return false;
            }
        }
        return true;
    }

    // Resizes entity relative to the cell that it occupies
    public void position_board_entity(Direction new_direction, Entity entity, Cell cell, float cell_x, float cell_y) {
        entity.old_x = entity.x;
        entity.old_y = entity.y;
        entity.old_scale = entity.img.getScaleX();

        if (null == new_direction) {
            // We're on the current board, reset positioning elements
            entity.new_x = cell.center_x;
            entity.new_y = cell.center_y;
            entity.new_scale = 1;
            entity.setup_animation();
            return;
        }
        switch(new_direction) {
            case LEFT:
                entity.new_x = cell.center_x - 150 - (left.cells.length/2 - cell_x) * 26;
                entity.new_y = cell.center_y - (left.cells.length/2 - cell_y) * 26 + 15;
                entity.new_scale = 0.5f;
                break;
            case RIGHT:
                entity.new_x = cell.center_x + 150 - (right.cells.length/2 - cell_x) * 26 + 26;
                entity.new_y = cell.center_y - (right.cells.length/2 - cell_y) * 26 + 15;
                entity.new_scale = 0.5f;
                break;
            case UP:
                entity.new_x = cell.center_x - (up.cells.length/2 - cell_x) * 26 + 15;
                entity.new_y = cell.center_y + 150 - (up.cells.length/2 - cell_y) * 26 + 26;
                entity.new_scale = 0.5f;
                break;
            case DOWN:
                entity.new_x = cell.center_x - (down.cells.length/2 - cell_x) * 26 + 15;
                entity.new_y = cell.center_y - 150 - (down.cells.length/2 - cell_y) * 26;
                entity.new_scale = 0.5f;
                break;
            case UPPER_LEFT:
                entity.new_x = cell.center_x - 150 - (left.cells.length/2 - cell_x) * 26;
                entity.new_y = cell.center_y + 150 - (up.cells.length/2 - cell_y) * 26 + 26;
                entity.new_scale = 0.0f;
                break;
            case LOWER_LEFT:
                entity.new_x = cell.center_x - 150 - (left.cells.length/2 - cell_x) * 26;
                entity.new_y = cell.center_y - 150 - (down.cells.length/2 - cell_y) * 26;
                entity.new_scale = 0.0f;
                break;
            case UPPER_RIGHT:
                entity.new_x = cell.center_x + 150 - (right.cells.length/2 - cell_x) * 26 + 26;
                entity.new_y = cell.center_y + 150 - (up.cells.length/2 - cell_y) * 26 + 26;
                entity.new_scale = 0.0f;
                break;
            case LOWER_RIGHT:
                entity.new_x = cell.center_x + 150 - (right.cells.length/2 - cell_x) * 26 + 26;
                entity.new_y = cell.center_y - 150 - (down.cells.length/2 - cell_y) * 26;
                entity.new_scale = 0.0f;
                break;
        }
        entity.setup_animation();
    }

    private void link(Board[][] boards, int x, int y) {
        // Wrap around so that all boards are linked with a board in each cardinal direction

        int y_top = (y == boards[x].length - 1) ? 0 : y + 1;
        int y_bottom = (y == 0) ? boards[x].length - 1 : y - 1;
        int x_left = (x == 0) ? boards.length - 1 : x - 1;
        int x_right = (x == boards.length - 1) ? 0 : x + 1;

        up = boards[x][y_top];
        down = boards[x][y_bottom];
        left = boards[x_left][y];
        right = boards[x_right][y];
        upper_left = boards[x_left][y_top];
        upper_right = boards[x_right][y_top];
        lower_left = boards[x_left][y_bottom];
        lower_right = boards[x_right][y_bottom];
    }

    private void checkDrawContestants(SpriteBatch batch, Board board) {
        // Draw contestants
        if (DiscGame.player.cell.board == board) {
            DiscGame.player.draw(batch);
            DiscGame.computer.draw(batch);
        }
    }

    public void draw(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].animate();
                cells[i][ j].draw(batch);
            }
        }

        checkDrawContestants(batch, this);

        // Draw topic
        DiscGame.header_font.draw(batch, topic, screen_width/2 - DiscGame.header_font.getBounds(topic).width/2, screen_height - 12);
    }

    public void draw_left(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < left.cells.length; i++) {
            for (int j = 0; j < left.cells[i].length; j++) {
                left.cells[i][j].animate();
                left.cells[i][j].draw(batch);
            }
        }

        checkDrawContestants(batch, left);

        // Draw topic
        DiscGame.header_font.draw(batch, left.topic, left.cells[0][0].img.getX() - DiscGame.header_font.getBounds(left.topic).width/2, screen_height - 12);
    }

    public void draw_right(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < right.cells.length; i++) {
            for (int j = 0; j < right.cells[i].length; j++) {
                right.cells[i][j].animate();
                right.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, right);

        // Draw topic
        DiscGame.header_font.draw(batch, right.topic, right.cells[0][0].img.getX() - DiscGame.header_font.getBounds(right.topic).width/2, screen_height - 12);
    }

    public void draw_up(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < up.cells.length; i++) {
            for (int j = 0; j < up.cells[i].length; j++) {
                up.cells[i][j].animate();
                up.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, up);
    }

    public void draw_down(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < down.cells.length; i++) {
            for (int j = 0; j < down.cells[i].length; j++) {
                down.cells[i][j].animate();
                down.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, down);
    }

    public void draw_upper_left(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < upper_left.cells.length; i++) {
            for (int j = 0; j < upper_left.cells[i].length; j++) {
                upper_left.cells[i][j].animate();
                upper_left.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, upper_left);
    }

    public void draw_lower_left(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < lower_left.cells.length; i++) {
            for (int j = 0; j < lower_left.cells[i].length; j++) {
                lower_left.cells[i][j].animate();
                lower_left.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, lower_left);
    }

    public void draw_upper_right(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < upper_right.cells.length; i++) {
            for (int j = 0; j < upper_right.cells[i].length; j++) {
                upper_right.cells[i][j].animate();
                upper_right.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, upper_right);
    }

    public void draw_lower_right(SpriteBatch batch) {
        // Draw the space of ideas
        for (int i = 0; i < lower_right.cells.length; i++) {
            for (int j = 0; j < lower_right.cells[i].length; j++) {
                lower_right.cells[i][j].animate();
                lower_right.cells[i][ j].draw(batch);
            }
        }
        checkDrawContestants(batch, lower_right);
    }

    public void move_computer() {
        DiscGame.computer.update_position(DiscGame.computer_ai.find_next_move());
    }
}
