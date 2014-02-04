package org.discontinuous.discgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Each cell representing an argument in the table of ideas
 */
public class Cell extends Entity {
    // Set up our static textures
    static Texture logical = new Texture(Gdx.files.internal("cell/logical.jpg"));
    static Texture ethical = new Texture(Gdx.files.internal("cell/ethical.jpg"));
    static Texture interrogate = new Texture(Gdx.files.internal("cell/interrogate.jpg"));
    static Texture intimidate = new Texture(Gdx.files.internal("cell/intimidate.jpg"));
    static Texture consume = new Texture(Gdx.files.internal("cell/consumed.jpg"));

    // Board position
    int board_x;
    int board_y;


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
    String yi_dialog;
    String arlene_dialog;

    // Super basic constructor
    public Cell (int concept_num, boolean consumed, boolean visible, int board_x, int board_y, int x, int y, int length){
        super(x, y, length, length);
        try {
            switch (concept_num) {
                case 1: type = concepts.Logical; img = new Sprite(logical, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE); break;
                case 2: type = concepts.Ethical; img = new Sprite(ethical, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE); break;
                case 3: type = concepts.Interrogate; img = new Sprite(interrogate, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE); break;
                case 4: type = concepts.Intimidate; img = new Sprite(intimidate, Board.TEXTURE_EDGE, Board.TEXTURE_EDGE); break;
                default: throw new Exception();
            }
            img.setPosition(x, y);
            // TODO: Hack in place for 48x48 for more screen real estate, remove/redo
            img.scale((float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE - 1);
        }
        catch (Exception e) {
            System.err.println("Invalid concept cell type!");
            System.exit(0);
        }
        this.consumed = consumed;
        this.visible = visible;
        this.board_x = board_x;
        this.board_y = board_y;

        // Entity has action on hover, add to hover list
        DiscGame.hover_list.add(this);
        // Entity may have action on click, add to click list
        DiscGame.click_list.add(this);

        //Grab a line of dialog for each character involved
        yi_dialog = DiscGame.topics.get(0).getYiDialog(this);
        arlene_dialog = DiscGame.topics.get(0).getArleneDialog(this);
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
        if (DiscGame.yi.is_adjacent_to(this) && State.checkState(State.states.SelectDialog)) {
            dialog_option.drawShapeHover(shapes);
        }
    }
    public void drawHover(SpriteBatch batch) {
        // If the cell is adjacent to the player, scale it up and redraw on top
        if (DiscGame.yi.is_adjacent_to(this)&& State.checkState(State.states.SelectDialog)) {
            img.scale(0.3f);
            img.draw(batch);
            img.scale(-0.3f);
            switch (type) {
                case Logical: DiscGame.text_font.draw(batch, type.toString(), x + 8, y + 38); break;
                case Ethical: DiscGame.text_font.draw(batch, type.toString(), x + 10, y + 38); break;
                case Interrogate: DiscGame.text_font_small.draw(batch, type.toString(), x + 2, y + 38); break;
                case Intimidate: DiscGame.text_font_small.draw(batch, type.toString(), x + 3, y + 38); break;
            }
            //Redraw so that the shape doesn't cover text
            dialog_option.drawDialogOption(batch);
        }
        else {
            // Tint it on hover
            img.setColor(0.0f, 0.0f, 0.0f, 0.3f);
            img.draw(batch);
            // Return image to original color/scale
            img.setColor(1f, 1f, 1f, 1);

            switch (type) {
                case Logical: DiscGame.text_font.draw(batch, type.toString(), x + 8, y + 38); break;
                case Ethical: DiscGame.text_font.draw(batch, type.toString(), x + 10, y + 38); break;
                case Interrogate: DiscGame.text_font_small.draw(batch, type.toString(), x + 2, y + 38); break;
                case Intimidate: DiscGame.text_font_small.draw(batch, type.toString(), x + 3, y + 38); break;
            }
        }
        //DiscGame.text_font_small.draw(batch, "X: " + Gdx.input.getX() + " Y: " + Gdx.input.getY() + " Hover item: " + DiscGame.hover.toString(), x, y + 30);
        //DiscGame.text_font_small.draw(batch, "Board X: " + board_x + " Board Y: " + board_y, x, y + 25);
    }

    public void clickHandler (){
        //check that mouse is in this area

        //only time we're going to do something, player can't travel otherwise
        if (DiscGame.yi.is_adjacent_to(this) && State.checkState(State.states.SelectDialog)) {
            DiscGame.yi.update_position(this);
        }
    }

    // Imperative; mark this as consumed
    public void consume() {

    }

    // Imperative; mark this as visible
    public void seen() {
        //TODO: IMPLEMENT LINE OF SIGHT CALCULATIONS
    }

    public ArrayList<Cell> find_adjacent_cells() {
        ArrayList<Cell> adjacent = new ArrayList();
        // If exists AND opponent is not on it, add adjacent cell to array
        if (board_x + 1 < DiscGame.BOARD_WIDTH && DiscGame.board.cells[board_x + 1][board_y].occupied == false) {adjacent.add(DiscGame.board.cells[board_x + 1][board_y]);}
        if (board_x - 1 >= 0 && DiscGame.board.cells[board_x - 1][board_y].occupied == false) {adjacent.add(DiscGame.board.cells[board_x - 1][board_y]);}
        if (board_y + 1 < DiscGame.BOARD_HEIGHT && DiscGame.board.cells[board_x][board_y + 1].occupied == false) { adjacent.add(DiscGame.board.cells[board_x][board_y + 1]);}
        if (board_y - 1 >= 0 && DiscGame.board.cells[board_x][board_y - 1].occupied == false) {adjacent.add(DiscGame.board.cells[board_x][board_y - 1]);}
        return adjacent;
    }
}
