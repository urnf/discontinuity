package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.StateHandling.State;
import org.discontinuous.discgame.abilities.AbilityTarget;
import org.discontinuous.discgame.states.game.GameState;

import java.util.ArrayList;

/**
 * Each cell representing an argument in the table of ideas
 */
public class Cell extends Entity {
    // Set up our static textures
    static Texture tintable = SympGame.manager.get("cell/tint.png", Texture.class);
    /*
    static Texture logical = new Texture(Gdx.files.internal("cell/logical.jpg"));
    static Texture ethical = new Texture(Gdx.files.internal("cell/ethical.jpg"));
    static Texture interrogate = new Texture(Gdx.files.internal("cell/interrogate.jpg"));
    static Texture intimidate = new Texture(Gdx.files.internal("cell/intimidate.jpg"));
    static Texture consume = new Texture(Gdx.files.internal("cell/consumed.jpg"));
    */
    // Board position
    int board_x;
    int board_y;

    // X and Y coordinates when in the center, used for animation to these coordinates
    int center_x;
    int center_y;

    // Cell type
    public enum concepts {
        Logical, Ethical, Interrogate, Intimidate
    }
    concepts type;
    // Cell consumed state
    boolean consumed;
    // Cell visible (fog of war)
    boolean visible;

    // Cell occupied
    boolean occupied;

    // Dialog, if applicable
    DialogOption dialog_option;
    public String player_dialog;
    //public String player_resp_dialog;
    public String computer_dialog;
    //public String computer_resp_dialog;

    // Adjacent cells
    ArrayList<Cell> adjacent;

    // Associated board
    Board board;

    //static float SCALE = (float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE;

    // Super basic constructor
    public Cell (int concept_num, boolean consumed, boolean visible, int board_x, int board_y, int x, int y, int length, Board board){
        super(x, y, length, length);
        try {
            switch (concept_num) {
                case 1:
                    type = concepts.Logical;
                    img = new Sprite(tintable, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
                    img.setColor(Colors.ColorMap.get("logical_color")); // Make it adjustable later on
                    break;
                case 2:
                    type = concepts.Ethical;
                    img = new Sprite(tintable, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
                    img.setColor(Colors.ColorMap.get("ethical_color"));
                    break;
                case 3:
                    type = concepts.Interrogate;
                    img = new Sprite(tintable, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
                    img.setColor(Colors.ColorMap.get("interrogate_color"));
                    break;
                case 4:
                    type = concepts.Intimidate;
                    img = new Sprite(tintable, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
                    img.setColor(Colors.ColorMap.get("intimidate_color"));
                    break;
                default: throw new Exception();
            }
            img.setPosition(x, y);
        }
        catch (Exception e) {
            System.err.println("Invalid concept cell type!");
            System.exit(0);
        }
        this.consumed = consumed;
        this.visible = visible;
        this.board_x = board_x;
        this.board_y = board_y;
        this.center_x = x;
        this.center_y = y;
        this.board = board;

        // Entity has action on hover, add to hover list
        //SympGame.hover_list.add(this);
        // Entity may have action on click, add to click list
        //SympGame.click_list.add(this);

        //Grab a line of dialog for each character involved
        player_dialog = board.topic.getPlayerDialog(this)[0];
        computer_dialog = board.topic.getComputerDialog(this)[0];
    }

    public void createAdjacentList(Cell[][] cells) {
        adjacent = new ArrayList();
        if (board_x + 1 < GameState.BOARD_WIDTH) {
            adjacent.add(cells[board_x + 1][board_y]);
        }
        else {
            adjacent.add(board.left.cells[0][board_y]);
        }
        if (board_x - 1 >= 0) {
            adjacent.add(cells[board_x - 1][board_y]);
        }
        else {
            adjacent.add(board.right.cells[GameState.BOARD_WIDTH - 1][board_y]);
        }
        if (board_y + 1 < GameState.BOARD_HEIGHT) {
            adjacent.add(cells[board_x][board_y + 1]);
        }
        else {
            adjacent.add(board.down.cells[board_x][0]);
        }
        if (board_y - 1 >= 0) {
            adjacent.add(cells[board_x][board_y - 1]);
        }
        else {
            adjacent.add(board.up.cells[board_x][GameState.BOARD_HEIGHT - 1]);
        }
    }

    public void add_handlers() {

    }

    // Override the default setImg in entity, want to use texture_edge instead
    public Entity setImg(Texture img) {
        this.img = new Sprite(img, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE);
        this.img.setPosition(x, y);
        return this;
    }

    public void draw(SpriteBatch batch) {
        img.draw(batch);
    }

    public void drawShapeHover(ShapeRenderer shapes) {
        if (GameState.player.is_adjacent_to(this) && StateHandling.checkState(State.SelectDialog)) {
            dialog_option.drawShapeHover(shapes);
        }
    }
    public void drawHover(SpriteBatch batch) {
        // Return if in post game select
        if (StateHandling.checkState(State.PostGameSelect)) { return; }
        // If in ability targeting selection
        if (StateHandling.checkState(State.AbilityTargeting)) {
            AbilityTarget.target_cell_hover(GameState.player, GameState.player.ability_selected, this, consumed, batch);
            return;
        }
        // If the cell is adjacent to the player, scale it up and redraw on top
        if (GameState.player.is_adjacent_to(this)&& StateHandling.checkState(State.SelectDialog)) {
            enlargeCell(batch);
            //Redraw so that the shape doesn't cover text
            dialog_option.drawDialogOption(batch);
        }
        else {

            switch (type) {
                case Logical:
                    img.setColor(Colors.ColorMap.get("logical_fade"));
                    break;
                case Ethical:
                    img.setColor(Colors.ColorMap.get("ethical_fade"));
                    break;
                case Interrogate:
                    img.setColor(Colors.ColorMap.get("interrogate_fade"));
                    break;
                case Intimidate:
                    img.setColor(Colors.ColorMap.get("intimidate_fade"));
                    break;
            }

            // Tint it on hover
            //Color tempColor = img.getColor();
            //img.setColor(tempColor.r * 0.7f, tempColor.g * 0.7f, tempColor.b * 0.7f, 1);
            img.draw(batch);
            // Redraw char on top
            // Use img.draw instead of draw otherwise player/computer animate twice as fast.
            if (this == GameState.player.cell) GameState.player.img.draw(batch);
            if (this == GameState.computer.cell) GameState.computer.img.draw(batch);
            // Return image to original color/scale
            //img.setColor(tempColor);
            switch (type) {
                case Logical:
                    img.setColor(Colors.ColorMap.get("logical_color"));
                    SympGame.text_font.draw(batch, type.toString(), x + 3, y + 33);
                    break;
                case Ethical:
                    img.setColor(Colors.ColorMap.get("ethical_color"));
                    SympGame.text_font.draw(batch, type.toString(), x + 5, y + 33);
                    break;
                case Interrogate:
                    img.setColor(Colors.ColorMap.get("interrogate_color"));
                    SympGame.text_font_small.draw(batch, type.toString(), x - 3, y + 33);
                    break;
                case Intimidate:
                    img.setColor(Colors.ColorMap.get("intimidate_color"));
                    SympGame.text_font_small.draw(batch, type.toString(), x - 2, y + 33);
                    break;
            }
            if(consumed) img.setColor(Colors.ColorMap.get("consumed"));
        }
        //SympGame.text_font_small.draw(batch, "X: " + Gdx.input.getX() + " Y: " + Gdx.input.getY() + " Hover item: " + SympGame.hover.toString(), x, y + 30);
        //SympGame.text_font_small.draw(batch, "Board X: " + board_x + " Board Y: " + board_y, x, y + 25);
    }

    public void clickHandler (){
        // if not current board, swap in this board
        if (GameState.current_board != board) {
            GameState.current_board.reset_board_positions();
            board.set_current_board();
            return;
        }

        // player can't travel otherwise if not adjacent
        if (GameState.player.is_adjacent_to(this) && StateHandling.checkState(State.SelectDialog)) {
            GameState.player.update_position(this);
        }

        // If in ability targeting selection
        if (StateHandling.checkState(State.AbilityTargeting)) {
            AbilityTarget.target_cell_click(GameState.player, GameState.player.ability_selected, this, consumed);
        }
    }

    // Imperative; mark this as consumed
    public void consume() {
        // mark current cell as consumed
        consumed = true;
        img.setColor(Colors.ColorMap.get("consumed"));
        //img.scale((float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE - 1);
    }

    // Imperative; mark this as visible
    public void seen() {
        //TODO: IMPLEMENT LINE OF SIGHT CALCULATIONS
    }

    public ArrayList<Cell> adjacent_cells() {
        return adjacent;
    }

    public ArrayList<Cell> unoccupied_cells() {
        ArrayList<Cell> return_list = new ArrayList<Cell>(adjacent);
        for (Cell adjacent_cell: adjacent) {
            // Keep only if opponent is not on it
            if( adjacent_cell.occupied == true) {
                return_list.remove(adjacent_cell);
                break;
            }
        }
        return return_list;
    }

    public void enlargeCell(SpriteBatch batch) {
        img.scale(0.3f);
        img.draw(batch);
        img.scale(-0.3f);
        switch (type) {
            case Logical: SympGame.text_font.draw(batch, type.toString(), x + 8, y + 38); break;
            case Ethical: SympGame.text_font.draw(batch, type.toString(), x + 10, y + 38); break;
            case Interrogate: SympGame.text_font_small.draw(batch, type.toString(), x + 2, y + 38); break;
            case Intimidate: SympGame.text_font_small.draw(batch, type.toString(), x + 3, y + 38); break;
        }
    }
}
