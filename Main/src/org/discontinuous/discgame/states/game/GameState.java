package org.discontinuous.discgame.states.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.abilities.AbilitiesButton;
import org.discontinuous.discgame.contestants.Confucius;
import org.discontinuous.discgame.contestants.Socrates;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Urk on 4/9/14.
 */
public class GameState {
    // Any common GameState methods will go here, draw_batch and draw_shapes have different parameters at the moment
    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

    public static Board[][] boards;
    public static Board current_board;
    public static Contestant player;
    public static Contestant computer;

    public static final int BOARD_WIDTH = 4;
    public static final int BOARD_HEIGHT = 4;

    static ArrayList<Topic> topics;

    static DealPower dealpower;

    static final int DIALOG_X = 20;
    static final int DIALOG_Y = 20;

    public static AI computer_ai;
    public static AbilitiesButton abilities_button;
    public static ArrayList<EndGameOption> endgame_options;

    public static Texture movestats;

    public static DialogOption[] dialog_options;

    public static void create(int DESIRED_WIDTH, int DESIRED_HEIGHT, ArrayList<Entity> hover_list, ArrayList<Entity> click_list) {

        // Setup fonts
        //setupFonts();

        // Setup confidence/inspiration icons
        setupIcons();

        // Setup board and character entities - may need new entity subclass for characters
        // These are created and added to the hover list in order from top to bottom - so no need for layer.

        setupPlayer(DESIRED_WIDTH);
        setupOpponent(DESIRED_WIDTH);

        // Set up boards sprite to show who is ahead on each board
        Board.left_player = new Sprite(player.img.getTexture(), 48, 48);
        Board.left_player.setPosition(DESIRED_WIDTH/2 - 242, 280);
        Board.left_player.setScale(0.6f);
        Board.left_computer = new Sprite(computer.img.getTexture(), 48, 48);
        Board.left_computer.setPosition(DESIRED_WIDTH/2 - 130, 280);
        Board.left_computer.setScale(0.6f);

        Board.right_player = new Sprite(player.img.getTexture(), 48, 48);
        Board.right_player.setPosition(DESIRED_WIDTH/2 + 82, 280);
        Board.right_player.setScale(0.6f);
        Board.right_computer = new Sprite(computer.img.getTexture(), 48, 48);
        Board.right_computer.setPosition(DESIRED_WIDTH/2 + 196, 280);
        Board.right_computer.setScale(0.6f);

        Board.up_player = new Sprite(player.img.getTexture(), 48, 48);
        Board.up_player.setPosition(DESIRED_WIDTH/2 - 77, 442);
        Board.up_player.setScale(0.6f);
        Board.up_computer = new Sprite(computer.img.getTexture(), 48, 48);
        Board.up_computer.setPosition(DESIRED_WIDTH/2 + 37, 442);
        Board.up_computer.setScale(0.6f);

        Board.down_player = new Sprite(player.img.getTexture(), 48, 48);
        Board.down_player.setPosition(DESIRED_WIDTH/2 - 77, 115);
        Board.down_player.setScale(0.6f);
        Board.down_computer = new Sprite(computer.img.getTexture(), 48, 48);
        Board.down_computer.setPosition(DESIRED_WIDTH/2 + 37, 115);
        Board.down_computer.setScale(0.6f);

        //Load dialog
        loadDialog();

        // Setup a 3x3 board set.
        // TODO: Move out into constants, but I don't foresee being anything other than 3 x 3
        boards = new Board[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boards[i][j] = new Board(BOARD_HEIGHT, BOARD_WIDTH, topics.remove((int) (Math.random() * topics.size())));
            }
        }
        Board.link_boards(boards);

        // Do this in this order since we want board hover on top of player/opponent
        player.set_cell(boards[1][1], BOARD_WIDTH - 1, BOARD_HEIGHT - 1);
        computer.set_cell(boards[1][1], 0, 0);

        boards[1][1].set_current_board();

        // Setup dialog option hovers
        setupDialogEntities();

        // Setup Deal Power counter
        dealpower = new DealPower();

        // Setup state coordinates
        // TODO: Move out to its own method when it gets unwieldly
        //InDialog.setTooltipX(DESIRED_WIDTH / 2 - 200);
        InDialog.setTooltipX(DESIRED_WIDTH / 2 - 355 / 2);
        //SelectDialog.setTooltipX(SympGame.DESIRED_WIDTH/2 - 120);
        SelectDialog.setTooltipX(DIALOG_X);
        //setTooltipY(dialog_height);
        SelectDialog.setTooltipY(DIALOG_Y);

        // TODO: Need a better way of setting up cross references to each other, if we don't do this here there's a null ref
        player.set_opponent(computer);
        computer.set_opponent(player);
        player.setup_adjacent();
        player.update_dialog_options();
        player.update_abilities();

        // Setup AI class so Arlene doesn't wander randomly.
        computer_ai = new AI(computer, player);

        // Setup ability button
        abilities_button = new AbilitiesButton(110, 300, 64, 64, hover_list, click_list, player.get_abilities(), SympGame.movestats_font);
        abilities_button.setImg(SympGame.manager.get("img/abilities.png", Texture.class));

        // Setup end game options
        endgame_options = new ArrayList();
        setupEndgameOptions(endgame_options);
    }

    public static void render(SpriteBatch batch, ShapeRenderer shapes) {
        // Now we're doing the sprite batch
        batch.begin();
        drawBatchCore(batch);

        // debug for AI
        /*
        if (!arlene_ai.possible_moves.isEmpty()) {
            int i = 0;
            for (Map.Entry<Cell, Float> entry: arlene_ai.possible_moves.entrySet()) {
                header_font.draw(batch, entry.getKey().type + ": " + entry.getValue(), 700, 500 + i * 30);
                i++;
            }
            header_font.draw(batch, "Max value there was: " + arlene_ai.max_value, 600, 700);
        }
        */

        // debug for mouse
        SympGame.header_font.draw(batch, "Mouse X: " + SympGame.mouse_x + " Mouse Y: " + SympGame.mouse_y + "Hovering over: " + SympGame.hover.getClass(), 400, 520);
        //header_font.draw(batch, "View Width: " + view_width + " View Height: " + view_height + " View X: " + view_x + " View Y: " + view_y, 400, 400);
        //header_font.draw(batch, "Hovering over: " + SympGame.hover.getClass(), 400, 500);

        // debug for phone resolution

        batch.end();

        // FFS, I can't use shape renderer for the bars inside batch.  So it needs to be done outside.
        //shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        //drawShapesCore();
        StateHandling.drawGameShapes(shapes);
        shapes.end();

        // I really hate not drawing all in one spritebatch block - but this hacky solution works for now
        batch.begin();
        StateHandling.drawGameBatch(batch);
        // TODO: Hacky way to hide this in the post game scenario - Hack moved to statehandling drawGameBatch
        //if(StateHandling.currentState != StateHandling.State.PostGameResult) { drawBatchCoreTop(); }
        batch.end();

        // Need a separate shape and sprite batch for hovers
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        SympGame.shape_hover.drawShapeHover(shapes);
        shapes.end();

        batch.begin();
        // Assumption is that only one entity is allowed to be hovered over at any given moment
        SympGame.hover.drawHover(batch);
        batch.end();
    }

/*
    public void drawShapesCore() {

        player.draw_ethical(shapes);
        player.draw_logical(shapes);
        player.draw_interrogate(shapes);
        player.draw_intimidate(shapes);
        computer.draw_ethical(shapes);
        computer.draw_logical(shapes);
        computer.draw_interrogate(shapes);
        computer.draw_intimidate(shapes);

    }
*/

    public static void drawBatchCore(SpriteBatch batch) {
        // Draw characters - order matters since hover states are affected
        player.portrait.draw(batch);
        computer.portrait.draw(batch);

        // Draw the board & Board topics

        // Adjust the order so that those transitioning underneath are below
        Cell sample_cell = current_board.get_sample_cell();


        current_board.draw_upper_left(batch);
        current_board.draw_lower_left(batch);
        current_board.draw_upper_right(batch);
        current_board.draw_lower_right(batch);

        if (sample_cell.x <= sample_cell.new_x) {
            // Moving from left to right
            current_board.draw_left(batch);
            current_board.draw_right(batch);
        }
        if (sample_cell.x > sample_cell.new_x) {
            // Moving from right to left
            current_board.draw_right(batch);
            current_board.draw_left(batch);
        }
        if (sample_cell.y >= sample_cell.new_y) {
            // Moving from top down
            current_board.draw_up(batch);
            current_board.draw_down(batch);
        }
        if (sample_cell.y < sample_cell.new_y) {
            // Moving from bottom up
            current_board.draw_down(batch);
            current_board.draw_up(batch);
        }
        current_board.draw(batch);

        // Draw argument bars
        player.draw_ethical(batch);
        player.draw_logical(batch);
        player.draw_interrogate(batch);
        player.draw_intimidate(batch);
        computer.draw_ethical(batch);
        computer.draw_logical(batch);
        computer.draw_interrogate(batch);
        computer.draw_intimidate(batch);

        // Draw boards won and moves left
        player.draw_boards_won(batch);
        computer.draw_boards_won(batch);
        player.draw_moves_left(batch);
        computer.draw_moves_left(batch);

        // TOPIC FOR DEBATE
        //header_font.draw(batch, "Resolved: That Prof. Elecantos should not horribly murder us and obliterate our souls.", screen_width/2 - 300, screen_height - 12);

        // Draw Deal Power
        //dealpower.draw(batch);
    }

    public static void drawBatchCoreTop(SpriteBatch batch) {
        // Draw confidence/inspiration amounts
        player.draw_bar_counters(batch);
        computer.draw_bar_counters(batch);
    }

    public static void setupPlayer(int DESIRED_WIDTH) {
        player = new Socrates(true, BOARD_WIDTH, BOARD_HEIGHT, DESIRED_WIDTH, SympGame.movestats_font, SympGame.manager.get("img/socrates.png", Texture.class), SympGame.manager.get("img/zhugemini.png", Texture.class));
    }

    public static void setupOpponent(int DESIRED_WIDTH) {
        computer = new Confucius(false, 1, 1, DESIRED_WIDTH, SympGame.movestats_font, SympGame.manager.get("img/confucius.png", Texture.class), SympGame.manager.get("img/arlenemini.png", Texture.class));
    }

    /*
    public static void setupFonts() {
        FreeTypeFontGenerator sinanova = SympGame.manager.get("fonts/SinaNovaReg.otf", FreeTypeFontGenerator.class);
        FreeTypeFontGenerator ptsans = SympGame.manager.get("fonts/PTSans.ttf", FreeTypeFontGenerator.class);
        FreeTypeFontGenerator nightmare = SympGame.manager.get("fonts/Cinzel-Regular.ttf", FreeTypeFontGenerator.class);
        header_font = sinanova.generateFont(18);
        deal_font = sinanova.generateFont(24);
        animation_font = sinanova.generateFont(24);
        movestats_font = ptsans.generateFont(20);
        text_font = ptsans.generateFont(16);
        dialog_font = ptsans.generateFont(20);
        text_font_small = ptsans.generateFont(13);
        nightmare_font = nightmare.generateFont(20);
        sinanova.dispose();
        ptsans.dispose();
    }*/

    public static void loadDialog() {
        Yaml yaml = new Yaml();
        // TODO: Needs a try catch to pick up file read exceptions, including someone messing with the structure
        topics = new ArrayList();
        // TODO: Have the AssetManager handle this so that it cleans up nicely.
        for (Object object : yaml.loadAll(Gdx.files.internal("dialog/dialog.yml").read())) {
            LinkedHashMap topic = (LinkedHashMap) object;
            topics.add(new Topic(topic.get("name"), topic.get("logical"), topic.get("ethical"), topic.get("interrogate"), topic.get("intimidate")));
        }
    }

    public static void setupIcons() {
        /*
        String confidence = "Confidence \n At zero confidence, your opponent forces you to concede the match.  This results in the end of the game along with a 1000 DP penalty.";
        String inspiration = "Inspiration \n This enables you to use special debate techniques.  Using a special ability costs your turn, but you don't take backtracking penalties.";
        confidence_icon = new Texture(Gdx.files.internal("img/confidence.png"));
        inspiration_icon = new Texture(Gdx.files.internal("img/inspiration.png"));

        confidence_icon_player = new Icon(DESIRED_WIDTH/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 95, screen_height - 65, 32, 32, DESIRED_WIDTH/2 - 200, 600, 400, 80, "Your " + confidence);
        confidence_icon_player.setImg(confidence_icon);
        confidence_icon_opponent = new Icon(DESIRED_WIDTH/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 30, screen_height - 67, 32, 32, DESIRED_WIDTH/2 - 200, 600, 400, 80, "Opponent " + confidence);
        confidence_icon_opponent.setImg(confidence_icon);
        inspiration_icon_player = new Icon(DESIRED_WIDTH/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 55, screen_height - 65, 32, 32, DESIRED_WIDTH/2 - 200, 600, 400, 80, "Your " + inspiration);
        inspiration_icon_player.setImg(inspiration_icon);
        inspiration_icon_opponent = new Icon(DESIRED_WIDTH/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 70, screen_height - 67, 32, 32, DESIRED_WIDTH/2 - 200, 600, 400, 80, "Opponent " + inspiration);
        inspiration_icon_opponent.setImg(inspiration_icon);
        */
        // Assign the movestats texture to be used generally in portraits
        movestats = SympGame.manager.get("img/movestats.png", Texture.class);
    }

    // Create four new entities for dialog options whose role is solely to be a hover over/click handler and draw a bounding box
    public static void setupDialogEntities() {
        dialog_options = new DialogOption[4];
        dialog_options[0] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[1] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[2] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[3] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
    }

    public static void setupEndgameOptions(ArrayList<EndGameOption> endgame_options) {
        endgame_options.add(new EndGameOption(2000, SympGame.movestats_font,
                "Help us create a place in society rather than perpetuating this constant destruction. (Arlene joins the party)",
                "Help us create a place in society rather than perpetuating this constant destruction.",
                "How do you propose going about that?"));
        endgame_options.add(new EndGameOption(1000, SympGame.movestats_font,
                "You should be more worried about your position than whether you can detain us. (+20 Initiative on Fleet Combat)",
                "You should be more worried about your position than whether you can detain us.",
                "So this was nothing more than a distraction.  We'll destroy you, regardless."));
        endgame_options.add(new EndGameOption(500, SympGame.movestats_font,
                "Let us leave.  Pretend you never saw us.  We'll leave now and withdraw our forces.",
                "Let us leave.  Pretend you never saw us.  We'll leave now and withdraw our forces.",
                "This is not a good place to detain you, regardless.  Go ahead and run.  But you only delay the inevitable."));
        endgame_options.add(new EndGameOption(0, SympGame.movestats_font,
                "Looks like this isn't going to end well.  We're leaving, and by force if necessary. (Initiate Fleet Combat)",
                "Looks like this isn't going to end well.  We're leaving, and by force if necessary.",
                "You can't just waltz out of here.  This sector has been on lockdown since we dropped out of tachyspace."));
        endgame_options.add(new EndGameOption(-9999, SympGame.nightmare_font,
                "THE NIGHTMARES OF THE PAST CANNOT BE SO EASILY DEFEATED. (Initiate Squad Combat)",
                "THE NIGHTMARES OF THE PAST CANNOT BE SO EASILY DEFEATED.",
                "SO BE IT.  RETURN TO THE VOID, ABOMINATION."));
    }

}
