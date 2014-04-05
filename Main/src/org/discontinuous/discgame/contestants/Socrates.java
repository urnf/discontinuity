package org.discontinuous.discgame.contestants;

import com.badlogic.gdx.Gdx;
import org.discontinuous.discgame.*;
import org.discontinuous.discgame.abilities.Ability;
import org.discontinuous.discgame.abilities.AbilityEffect;
import org.discontinuous.discgame.abilities.AbilityTarget;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Urk on 4/4/14.
 */
public class Socrates extends Contestant {
    // Setup Contestant stats
    static Hashtable<String, Integer> log_stats = new Hashtable<String, Integer>() {{
        put("power", 100);
        put("conf_plus", 25);
        put("conf_minus", 0);
        put("ins_plus", 10);
        put("ins_minus", 10);
    }};
    static Hashtable<String, Integer> eth_stats = new Hashtable<String, Integer>() {{
        put("power", 30);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 0);
        put("ins_minus", 20);
    }};
    static Hashtable<String, Integer> inm_stats = new Hashtable<String, Integer>() {{
        put("power", 50);
        put("conf_plus", 10);
        put("conf_minus", 20);
        put("ins_plus", 0);
        put("ins_minus", 0);
    }};
    static Hashtable<String, Integer> ing_stats = new Hashtable<String, Integer>() {{
        put("power", 70);
        put("conf_plus", 10);
        put("conf_minus", 0);
        put("ins_plus", 30);
        put("ins_minus", 0);
    }};

    public Socrates(  int coordinate,
                      boolean isPlayer,
                      Cell cell) {
        super(DiscGame.BOARD_WIDTH, DiscGame.BOARD_HEIGHT, log_stats, eth_stats, inm_stats, ing_stats, 100, 140, DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 120, isPlayer, cell);

        // Set up Socrates' combos
        ArrayList<String[]> combo_list = new  ArrayList<String[]>();
        combo_list.add(new String[]{"Intimidate", "Logical"});
        combo_list.add(new String[]{"Logical", "Ethical"});
        combo_list.add(new String[]{"Logical", "Interrogate"});
        combo_list.add(new String[]{"Ethical", "Interrogate"});
        combo_list.add(new String[]{"Interrogate", "Intimidate"});
        this.set_combo(new Combo(combo_list));

        //yi = new Contestant(combos, BOARD_WIDTH, BOARD_HEIGHT, log_stats, eth_stats, inm_stats, ing_stats, 100, 140, screen_width/2 - (Board.CELL_EDGE_SIZE * BOARD_WIDTH/2) - 120, true, current_board.cells[BOARD_WIDTH - 1][BOARD_HEIGHT - 1]);
        setImg(new Texture(Gdx.files.internal("img/zhugemini.png")));
        yi_portrait.setContestant(yi);
    }
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
            new AbilityEffect(AbilityEffect.effects.multiply_all, 1, true),
            "~ Non Sequitur ~ (Cost 50)\nDiscreetly move the conversation elsewhere; teleport to and consume any square.",
            "If you think about it, you're actually talking about something else, such as this.");
    nonsequitur.setImg(new Texture(Gdx.files.internal("img/nonsequitur.png")));
    yi.abilities.add(nonsequitur);

    // Ability reasonable doubt - surrounding AoE opponent squares consumed
    Ability reasonable_doubt = new Ability(yi, 64, 64, 40,
            AbilityTarget.targets.self,
            new AbilityEffect(AbilityEffect.effects.aoe_consume, 1, false),
            "~ Reasonable Doubt ~ (Cost 40)\nSow doubt and make your opponent's adjacent squares consumed.",
            "Are you sure about that?  I think you're making a bad assumption.");
    reasonable_doubt.setImg(new Texture(Gdx.files.internal("img/reasonabledoubt.png")));
    yi.abilities.add(reasonable_doubt);

    // Ability double down - refresh and consume an adjacent consumed argument
    Ability double_down = new Ability(yi, 64, 64, 60,
            AbilityTarget.targets.adjacent_square_consumed,
            new AbilityEffect(AbilityEffect.effects.refresh_consume, 1, true),
            "~ Double Down ~ (Cost 60)\nRefuse to be wrong and repeat an adjacent, consumed square without penalties.",
            "No.  Let me repeat it again, just slower and louder, until you understand.");
    double_down.setImg(new Texture(Gdx.files.internal("img/doubledown.png")));
    yi.abilities.add(double_down);

    Ability.setup_ability_display(yi);
}
