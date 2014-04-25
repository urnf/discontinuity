package org.discontinuous.discgame.abilities;

import org.discontinuous.discgame.Cell;
import org.discontinuous.discgame.Contestant;
import org.discontinuous.discgame.DiscGame;
import org.discontinuous.discgame.StateHandling;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Urk on 2/5/14.
 */
public class AbilityEffect {
    public enum effects {
        multiply,
        damage,
        aoe_consume,
        refresh_consume
    }

    int magnitude;
    effects effect;
    boolean move_to_cell;

    public AbilityEffect(effects effect, int magnitude, boolean move_to_cell) {
        this.effect = effect;
        this.magnitude = magnitude;
        this.move_to_cell = move_to_cell;
    }

    public HashMap<String, Integer> apply_effect(Contestant contestant,
                              Ability ability_selected,
                              Cell.concepts type,
                              int player_logical_bar,
                              int player_ethical_bar,
                              int player_interrogate_bar,
                              int player_intimidate_bar,
                              int opponent_logical_bar,
                              int opponent_ethical_bar,
                              int opponent_interrogate_bar,
                              int opponent_intimidate_bar,
                              //Hashtable<String, Integer> log_stats,
                              //Hashtable<String, Integer> eth_stats,
                              //Hashtable<String, Integer> ing_stats,
                              //Hashtable<String, Integer> inm_stats,
                              int logical_max,
                              int ethical_max,
                              int interrogate_max,
                              int intimidate_max,
                              Cell opponent_cell,
                              Cell new_cell){
        // Null protect, though I'm tempted to pull out to expose bugs
        if (null != new_cell && move_to_cell) {
            contestant.update_only_position(new_cell);
        }

        // Deduct the ability cost
        player_logical_bar -= ability_selected.logical_cost;
        player_ethical_bar -= ability_selected.ethical_cost;
        player_interrogate_bar -= ability_selected.interrogate_cost;
        player_intimidate_bar -= ability_selected.intimidate_cost;

        int dp = 0;
        // Apply the effect
        switch (effect) {
            case multiply:
                Hashtable<String, Integer> stats;
                switch (type) {
                    case Logical:
                        player_logical_bar = Math.min(player_logical_bar + magnitude, logical_max);
                        break;
                    case Ethical:
                        player_ethical_bar = Math.min(player_ethical_bar + magnitude, ethical_max);
                        break;
                    case Interrogate:
                        player_interrogate_bar = Math.min(player_interrogate_bar + magnitude, interrogate_max);
                        break;
                    case Intimidate:
                        player_intimidate_bar = Math.min(player_intimidate_bar + magnitude, intimidate_max);
                        break;
                    /*
                    case Logical: stats = log_stats; break;
                    case Ethical: stats = eth_stats; break;
                    case Interrogate: stats = ing_stats; break;
                    case Intimidate: stats = inm_stats; break;
                    // Will lead to exceptions - intentional
                    default: stats = null; break;
                    */
                }
                // dp = stats.get("power") * magnitude;

                /*
                player_confidence = Math.min(player_confidence + stats.get("conf_plus") * magnitude, conf_max);
                opponent_confidence = Math.max(opponent_confidence - stats.get("conf_minus") * magnitude, 0);
                player_inspiration = Math.min(player_inspiration + stats.get("ins_plus") * magnitude, insp_max);
                opponent_inspiration = Math.max(opponent_inspiration - stats.get("ins_minus") * magnitude, 0);
                */
                break;
            case damage:
                // TODO: Implement stammer mechanic, lose turn for sub zero
                switch (type) {
                    case Logical:
                        opponent_logical_bar = Math.max(opponent_logical_bar - magnitude, 0);
                        break;
                    case Ethical:
                        opponent_ethical_bar = Math.max(opponent_ethical_bar - magnitude, 0);
                        break;
                    case Interrogate:
                        opponent_interrogate_bar = Math.max(opponent_interrogate_bar - magnitude, 0);
                        break;
                    case Intimidate:
                        opponent_intimidate_bar = Math.max(opponent_intimidate_bar - magnitude, 0);
                        break;
                }
                //opponent_confidence = Math.max(opponent_confidence - magnitude, 0);
                break;
            case aoe_consume:
                // Adjacent cells, not unoccupied cells, though no difference in game mechanics
                for (Cell adjacent_cell : opponent_cell.adjacent_cells()) {
                    adjacent_cell.consume();
                }
                break;
            case refresh_consume:
                contestant.refresh_consume_effect(new_cell);
                break;
            default:
        }
        HashMap<String, Integer> return_hash = new HashMap<String, Integer>();
        //return_hash.put("dp", dp);
        return_hash.put("player_logical_bar", player_logical_bar);
        return_hash.put("player_ethical_bar", player_ethical_bar);
        return_hash.put("player_interrogate_bar", player_interrogate_bar);
        return_hash.put("player_intimidate_bar", player_intimidate_bar);

        return_hash.put("opponent_logical_bar", opponent_logical_bar);
        return_hash.put("opponent_ethical_bar", opponent_ethical_bar);
        return_hash.put("opponent_interrogate_bar", opponent_interrogate_bar);
        return_hash.put("opponent_intimidate_bar", opponent_intimidate_bar);
        return return_hash;
    }
}
