package org.discontinuous.discgame.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import org.discontinuous.discgame.Contestant;
import org.discontinuous.discgame.SympGame;

import java.util.ArrayList;

/**
 * Created by Urk on 4/9/14.
 */
public class AbilityList {
    public static void init_abilities(Contestant contestant, ArrayList<Ability> abilities, BitmapFont font) {
    Ability strawman = new Ability(contestant, font, 64, 64, 1, 1, 0, 0,
            AbilityTarget.targets.adjacent_square_fresh,
            new AbilityEffect(AbilityEffect.effects.multiply, 2, true),
            "~ Strawman ~\nConsume an unconsumed adjacent argument for 2x the points and gain.",
            "That's a horrible example.  What you failed to consider is the following situation...");
    strawman.setImg(SympGame.manager.get("img/strawman.png", Texture.class));
    abilities.add(strawman);

    // Ability tableflip
    Ability tableflip = new Ability(contestant, font, 64, 64, 0, 0, 1, 1,
            AbilityTarget.targets.self,
            new AbilityEffect(AbilityEffect.effects.damage, 3, false),
            "~ Tableflip ~\nFlip a table at your opponent, damaging your opponent's argument by 3x type you are on.",
            "I will now proceed to flip the everlasting shit out of this table here.");
    tableflip.setImg(SympGame.manager.get("img/tableflip.png", Texture.class));
    abilities.add(tableflip);

    // Ability non sequitur
    Ability nonsequitur = new Ability(contestant, font, 64, 64, 1, 1, 1, 1,
            AbilityTarget.targets.any_square,
            new AbilityEffect(AbilityEffect.effects.multiply, 1, true),
            "~ Non Sequitur ~\nDiscreetly move the conversation elsewhere; teleport to and consume any square.",
            "If you think about it, you're actually talking about something else, such as this.");
    nonsequitur.setImg(SympGame.manager.get("img/nonsequitur.png", Texture.class));
    abilities.add(nonsequitur);

    // Ability reasonable doubt - surrounding AoE opponent squares consumed
    Ability reasonable_doubt = new Ability(contestant, font, 64, 64, 1, 1, 1, 1,
            AbilityTarget.targets.self,
            new AbilityEffect(AbilityEffect.effects.aoe_consume, 1, false),
            "~ Reasonable Doubt ~\nSow doubt and make your opponent's adjacent squares consumed.",
            "Are you sure about that?  I think you're making a bad assumption.");
    reasonable_doubt.setImg(SympGame.manager.get("img/reasonabledoubt.png", Texture.class));
    abilities.add(reasonable_doubt);

    // Ability double down - refresh and consume an adjacent consumed argument
    Ability double_down = new Ability(contestant,  font,64, 64, 1, 0, 0, 1,
            AbilityTarget.targets.adjacent_square_consumed,
            new AbilityEffect(AbilityEffect.effects.refresh_consume, 1, true),
            "~ Double Down ~\nRefuse to be wrong and repeat an adjacent, consumed square without penalties.",
            "No.  Let me repeat it again, just slower and louder, until you understand.");
    double_down.setImg(SympGame.manager.get("img/doubledown.png", Texture.class));
    abilities.add(double_down);
    }

    public void AddAbilities(Contestant contestant, Ability[] AbilityList) {
        for (Ability ability : AbilityList) {

        }
    }
}
