package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Urk on 1/25/14.
 */
public class State {
    // singleton game state library, drawing state-specific UI elements and transitions to other states

    public enum states {
        SelectDialog, SelectAbility, AbilityTargeting, AbilityDialog, InDialog, PostGameDialog, PostGameSelect, PostGameResult
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
    public static int yi_dialog_height_offset;
    public static int arlene_dialog_height_offset;
    private static int dialog_width_offset =  DiscGame.screen_width/2 - 190;
    private static String yi_text;
    private static String arlene_text;
    public static EndGameOption selected_endgame_option;

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
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, DiscGame.screen_width - 270, 160, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                else {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, DiscGame.screen_width - 270, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, 270, 150, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                //
                break;
            case SelectAbility:
                Tooltip.drawDialogBox(shapes);
                break;
            case AbilityTargeting:
                Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, DiscGame.screen_width/2 - 180, 250, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                break;
            case AbilityDialog:
                Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, 200, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                break;
            // TODO: This is lazy.  Clean up the design, integrate with current states.
            case PostGameDialog:
                if (DiscGame.dealpower.dp >= 0) {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, 200, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, DiscGame.screen_width - 270, 160, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                else {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, DiscGame.screen_width - 270, 220, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, 270, 150, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                break;
            case PostGameSelect:
                for (int i = 0; i < DiscGame.endgame_options.size(); i++) {
                    Tooltip.newTip(DiscGame.screen_width/2 - 200, 600 - 100 * i, 400, 50, DiscGame.screen_width/2 - 200, 600 - 100 * i, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                }
                break;
            case PostGameResult:
                Tooltip.newTip(DiscGame.screen_width/2 - 200, 200, 400, 100, 260, 260, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                Tooltip.newTip(DiscGame.screen_width/2 - 200, 40, 400, 100, DiscGame.screen_width - 270, 160, Tooltip.dark_grey, Tooltip.light_grey, false, shapes);
                break;
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
                animateGain(batch, currentSpeaker.player);

                // If player speaking, print line chosen, centered in the box.
                if (currentSpeaker.player) {
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.yi_dialog, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.cell.arlene_resp_dialog, dialog_width_offset, 40 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
                }
                else {
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.arlene_dialog, dialog_width_offset, 200 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                    DiscGame.movestats_font.drawWrapped(batch, DiscGame.arlene.cell.yi_resp_dialog, dialog_width_offset, 40 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
                }
                break;
            case SelectAbility:
                for (Ability ability : DiscGame.yi.abilities) {
                    ability.draw(batch);
                }
                Tooltip.drawDialogWidgets(DiscGame.screen_width/2 - 230, 50, 450, 200, batch);
                break;
            case AbilityTargeting:
                DiscGame.movestats_font.drawWrapped(batch, yi_text, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
                break;
            case AbilityDialog:
                animateGain(batch, true);
                DiscGame.movestats_font.drawWrapped(batch, DiscGame.yi.ability_selected.dialog, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
                Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                break;
            case PostGameDialog:
                if (DiscGame.dealpower.dp >= 0) {
                    DiscGame.movestats_font.drawWrapped(batch, yi_text, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                    DiscGame.movestats_font.drawWrapped(batch, arlene_text, dialog_width_offset, 40 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
                }
                else {
                    DiscGame.movestats_font.drawWrapped(batch, arlene_text, dialog_width_offset, 200 + arlene_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                    DiscGame.movestats_font.drawWrapped(batch, yi_text, dialog_width_offset, 40 + yi_dialog_height_offset, 380);
                    Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
                }
                break;
            case PostGameSelect:
                for (EndGameOption option: DiscGame.endgame_options) {
                    DiscGame.movestats_font.drawWrapped(batch, Integer.toString(option.dp_cost), option.x + 10, option.y + 75, option.width - 40);
                    option.font.drawWrapped(batch, option.option_text, option.x + 70, option.y + 75, option.width - 70);
                }
                break;
            case PostGameResult:
                selected_endgame_option.font.drawWrapped(batch, yi_text, dialog_width_offset, 200 + yi_dialog_height_offset, 380);
                Tooltip.drawDialogWidgets(dialog_width_offset - 10, 200, 400, 100, batch);
                selected_endgame_option.font.drawWrapped(batch, arlene_text, dialog_width_offset, 40 + arlene_dialog_height_offset, 380);
                Tooltip.drawDialogWidgets(dialog_width_offset - 10, 40, 400, 100, batch);
                //Redraw portraits on top of everything else
                DiscGame.yi_portrait.draw(batch);
                DiscGame.arlene_portrait.draw(batch);
                break;
        }

    }

    public static void advanceDialog(){
        if (currentSpeaker.player) { DiscGame.board.move_arlene(); }
        else {
            // For player character: update the new cells considered adjacent
            DiscGame.yi.adjacent = DiscGame.yi.cell.unoccupied_cells();
            DiscGame.yi.update_dialog_options();
            DiscGame.yi.update_abilities();

            // Check to see if it's game over
            if (DiscGame.arlene.confidence <= 0) {
                DiscGame.dealpower.dp += 1000;
                yi_text = "Why are we still talking?  I think it's time to resolve this.";
                arlene_text = "Definitely.";
                set_yi_offset(yi_text);
                set_arlene_offset(arlene_text);
                currentState = states.PostGameDialog;
                return;
            }
            if (DiscGame.yi.confidence <= 0) {
                DiscGame.dealpower.dp -= 1000;
                arlene_text = "This discussion is over.";
                yi_text = "That seems so.";
                set_yi_offset(yi_text);
                set_arlene_offset(arlene_text);
                currentState = states.PostGameDialog;
                return;
            }

            // Otherwise back to select dialog
            currentState = states.SelectDialog;
        }
    }

    public static boolean checkState(states checkThis){
        if (currentState == checkThis) {return true;}
        return false;
    }

    private static void animateGain(SpriteBatch batch, boolean playerSpeaking) {
        if (animation_counter <= animation_max * animation_coefficient) {
            DiscGame.animation_font.setColor(1f, 1f, 1f, 1 - ((float) animation_counter / (animation_coefficient * 100)));

            if (playerSpeaking) {
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
            DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.dealpower.dp - previousPower), DiscGame.dealpower.x, DiscGame.screen_height/2 - 20 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, conf_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, ins_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, conf_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, ins_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);
            if (State.combo) {
                DiscGame.animation_font.drawWrapped(batch, "COMBO", currentSpeaker.cell.x, currentSpeaker.cell.y + 30 + animation_counter / animation_coefficient, 380);
            }
            animation_counter++;
        }
    }

    public static void set_yi_offset(String text) {
        yi_text = text;
        yi_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(text, 380).height)/2));
    }

    public static void set_arlene_offset(String text) {
        arlene_text = text;
        arlene_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(text, 380).height)/2));
    }
     public static void setup_endgame_options() {
         int i = 0;
         for (EndGameOption option: DiscGame.endgame_options) {
             option.width = 428;
             option.height = 80;
             option.x = dialog_width_offset - 24;
             option.y = 585  - 100 * i;
             i++;
             // Entity has action on hover, add to hover list
             DiscGame.hover_list.add(option);
             // Entity may have action on click, add to click list
             DiscGame.click_list.add(option);
         }
    }
}
