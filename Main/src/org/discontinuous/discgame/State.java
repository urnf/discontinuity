package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/25/14.
 */
public class State {
    // singleton game state library, drawing state-specific UI elements and transitions to other states

    public enum states {
        SelectDialog, InDialog, PreGame, PostGame
    }

    static states currentState = states.SelectDialog;
    static Contestant currentSpeaker;
    static int animation_max = 0;
    static int animation_counter = 0;
    // Higher value is divided into animation speed
    private static int animation_coefficient = 4; // TODO: Results in divide by zero error if zero, make more robust
    static int previousPower;
    static int previousPlayerConf;
    static int previousPlayerIns;
    static int previousOpponentConf;
    static int previousOpponentIns;
    private static int conf_plus_x;
    private static int ins_plus_x;
    private static int conf_minus_x;
    private static int ins_minus_x;
    static boolean combo;

    public static void drawStateShapes(ShapeRenderer shapes) {
        switch(currentState) {
            case SelectDialog:
                Tooltip.drawDialogTooltip(shapes);
                break;
            case InDialog:
                // Find out who is speaking
                if (currentSpeaker.player) {
                    Tooltip.newTip(300, 120, 400, 100, 200, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                else {
                    Tooltip.newTip(300, 120, 400, 100, 770, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                //
                break;
            case PreGame: break;
            case PostGame: break;
        }
    }

    public static void drawStateBatch(SpriteBatch batch) {
        switch(currentState) {
            case SelectDialog:
                // Draw Dialog Options which need to overlap the underlying element
                for (DialogOption option : DiscGame.dialog_options) {
                    option.drawDialogOption(batch);
                }
                break;
            case InDialog:
                if (animation_counter <= animation_max * animation_coefficient) {
                    DiscGame.animation_font.setColor(1f, 1f, 1f, 1 - ((float) animation_counter / (animation_coefficient * 100)));

                    //().toString
                    /*
                    switch (currentSpeaker.cell.type) {
                        case Logical:
                            conf_plus = "+" + currentSpeaker.log_stats.get("conf_plus");
                            ins_plus = "+" + currentSpeaker.log_stats.get("ins_plus");
                            conf_minus = "-" + currentSpeaker.log_stats.get("conf_minus");
                            ins_minus = "-" + currentSpeaker.log_stats.get("ins_minus");
                            break;
                        case Ethical:
                            conf_plus = "+" + currentSpeaker.eth_stats.get("conf_plus");
                            ins_plus = "+" + currentSpeaker.eth_stats.get("ins_plus");
                            conf_minus = "-" + currentSpeaker.eth_stats.get("conf_minus");
                            ins_minus = "-" + currentSpeaker.eth_stats.get("ins_minus");
                            break;
                        case Interrogate:
                            conf_plus = "+" + currentSpeaker.ing_stats.get("conf_plus");
                            ins_plus = "+" + currentSpeaker.ing_stats.get("ins_plus");
                            conf_minus = "-" + currentSpeaker.ing_stats.get("conf_minus");
                            ins_minus = "-" + currentSpeaker.ing_stats.get("ins_minus");
                            break;
                        case Intimidate:
                            conf_plus = "+" + currentSpeaker.inm_stats.get("conf_plus");
                            ins_plus = "+" + currentSpeaker.inm_stats.get("ins_plus");
                            conf_minus = "-" + currentSpeaker.inm_stats.get("conf_minus");
                            ins_minus = "-" + currentSpeaker.inm_stats.get("ins_minus");
                            break;
                    }*/
                    if (currentSpeaker.player) {
                        conf_plus_x = 80;
                        ins_plus_x = 122;
                        conf_minus_x = 844;
                        ins_minus_x = 886;
                    }
                    else {
                        conf_plus_x = 844;
                        ins_plus_x = 886;
                        conf_minus_x = 80;
                        ins_minus_x = 122;
                    }
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.dealpower.dp - previousPower), 482, 295 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.yi.confidence - previousPlayerConf), conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.yi.inspiration - previousPlayerIns), ins_plus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.arlene.confidence - previousOpponentConf), conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.arlene.inspiration - previousOpponentIns), ins_minus_x, 420 + animation_counter/animation_coefficient, 380);
                    if (State.combo) {
                        DiscGame.animation_font.drawWrapped(batch, "COMBO", currentSpeaker.cell.x, currentSpeaker.cell.y + 30 + animation_counter / animation_coefficient, 380);
                    }
                    animation_counter++;
                }

                // If player speaking, print line chosen, centered in the box.
                // TODO : This is calculated on render at 60Hz.  That's crap.  Move out into Contestant and calculate once there.
                if (currentSpeaker.player) {
                    int height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.yi.cell.yi_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.yi_dialog, 310, 125 + height_offset, 380);
                }
                else {
                    int height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.arlene.cell.arlene_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.arlene_dialog, 310, 125 + height_offset, 380);
                }
                //
                break;
            case PreGame: break;
            case PostGame: break;
        }

    }

    public static void advanceDialog(){
        if (currentSpeaker.player) { DiscGame.board.move_arlene(); }
        else {
            // For player character: update the new cells considered adjacent
            DiscGame.yi.adjacent = DiscGame.yi.cell.find_adjacent_cells();
            DiscGame.yi.update_dialog_options();
            // Back to select dialog
            currentState = states.SelectDialog;
        }
    }

    public static boolean checkState(states checkThis){
        if (currentState == checkThis) {return true;}
        return false;
    }
}
