package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.Hashtable;

/**
 * Created by Urk on 2/5/14.
 */
public class AbilityEffect {
    public enum effects {
        multiply_self_dp,
        multiply_self_conf,
        multiply_self_ins,
        multiply_all,
        conf_damage,
        conf_gain
    }

    int magnitude;
    effects effect;
    boolean move_to_cell;

    public AbilityEffect(effects effect, int magnitude, boolean move_to_cell) {
        this.effect = effect;
        this.magnitude = magnitude;
        this.move_to_cell = move_to_cell;
    }

    public void apply_effect(Contestant contestant, Cell cell){
        // Record current values of stats - player and opponent before effects
        State.previousPower = DiscGame.dealpower.dp;
        State.previousPlayerConf = DiscGame.yi.confidence;
        State.previousPlayerIns = DiscGame.yi.inspiration;
        State.previousOpponentConf = DiscGame.arlene.confidence;
        State.previousOpponentIns = DiscGame.arlene.inspiration;

        // Null protect, though I'm tempted to pull out to expose bugs
        if (null != cell && move_to_cell) {
            DiscGame.yi.update_only_position(cell);
        }

        // Deduct the inspiration cost
        contestant.inspiration -= contestant.ability_selected.ins_cost;

        // Apply the effect
        switch (effect) {
            case multiply_all:
                Hashtable<String, Integer> stats;
                switch (cell.type) {
                    case Logical: stats = contestant.log_stats; break;
                    case Ethical: stats = contestant.eth_stats; break;
                    case Interrogate: stats = contestant.ing_stats; break;
                    case Intimidate: stats = contestant.inm_stats; break;
                    // Will lead to exceptions - intentional
                    default: stats = null; break;
                }
                DiscGame.dealpower.update(stats.get("power") * magnitude, contestant.player, cell.consumed);
                contestant.confidence = Math.min(contestant.confidence + stats.get("conf_plus") * magnitude, contestant.conf_max);
                contestant.opponent.confidence = Math.max(contestant.opponent.confidence - stats.get("conf_minus") * magnitude, 0);
                contestant.inspiration = Math.min(contestant.inspiration + stats.get("ins_plus") * magnitude, contestant.insp_max);
                contestant.opponent.inspiration = Math.max(contestant.opponent.inspiration - stats.get("ins_minus") * magnitude, 0);
                break;
            case conf_damage:
                contestant.opponent.confidence = Math.max(contestant.opponent.confidence - magnitude, 0);
                break;
            default:
        }
        // Show bonus
        State.animation_counter = 0;
        State.animation_max = 30;

    }
}
