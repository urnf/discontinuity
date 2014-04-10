package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.discontinuous.discgame.Contestant;

/**
 * Created by Urk on 4/9/14.
 */
public class AbilityList {
    public static void init_abilities(Contestant contestant, BitmapFont font) {
    Ability strawman = new Ability(contestant, font, 64, 64, 40,
            AbilityTarget.targets.adjacent_square_fresh,
            new AbilityEffect(AbilityEffect.effects.multiply_all, 4, true),
            "~ Strawman ~ (Cost 40)\nConsume an unconsumed adjacent square for 4x the bonuses (DP, Cf+, Cf-, Ins+, Ins-)",
            "That's a horrible example.  What you failed to consider is the following situation...");
    strawman.setImg(new Texture(Gdx.files.internal("img/strawman.png")));
    contestant.abilities.add(strawman);

    // Ability tableflip
    Ability tableflip = new Ability(contestant, font, 64, 64, 30,
            AbilityTarget.targets.self,
            new AbilityEffect(AbilityEffect.effects.conf_damage, 60, false),
            "~ Tableflip ~ (Cost 30)\nFlip a table at your opponent, damaging your opponent's confidence by 60.",
            "Special case generated in Ability class, you should never see this.");
    tableflip.setImg(new Texture(Gdx.files.internal("img/tableflip.png")));
    contestant.abilities.add(tableflip);

    // Ability non sequitur
    Ability nonsequitur = new Ability(contestant, font, 64, 64, 50,
            AbilityTarget.targets.any_square,
            new AbilityEffect(AbilityEffect.effects.multiply_all, 1, true),
            "~ Non Sequitur ~ (Cost 50)\nDiscreetly move the conversation elsewhere; teleport to and consume any square.",
            "If you think about it, you're actually talking about something else, such as this.");
    nonsequitur.setImg(new Texture(Gdx.files.internal("img/nonsequitur.png")));
    contestant.abilities.add(nonsequitur);

    // Ability reasonable doubt - surrounding AoE opponent squares consumed
    Ability reasonable_doubt = new Ability(contestant, font, 64, 64, 40,
            AbilityTarget.targets.self,
            new AbilityEffect(AbilityEffect.effects.aoe_consume, 1, false),
            "~ Reasonable Doubt ~ (Cost 40)\nSow doubt and make your opponent's adjacent squares consumed.",
            "Are you sure about that?  I think you're making a bad assumption.");
    reasonable_doubt.setImg(new Texture(Gdx.files.internal("img/reasonabledoubt.png")));
    contestant.abilities.add(reasonable_doubt);

    // Ability double down - refresh and consume an adjacent consumed argument
    Ability double_down = new Ability(contestant,  font,64, 64, 60,
            AbilityTarget.targets.adjacent_square_consumed,
            new AbilityEffect(AbilityEffect.effects.refresh_consume, 1, true),
            "~ Double Down ~ (Cost 60)\nRefuse to be wrong and repeat an adjacent, consumed square without penalties.",
            "No.  Let me repeat it again, just slower and louder, until you understand.");
    double_down.setImg(new Texture(Gdx.files.internal("img/doubledown.png")));
    contestant.abilities.add(double_down);
    }

    public void AddAbilities(Contestant contestant, Ability[] AbilityList) {
        for (Ability ability : AbilityList) {

        }
    }
}
