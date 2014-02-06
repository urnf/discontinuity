package org.discontinuous.discgame;

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

        //Deduct the inspiration cost
        contestant.inspiration -= contestant.ability_selected.ins_cost;

        // Show bonus
        State.animation_counter = 0;
        State.animation_max = 30;

    }
}
