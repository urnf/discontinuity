package org.discontinuous.discgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Actual game code.  Pleased that unlike Slick, libgdx abstracts
 * the drawing to a higher level than OpenGL rendering, which will
 * be adequate given that I'm planning on working on a purely 2D plane
 */
public class DiscGame extends Game {
    // TODO: Too much casting.  I'm trying to write Java like I'm writing Ruby.  Clean up/reduce casting.
    // TODO: - Insults/Compliments
    // TODO: - DP spending
    // TODO: - Oral Swap Hyper Combos
    // TODO: - Fog of war

    // TODO: Giant pile of static variables.  OK for prototype, terrible design.
    static Board board;
    static Contestant yi;
    static Contestant arlene;
    Portrait yi_portrait;
    Portrait arlene_portrait;
    OrthographicCamera camera;
    SpriteBatch batch;
    ShapeRenderer shapes;
    Texture confidence_icon;
    Texture inspiration_icon;
    Icon confidence_icon_player;
    Icon confidence_icon_opponent;
    Icon inspiration_icon_player;
    Icon inspiration_icon_opponent;
    static AbilitiesButton abilities_button;
    static AI arlene_ai;

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

    static final int BOARD_WIDTH = 8;
    static final int BOARD_HEIGHT = 8;

    public void create() {
        screen_width = Gdx.graphics.getWidth();
        screen_height = Gdx.graphics.getHeight();

        //Load dialog
        loadDialog();

        // Setup list of entities which have an action on hover/click
        click_list = new ArrayList();
        hover_list = new ArrayList();
        // Separate hover list for shapes since it uses a different renderer
        shape_hover_list = new ArrayList();

        // Setup fonts
        setupFonts();

        // Setup camera and sprite batch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen_width, screen_height);
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        // Setup confidence/inspiration icons
        setupIcons();

        // Setup board and character entities - may need new entity subclass for characters
        // These are created and added to the hover list in order from top to bottom - so no need for layer.
        setupPortraits();
        board = new Board(BOARD_HEIGHT, BOARD_WIDTH);

        // Setup dialog option hovers
        setupDialogEntities();

        // Setup Deal Power counter
        dealpower = new DealPower();

        // Initialize stats for each contestant
        setupYi();
        setupArlene();

        // TODO: Need a better way of setting up cross references to each other, if we don't do this here there's a null ref
        yi.opponent = arlene;
        arlene.opponent = yi;
        yi.adjacent = yi.cell.find_adjacent_cells();
        yi.update_dialog_options();
        yi.update_abilities();

        // Setup AI class so Arlene doesn't wander randomly.
        arlene_ai = new AI(arlene, yi);

        // Setup ability button
        abilities_button = new AbilitiesButton(110, 380, 64, 64);
        abilities_button.setImg(new Texture(Gdx.files.internal("img/abilities.png")));

        DialogProcessor inputProcessor = new DialogProcessor();
        Gdx.input.setInputProcessor(inputProcessor);

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

        batch.end();

        // FFS, I can't use shape renderer for the bars inside batch.  So it needs to be done outside.
        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        drawShapesCore();
        State.drawStateShapes(shapes);
        shapes.end();

        // I really hate not drawing all in one spritebatch block - but this hacky solution works for now
        batch.begin();
        State.drawStateBatch(batch);
        drawBatchCoreTop();
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
    }

    public void drawShapesCore() {
        yi.draw_confidence(shapes);
        yi.draw_inspiration(shapes);
        arlene.draw_confidence(shapes);
        arlene.draw_inspiration(shapes);
    }

    public void drawBatchCore() {
        // Draw characters - order matters since hover states are affected
        yi_portrait.draw(batch);
        arlene_portrait.draw(batch);

        // Draw the board
        board.draw(batch, BOARD_WIDTH, BOARD_HEIGHT);

        // Draw confidence/inspiration icons
        confidence_icon_player.draw(batch);
        confidence_icon_opponent.draw(batch);
        inspiration_icon_player.draw(batch);
        inspiration_icon_opponent.draw(batch);

        // Draw contestants
        yi.draw(batch);
        arlene.draw(batch);

        // TOPIC FOR DEBATE
        header_font.draw(batch, "Resolved: That Prof. Elecantos should not horribly murder us and obliterate our souls.", screen_width/2 - 300, screen_height - 12);

        // Draw Deal Power
        dealpower.draw(batch);
    }

    public void drawBatchCoreTop() {
        // Draw confidence/inspiration amounts
        yi.draw_bar_counters(batch);
        arlene.draw_bar_counters(batch);
    }

    public void setupYi() {
        // Setup Contestant stats
        Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
            put("power", 100);
            put("conf_plus", 25);
            put("conf_minus", 0);
            put("ins_plus", 10);
            put("ins_minus", 10);
        }};
        Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
            put("power", 30);
            put("conf_plus", 10);
            put("conf_minus", 0);
            put("ins_plus", 0);
            put("ins_minus", 20);
        }};
        Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
            put("power", 50);
            put("conf_plus", 10);
            put("conf_minus", 20);
            put("ins_plus", 0);
            put("ins_minus", 0);
        }};
        Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
            put("power", 70);
            put("conf_plus", 10);
            put("conf_minus", 0);
            put("ins_plus", 30);
            put("ins_minus", 0);
        }};

        // Set up Yi's combos
        ArrayList<String []> yi_combo_list = new  ArrayList<String []>();
        yi_combo_list.add(new String[]{"Intimidate", "Logical"});
        yi_combo_list.add(new String[]{"Logical", "Ethical"});
        yi_combo_list.add(new String[]{"Logical", "Interrogate"});
        yi_combo_list.add(new String[]{"Ethical", "Interrogate"});
        yi_combo_list.add(new String[]{"Interrogate", "Intimidate"});
        Combo yi_combo = new Combo(yi_combo_list);

        yi = new Contestant(yi_combo, BOARD_WIDTH, BOARD_HEIGHT, log_stats, eth_stats, inm_stats, ing_stats, 100, 140, screen_width/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 120, true, board.cells[BOARD_WIDTH - 1][BOARD_HEIGHT - 1]);
        yi.setImg(new Texture(Gdx.files.internal("img/zhugemini.png")));
        //yi.img.scale((float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE - 1);
        yi_portrait.setContestant(yi);

        Ability strawman = new Ability(yi, 64, 64, 40,
                AbilityTarget.targets.adjacent_square_fresh,
                new AbilityEffect(AbilityEffect.effects.multiply_all, 4, true),
                "~ Strawman ~ (Cost 40)\nConsume an unconsumed adjacent square for 4x the bonuses (DP, Cf+, Cf-, Ins+, Ins-)",
                "That's a horrible example.  What you failed to consider is the following situation...");
        strawman.setImg(new Texture(Gdx.files.internal("img/strawman.png")));
        yi.abilities.add(strawman);

        // Ability tableflip
        Ability tableflip = new Ability(yi, 64, 64, 30,
                AbilityTarget.targets.self,
                new AbilityEffect(AbilityEffect.effects.conf_damage, 60, false),
                "~ Tableflip ~ (Cost 30)\nFlip a table at your opponent, damaging your opponent's confidence by 60.",
                "Special case generated in Ability class, you should never see this.");
        tableflip.setImg(new Texture(Gdx.files.internal("img/tableflip.png")));
        yi.abilities.add(tableflip);

        // Ability non sequitur
        Ability nonsequitur = new Ability(yi, 64, 64, 50,
                AbilityTarget.targets.any_square,
                new AbilityEffect(AbilityEffect.effects.multiply_all, 1, false),
                "~ Non Sequitur ~ (Cost 50)\nDiscreetly move the conversation elsewhere; teleport to and consume any square.",
                "If you think about it, you're actually talking about something else, such as this.");
        nonsequitur.setImg(new Texture(Gdx.files.internal("cell/ethical.jpg")));
        yi.abilities.add(nonsequitur);

        // Ability reasonable doubt - surrounding AoE opponent squares consumed
        Ability reasonable_doubt = new Ability(yi, 64, 64, 40,
                AbilityTarget.targets.self,
                new AbilityEffect(AbilityEffect.effects.aoe_consume, 1, false),
                "~ Reasonable Doubt ~ (Cost 40)\nSow doubt and make your opponent's adjacent squares consumed.",
                "Are you sure about that?  I think you're making a bad assumption.");
        reasonable_doubt.setImg(new Texture(Gdx.files.internal("cell/intimidate.jpg")));
        yi.abilities.add(reasonable_doubt);

        // Ability double down - refresh and consume an adjacent consumed argument
        Ability double_down = new Ability(yi, 64, 64, 60,
                AbilityTarget.targets.adjacent_square_consumed,
                new AbilityEffect(AbilityEffect.effects.refresh_consume, 1, true),
                "~ Double Down ~ (Cost 60)\nRefuse to be wrong and repeat an adjacent, consumed square without penalties.",
                "No.  Let me repeat it again, just slower and louder, until you understand.");
        double_down.setImg(new Texture(Gdx.files.internal("cell/logical.jpg")));
        yi.abilities.add(double_down);

        Ability.setup_ability_display(yi);
    }

    public void setupArlene() {
        Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
            put("power", 60);
            put("conf_plus", 0);
            put("conf_minus", 10);
            put("ins_plus", 10);
            put("ins_minus", 0);
        }};
        Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
            put("power", 40);
            put("conf_plus", 10);
            put("conf_minus", 0);
            put("ins_plus", 0);
            put("ins_minus", 0);
        }};
        Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
            put("power", 80);
            put("conf_plus", 0);
            put("conf_minus", 20);
            put("ins_plus", 0);
            put("ins_minus", 10);
        }};
        Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
            put("power", 120);
            put("conf_plus", 30);
            put("conf_minus", 20);
            put("ins_plus", 30);
            put("ins_minus", 10);
        }};

        // Set up Arlene's combos
        ArrayList<String []> arlene_combo_list = new  ArrayList<String []>();
        arlene_combo_list.add(new String[]{"Intimidate","Logical"});
        arlene_combo_list.add(new String[]{"Logical", "Ethical"});
        arlene_combo_list.add(new String[]{"Interrogate", "Ethical"});
        arlene_combo_list.add(new String[]{"Interrogate", "Logical"});
        arlene_combo_list.add(new String[]{"Interrogate", "Intimidate"});
        arlene_combo_list.add(new String[]{"Intimidate", "Ethical"});
        Combo arlene_combo = new Combo(arlene_combo_list);

        arlene = new Contestant(arlene_combo, 1, 1, log_stats, eth_stats, inm_stats, ing_stats, 200, 200, screen_width/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 40, false, board.cells[0][0]);
        arlene.setImg(new Texture(Gdx.files.internal("img/arlenemini.png")));
        //arlene.img.scale((float) Board.CELL_EDGE_SIZE/Board.TEXTURE_EDGE - 1);
        arlene_portrait.setContestant(arlene);
    }

    public void setupPortraits() {
        yi_portrait = new Portrait(new Texture(Gdx.files.internal("img/yi-combos.png")), 20, 0, 300, 375, screen_width/2 - 250, 700, 220, 250, 500, "Zhuge Yi\n" +
                "This proclaimed traveling businessman seems to have a surprising knack for methodical debate and inquiry.\n\n" +
                "His arguments are swift as the coursing river;\n" +
                "laid out with all the force of a great typhoon;\n" +
                "debated with all the strength of a raging fire;\n" +
                "and his next move is mysterious as the dark side of the moon.");
        yi_portrait.setImg(new Texture(Gdx.files.internal("img/zhugeyi.png")));
        arlene_portrait = new Portrait(new Texture(Gdx.files.internal("img/arlene-combos.png")), screen_width - 290,0, 300, 375, screen_width/2 - 250, 700, screen_width - 280, 250, 520, "Arlene Elecantos\n" +
                "J.D. University of New Oxford\n" +
                "Elecantos Legal Group\n" +
                "Professor Emeritus, Harvard Mars Law Adjunct\n\n" +
                "When she's not preparing for a major case or incinerating revenant souls, she relishes dishing out " +
                "verbal suplexes upon opposing counsel or unruly law students.  " +
                "Her fearsome reputation as both sorceror and lawyer means that few people cross her, professionally or personally.");
        arlene_portrait.setImg(new Texture(Gdx.files.internal("img/arlene.png")));
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
        dialog_font = ptsans.generateFont(14);
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
        String confidence = "Confidence \n At zero confidence, your opponent forces you to concede the match.  This results in the end of the game along with a 1000 DP penalty.";
        String inspiration = "Inspiration \n This enables you to use special debate techniques.  Using a special ability costs your turn, but you don't take backtracking penalties.";
        confidence_icon = new Texture(Gdx.files.internal("img/confidence.png"));
        inspiration_icon = new Texture(Gdx.files.internal("img/inspiration.png"));

        confidence_icon_player = new Icon(screen_width/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 115, screen_height - 65, 32, 32, screen_width/2 - 200, 600, 400, 80, "Your " + confidence);
        confidence_icon_player.setImg(confidence_icon);
        confidence_icon_opponent = new Icon(screen_width/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 45, screen_height - 67, 32, 32, screen_width/2 - 200, 600, 400, 80, "Opponent " + confidence);
        confidence_icon_opponent.setImg(confidence_icon);
        inspiration_icon_player = new Icon(screen_width/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 75, screen_height - 65, 32, 32, screen_width/2 - 200, 600, 400, 80, "Your " + inspiration);
        inspiration_icon_player.setImg(inspiration_icon);
        inspiration_icon_opponent = new Icon(screen_width/2 + (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) + 85, screen_height - 67, 32, 32, screen_width/2 - 200, 600, 400, 80, "Opponent " + inspiration);
        inspiration_icon_opponent.setImg(inspiration_icon);
        // Assign the movestats texture to be used generally in portraits
        movestats = new Texture(Gdx.files.internal("img/movestats.png"));
    }

    // Create four new entities for dialog options whose role is solely to be a hover over/click handler and draw a bounding box
    public void setupDialogEntities() {
        dialog_options = new DialogOption[4];
        dialog_options[0] = new DialogOption(DiscGame.screen_width/2 - 230, Tooltip.dialog_height + 150, 455, 55);
        dialog_options[1] = new DialogOption(DiscGame.screen_width/2 - 230, Tooltip.dialog_height + 95, 455, 55);
        dialog_options[2] = new DialogOption(DiscGame.screen_width/2 - 230, Tooltip.dialog_height + 40, 455, 55);
        dialog_options[3] = new DialogOption(DiscGame.screen_width/2 - 230, Tooltip.dialog_height - 15, 455, 55);
    }

    public void setupEndgameOptions(ArrayList<EndGameOption> endgame_options) {
        endgame_options.add(new EndGameOption(2000, movestats_font, "Help us create a place in society rather than perpetuating this constant destruction. (Arlene joins the party)"));
        endgame_options.add(new EndGameOption(1000, movestats_font, "You should be more worried about your position than whether you can detain us. (+20 Initiative on Fleet Combat)"));
        endgame_options.add(new EndGameOption(500, movestats_font, "Let us leave.  Pretend you never saw us.  We'll leave now and withdraw our forces."));
        endgame_options.add(new EndGameOption(0, movestats_font, "Looks like this isn't going to end well.  We're leaving, by force if necessary. (Initiate Fleet Combat)"));
        endgame_options.add(new EndGameOption(-9999, nightmare_font, "THE NIGHTMARES OF THE PAST CANNOT BE SO EASILY DEFEATED. (Initiate Tactical Combat)"));




    }
}
