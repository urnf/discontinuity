package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/25/14.
 */
public class State {
    // singleton game state library, drawing state-specific UI elements and transitions to other states

    public enum states {
        SelectDialog, SelectAbility, InDialog, PreGame, PostGame
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
    private static String conf_plus_string;
    private static String conf_minus_string;
    private static String ins_plus_string;
    private static String ins_minus_string;
    private static int yi_dialog_height_offset;
    private static int arlene_dialog_height_offset;

    static boolean combo;

    public static void drawStateShapes(ShapeRenderer shapes) {
        switch(currentState) {
            case SelectDialog:
                Tooltip.drawDialogBox(shapes);
                break;
            case InDialog:
                // Find out who is speaking
                if (currentSpeaker.player) {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, 200, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, DiscGame.screen_width - 270, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                else {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, DiscGame.screen_width - 270, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, 200, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
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
                Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 230, 50, 450, 200, batch);
                break;
            case InDialog:
                if (animation_counter <= animation_max * animation_coefficient) {
                    DiscGame.animation_font.setColor(1f, 1f, 1f, 1 - ((float) animation_counter / (animation_coefficient * 100)));

                    if (currentSpeaker.player) {
                        conf_plus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 116;
                        ins_plus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 76;
                        conf_minus_x =  DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 44;
                        ins_minus_x = DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 84;
                        conf_plus_string = Integer.toString(DiscGame.yi.confidence - previousPlayerConf);
                        ins_plus_string = Integer.toString(DiscGame.yi.inspiration - previousPlayerIns);
                        conf_minus_string = Integer.toString(DiscGame.arlene.confidence - previousOpponentConf);
                        ins_minus_string = Integer.toString(DiscGame.arlene.inspiration - previousOpponentIns);
                    }
                    else {
                        conf_plus_x = DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 44;
                        ins_plus_x =  DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 84;
                        conf_minus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 116;
                        ins_minus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 76;
                        conf_plus_string = Integer.toString(DiscGame.arlene.confidence - previousOpponentConf);
                        ins_plus_string = Integer.toString(DiscGame.arlene.inspiration - previousOpponentIns);
                        conf_minus_string = Integer.toString(DiscGame.yi.confidence - previousPlayerConf);
                        ins_minus_string = Integer.toString(DiscGame.yi.inspiration - previousPlayerIns);
                    }
                    DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.dealpower.dp - previousPower), DiscGame.dealpower.x, 325 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, conf_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, ins_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, conf_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
                    DiscGame.animation_font.drawWrapped(batch, ins_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);
                    if (State.combo) {
                        DiscGame.animation_font.drawWrapped(batch, "COMBO", currentSpeaker.cell.x, currentSpeaker.cell.y + 30 + animation_counter / animation_coefficient, 380);
                    }
                    animation_counter++;
                }

                // If player speaking, print line chosen, centered in the box.
                // TODO : This is calculated on render at 60Hz.  That's crap.  Move out into Contestant and calculate once there.
                if (currentSpeaker.player) {
                    yi_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.yi.cell.yi_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.yi_dialog, DiscGame.screen_width/2 - 190, 200 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 200, 200, 400, 100, batch);
                    arlene_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.yi.cell.arlene_resp_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.arlene_resp_dialog, DiscGame.screen_width/2 - 190, 40 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 200, 40, 400, 100, batch);
                }
                else {
                    arlene_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.arlene.cell.arlene_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.arlene_dialog, DiscGame.screen_width/2 - 190, 200 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 200, 200, 400, 100, batch);
                    yi_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(DiscGame.arlene.cell.yi_resp_dialog, 380).height)/2));
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.yi_resp_dialog, DiscGame.screen_width/2 - 190, 40 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 200, 40, 400, 100, batch);
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
