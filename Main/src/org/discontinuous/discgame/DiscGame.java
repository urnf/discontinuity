package org.discontinuous.discgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.abilities.*;
import org.discontinuous.discgame.contestants.Confucius;
import org.discontinuous.discgame.contestants.Socrates;
import org.discontinuous.discgame.states.InDialog;
import org.discontinuous.discgame.states.SelectDialog;
import org.yaml.snakeyaml.Yaml;
import org.discontinuous.discgame.StateHandling.State;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Actual game code.  Pleased that unlike Slick, libgdx abstracts
 * the drawing to a higher level than OpenGL rendering, which will
 * be adequate given that I'm planning on working on a purely 2D plane
 */
public class DiscGame extends Game {
    /*
    TODO: DONE - IMPLEMENT PENALTY FOR SWITCHING BEFORE ALL SQUARES CONSUMED
    TODO: IMPLEMENT STAMMER PENALTY FOR NEGATIVE BAR
    TODO: IMPLEMENT BOARD AND DIALOG NAMES
    TODO: IMPLEMENT SCORING PER BOARD


    TODO: BONUS TILES IN CENTER - EXAMPLE, CORINTHIAN COLUMN, CHINESE MEDICINE, ETC.
    TODO: FIX SCALED INPUT AREA FOR SMALLER SUB BOARDS
    TODO: ANIMATE BOARD TRANSITIONS

     */


    /*
    CURRENT IDEA
    - Each player has an ethical, logical, interrogate, intimidate bar
    - these start at different values and cap at different values
    - You gain +1 to each one as you consume it.  You also gain +argument on that specific topic equal to the value of the arugment you have
    - Abilities cost a certain amount of each to use.  Using it still consumes your turn.
    - Combos still work
    - Going over a consumed argument is -1 to all values
    - -1 to ANY value at 0 causes you to lose your next turn "stammer"
    - CHoice between - sitting on the pile for max +argument or using them for abilities
    - Goal of the game is to win the arugments.

     */

    /*
       TODO: DECIDE BETWEEEN 3D CAMERA (Perspective) or 2D with simulated transition for cube of dialog options
     */

    /*
       TODO: Write a library to get report positioning
       TODO: Gson - if need to convert JSON directly to java object
       TODO: UI toolkits export to JSON
     */

    /*
       TODO: Rather than a 3x3 set of squares, think of it as a cube

        Thanks, Hunter!
    */

    /*
       TODO: Think about the arguments as things you need to collect to get certain advantages and disadvantages
       - For example, a difference between one type becomes more or less important when opponent has collected a certain amount
       - Get rid of health/mana - NOT needed for arguments.

           +         +
           +         +           +             +
       | Logical | Ethical | Interrogate | Intimidate
        Interrogate - preps the other three for more power

        Does this end up feeling like a puzzle game, instead?

        Thanks, Hunter!

     */

    /*
        TODO: Side swipes to select the square you want - or to move the cube

        Thanks, Ethan!
     */

    // TODO: Too much casting.  I'm trying to write Java like I'm writing Ruby.  Clean up/reduce casting.
    // TODO: Model edges on graph - dialog is dynamically chosen based on edges - so logical -> ethical will choose from series of text
    // TODO: Convert players to be dynamic
    // TODO: - Prorated combos - 50%, 25%, 12.5%, etc.
    // TODO: - Swap functionality. - Sub board menu previews
    // TODO: - Insults/Compliments
    // TODO: - Oral Swap Hyper Combos
    // TODO: - Fog of war / Line of Sight
    // TODO: - Move from yml to JSON

    // TODO: Giant pile of static variables.  OK for prototype, terrible design.
    static Board[][] boards;
    static Board current_board;
    static Contestant player;
    static Contestant computer;
    OrthographicCamera camera;
    SpriteBatch batch;
    ShapeRenderer shapes;
    //Texture confidence_icon;
    //Texture inspiration_icon;
    //Icon confidence_icon_player;
    //Icon confidence_icon_opponent;
    //Icon inspiration_icon_player;
    //Icon inspiration_icon_opponent;
    static AbilitiesButton abilities_button;
    static AI computer_ai;

    public static AssetManager manager = new AssetManager();

    static DialogOption[] dialog_options;

    static DealPower dealpower;

    static Entity empty_hover = new Entity(0, 0, 0, 0);
    static Entity hover = empty_hover; //removes the need for a null check later
    static Entity shape_hover = empty_hover;
    static BitmapFont header_font;
    static BitmapFont deal_font;
    static BitmapFont text_font;
    static BitmapFont movestats_font;
    static BitmapFont nightmare_font;
    static BitmapFont animation_font;
    static BitmapFont dialog_font;
    static BitmapFont text_font_small;
    static int screen_width;
    static int screen_height;

    static ArrayList<Entity> click_list;
    static ArrayList<Entity> hover_list;
    static ArrayList<Entity> shape_hover_list;
    static ArrayList<Topic> topics;
    static ArrayList<EndGameOption> endgame_options;

    static Texture movestats;

    static final int BOARD_WIDTH = 4;
    static final int BOARD_HEIGHT = 4;

    static final int DESIRED_WIDTH = 960;
    static final int DESIRED_HEIGHT = 540;

    static final int DIALOG_X = 20;
    static final int DIALOG_Y = 20;

    static int view_x = 0;
    static int view_y = 0;
    int view_width;
    int view_height;
    static float scale;

    //debug
    static int mouse_x;
    static int mouse_y;

    @Override
    // Overriding resize to make it keep aspect ratio
    // Approach similar to one on acamara.es
    public void resize(int width, int height) {
        float aspect_ratio = (float) width/(float) height;
        float desired_ratio = (float) DESIRED_WIDTH/(float) DESIRED_HEIGHT;
        if (aspect_ratio >= desired_ratio) {
            // Width is larger than desired, so we divide height by DESIRED_HEIGHT
            // And use that as a scaling factor for width
            scale = (float) height/ (float) DESIRED_HEIGHT;
            view_x = (int) ((width - DESIRED_WIDTH * scale)/2f);
        }
        else {
            scale = (float) width/ (float) DESIRED_WIDTH;
            view_y = (int) ((height - DESIRED_HEIGHT * scale)/2f);
        }
        view_width = (int) (DESIRED_WIDTH * scale);
        view_height = (int) (DESIRED_HEIGHT * scale);
    }

    public void create() {
        screen_width = Gdx.graphics.getWidth();
        screen_height = Gdx.graphics.getHeight();
        resize(screen_width, screen_height);

        //Load dialog
        loadDialog();

        manager.load("cell/tint.png", Texture.class);

        manager.load("img/confucius.png", Texture.class);
        manager.load("img/arlenemini.png", Texture.class);

        manager.load("img/socrates.png", Texture.class);
        manager.load("img/zhugemini.png", Texture.class);

        manager.load("img/movestats.png", Texture.class);

        manager.load("img/yi-combos.png", Texture.class);
        manager.load("img/arlene-combos.png", Texture.class);

        manager.load("img/strawman.png", Texture.class);
        manager.load("img/tableflip.png", Texture.class);
        manager.load("img/nonsequitur.png", Texture.class);
        manager.load("img/reasonabledoubt.png", Texture.class);
        manager.load("img/doubledown.png", Texture.class);

        manager.load("img/abilities.png", Texture.class);

        manager.load("img/upper-left.png", Texture.class);
        manager.load("img/upper-right.png", Texture.class);
        manager.load("img/lower-left.png", Texture.class);
        manager.load("img/lower-right.png", Texture.class);

        manager.load("img/YiDemonResize.png", Texture.class);
        manager.load("img/ArleneLichResize.png", Texture.class);

        manager.finishLoading();

        // Setup list of entities which have an action on hover/click
        click_list = new ArrayList();
        hover_list = new ArrayList();
        // Separate hover list for shapes since it uses a different renderer
        shape_hover_list = new ArrayList();

        // Setup fonts
        setupFonts();

        // Setup camera and sprite batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, DESIRED_WIDTH, DESIRED_HEIGHT);
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        // Setup confidence/inspiration icons
        setupIcons();

        // Setup board and character entities - may need new entity subclass for characters
        // These are created and added to the hover list in order from top to bottom - so no need for layer.

        setupPlayer();
        setupOpponent();

        // Setup a 3x3 board set.
        // TODO: Move out into constants, but I don't foresee being anything other than 3 x 3
        boards = new Board[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boards[i][j] = new Board(BOARD_HEIGHT, BOARD_WIDTH, "Column " + String.valueOf(i) + " Row " + String.valueOf(j));
            }
        }
        Board.link_boards(boards);

        // Do this in this order since we want board hover on top of player/opponent
        player.cell = boards[1][1].cells[BOARD_WIDTH - 1][BOARD_HEIGHT - 1];
        computer.cell = boards[1][1].cells[0][0];

        boards[1][1].set_current_board();

        // Setup dialog option hovers
        setupDialogEntities();

        // Setup Deal Power counter
        dealpower = new DealPower();

        // Setup state coordinates
        // TODO: Move out to its own method when it gets unwieldly
        //InDialog.setTooltipX(DESIRED_WIDTH / 2 - 200);
        InDialog.setTooltipX(DESIRED_WIDTH / 2 - 355/2);
        //SelectDialog.setTooltipX(DiscGame.DESIRED_WIDTH/2 - 120);
        SelectDialog.setTooltipX(DIALOG_X);
        //setTooltipY(dialog_height);
        SelectDialog.setTooltipY(DIALOG_Y);

        // TODO: Need a better way of setting up cross references to each other, if we don't do this here there's a null ref
        player.opponent = computer;
        computer.opponent = player;
        player.adjacent = player.cell.unoccupied_cells();
        player.update_dialog_options();
        player.update_abilities();

        // Setup AI class so Arlene doesn't wander randomly.
        computer_ai = new AI(computer, player);

        // Setup ability button
        abilities_button = new AbilitiesButton(110, 300, 64, 64, hover_list, click_list, player.abilities, movestats_font);
        abilities_button.setImg( manager.get("img/abilities.png", Texture.class));

        //DialogProcessor inputProcessor = new DialogProcessor();
        //Gdx.input.setInputProcessor(inputProcessor);

        // Setup end game options
        endgame_options = new ArrayList();
        setupEndgameOptions(endgame_options);
    }
    // Dialog:
    /*
    This is Zhuge Yi (arrow)
    He's a businessman turned philosopher and tactician after being caught up with our crew.
    Aside from cutting wit and a wise demeanor, he's not known to have any special powers.

    This is Arlene Elecantos (arrow)
    Arlene is an ancient, likely undead law professor of immense magical power with a steam-powered raven paralegal.
    She feeds the raven a law student for breakfast every day.  Her students do not know if she's joking about that.

    It's the middle of the game.  She's been hunting your group for half a game.   She's tired and frustrated.
    Now that she's caught you, your job is to justify why she should not slaughter the party horribly with her mind and steely glare.
    Good luck!
     */

    // TODO: Introduce concept of game "states" - the board and confidence bars are really part of the dialogue state, which can change
    // The dialogue in between clicks is just one example - as will be selecting special abilities
    public void render() {

        // Looks like libgdx is wrapping lwjgl's display.update
        // Not sure what else it's doing, though
        camera.update();

        // Keep the aspect ratio that we want
        Gdx.gl.glViewport(view_x, view_y, view_width, view_height);

        // Clears the screen, from tutorial
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Now we're doing the sprite batch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawBatchCore();

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
        header_font.draw(batch, "Mouse X: " + mouse_x + " Mouse Y: " + mouse_y + "Hovering over: " + DiscGame.hover.getClass(), 400, 500);
        header_font.draw(batch, "View Width: " + view_width + " View Height: " + view_height + " View X: " + view_x + " View Y: " + view_y, 400, 400);
        //header_font.draw(batch, "Hovering over: " + DiscGame.hover.getClass(), 400, 500);

        // debug for phone resolution

        batch.end();

        // FFS, I can't use shape renderer for the bars inside batch.  So it needs to be done outside.
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawShapesCore();
        StateHandling.drawStateShapes(shapes);
        shapes.end();

        // I really hate not drawing all in one spritebatch block - but this hacky solution works for now
        batch.begin();
        StateHandling.drawStateBatch(batch);
        // TODO: Hacky way to hide this in the post game scenario
        if(StateHandling.currentState != State.PostGameResult) { drawBatchCoreTop(); }
        batch.end();

        // Need a separate shape and sprite batch for hovers
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shape_hover.drawShapeHover(shapes);
        shapes.end();

        batch.begin();
        // Assumption is that only one entity is allowed to be hovered over at any given moment
        hover.drawHover(batch);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        manager.dispose();
    }

    public void drawShapesCore() {
        /*
        player.draw_ethical(shapes);
        player.draw_logical(shapes);
        player.draw_interrogate(shapes);
        player.draw_intimidate(shapes);
        computer.draw_ethical(shapes);
        computer.draw_logical(shapes);
        computer.draw_interrogate(shapes);
        computer.draw_intimidate(shapes);
        */
    }

    public void drawBatchCore() {
        // Draw characters - order matters since hover states are affected
        player.portrait.draw(batch);
        computer.portrait.draw(batch);

        // Draw the board & Board topics

        // Adjust the order so that those transitioning underneath are below
        Cell sample_cell = DiscGame.current_board.cells[0][0];


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

        // TOPIC FOR DEBATE
        //header_font.draw(batch, "Resolved: That Prof. Elecantos should not horribly murder us and obliterate our souls.", screen_width/2 - 300, screen_height - 12);

        // Draw Deal Power
        //dealpower.draw(batch);
    }

    public void drawBatchCoreTop() {
        // Draw confidence/inspiration amounts
        player.draw_bar_counters(batch);
        computer.draw_bar_counters(batch);
    }

    public void setupPlayer() {
        player = new Socrates(true, BOARD_WIDTH, BOARD_HEIGHT, DESIRED_WIDTH, movestats_font, manager.get("img/socrates.png", Texture.class), manager.get("img/zhugemini.png", Texture.class));
    }

    public void setupOpponent() {
        computer = new Confucius(false, 1, 1, DESIRED_WIDTH, movestats_font, manager.get("img/confucius.png", Texture.class), manager.get("img/arlenemini.png", Texture.class));
    }

    public void setupFonts() {
        FreeTypeFontGenerator sinanova = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SinaNovaReg.otf"));
        FreeTypeFontGenerator ptsans = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PTSans.ttf"));
        FreeTypeFontGenerator nightmare = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Cinzel-Regular.ttf"));
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
    }

    public void loadDialog() {
        Yaml yaml = new Yaml();
        // TODO: Needs a try catch to pick up file read exceptions, including someone messing with the structure
        topics = new ArrayList();
        for (Object object : yaml.loadAll(Gdx.files.internal("dialog/dialog.yml").read())) {
            LinkedHashMap topic = (LinkedHashMap) object;
            topics.add(new Topic(topic.get("name"), topic.get("logical"), topic.get("ethical"), topic.get("interrogate"), topic.get("intimidate")));
        }
    }

    public void setupIcons() {
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
        movestats = manager.get("img/movestats.png", Texture.class);
    }

    // Create four new entities for dialog options whose role is solely to be a hover over/click handler and draw a bounding box
    public void setupDialogEntities() {
        dialog_options = new DialogOption[4];
        dialog_options[0] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[1] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[2] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
        dialog_options[3] = new DialogOption(DIALOG_X, DIALOG_Y, 355, 75);
    }

    public void setupEndgameOptions(ArrayList<EndGameOption> endgame_options) {
        endgame_options.add(new EndGameOption(2000, movestats_font,
                "Help us create a place in society rather than perpetuating this constant destruction. (Arlene joins the party)",
                "Help us create a place in society rather than perpetuating this constant destruction.",
                "How do you propose going about that?"));
        endgame_options.add(new EndGameOption(1000, movestats_font,
                "You should be more worried about your position than whether you can detain us. (+20 Initiative on Fleet Combat)",
                "You should be more worried about your position than whether you can detain us.",
                "So this was nothing more than a distraction.  We'll destroy you, regardless."));
        endgame_options.add(new EndGameOption(500, movestats_font,
                "Let us leave.  Pretend you never saw us.  We'll leave now and withdraw our forces.",
                "Let us leave.  Pretend you never saw us.  We'll leave now and withdraw our forces.",
                "This is not a good place to detain you, regardless.  Go ahead and run.  But you only delay the inevitable."));
        endgame_options.add(new EndGameOption(0, movestats_font,
                "Looks like this isn't going to end well.  We're leaving, and by force if necessary. (Initiate Fleet Combat)",
                "Looks like this isn't going to end well.  We're leaving, and by force if necessary.",
                "You can't just waltz out of here.  This sector has been on lockdown since we dropped out of tachyspace."));
        endgame_options.add(new EndGameOption(-9999, nightmare_font,
                "THE NIGHTMARES OF THE PAST CANNOT BE SO EASILY DEFEATED. (Initiate Squad Combat)",
                "THE NIGHTMARES OF THE PAST CANNOT BE SO EASILY DEFEATED.",
                "SO BE IT.  RETURN TO THE VOID, ABOMINATION."));




    }
}
