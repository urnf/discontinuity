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
        multiply_all,
        conf_damage,
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
                              int player_confidence,
                              int player_inspiration,
                              int opponent_confidence,
                              int opponent_inspiration,
                              Hashtable<String, Integer> log_stats,
                              Hashtable<String, Integer> eth_stats,
                              Hashtable<String, Integer> ing_stats,
                              Hashtable<String, Integer> inm_stats,
                              int conf_max,
                              int insp_max,
                              Cell opponent_cell,
                              Cell new_cell){
        // Null protect, though I'm tempted to pull out to expose bugs
        if (null != new_cell && move_to_cell) {
            contestant.update_only_position(new_cell);
        }

        // Deduct the inspiration cost
        player_inspiration -= ability_selected.ins_cost;

        int dp = 0;
        // Apply the effect
        switch (effect) {
            case multiply_all:
                Hashtable<String, Integer> stats;
                switch (type) {
                    case Logical: stats = log_stats; break;
                    case Ethical: stats = eth_stats; break;
                    case Interrogate: stats = ing_stats; break;
                    case Intimidate: stats = inm_stats; break;
                    // Will lead to exceptions - intentional
                    default: stats = null; break;
                }
                dp = stats.get("power") * magnitude;
                player_confidence = Math.min(player_confidence + stats.get("conf_plus") * magnitude, conf_max);
                opponent_confidence = Math.max(opponent_confidence - stats.get("conf_minus") * magnitude, 0);
                player_inspiration = Math.min(player_inspiration + stats.get("ins_plus") * magnitude, insp_max);
                opponent_inspiration = Math.max(opponent_inspiration - stats.get("ins_minus") * magnitude, 0);
                break;
            case conf_damage:
                opponent_confidence = Math.max(opponent_confidence - magnitude, 0);
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
        return_hash.put("dp", dp);
        return_hash.put("player_confidence", player_confidence);
        return_hash.put("player_inspiration", player_inspiration);
        return_hash.put("opponent_confidence", opponent_confidence);
        return_hash.put("opponent_inspiration", opponent_inspiration);
        return return_hash;
    }
}
