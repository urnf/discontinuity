package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.states.*;

/**
 * Created by Urk on 1/25/14.
 */
public class StateHandling {
    // singleton game state library, drawing state-specific UI elements and transitions to other states

    public enum State {
        SelectDialog, BoardTransition, SelectAbility, AbilityTargeting, AbilityDialog, InDialog, PostGameDialog, PostGameSelect, PostGameResult
    }

    static State currentState = State.SelectDialog;
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
    public static int dialog_width_offset =  DiscGame.screen_width/2 - 190;

    private static String yi_text;
    private static String arlene_text;
    public static EndGameOption selected_endgame_option;

    static boolean combo;

    static Color inner_color = Colors.ColorMap.get("dark_grey");
    static Color outer_color = Colors.ColorMap.get("light_grey");

    public static void drawStateShapes(ShapeRenderer shapes) {
        switch(currentState) {
            case SelectDialog:
                SelectDialog.drawShapes(shapes);
                break;
            case BoardTransition:
                break;
            case InDialog:
                if (currentSpeaker.player) {
                    InDialog.drawShapes(shapes, true, DiscGame.screen_width);
                }
                else {
                    InDialog.drawShapes(shapes, false, DiscGame.screen_width);
                }
                break;
            case SelectAbility:
                SelectAbility.drawShapes(shapes);
                break;
            case AbilityTargeting:
                AbilityTargeting.drawShapes(shapes, DiscGame.screen_width);
                break;
            case AbilityDialog:
                AbilityDialog.drawShapes(shapes, DiscGame.screen_width);
                break;
            case PostGameDialog:
                if (DiscGame.dealpower.dp >= 0) {
                    PostGameDialog.drawShapes(shapes, true, DiscGame.screen_width);
                }
                else {
                    PostGameDialog.drawShapes(shapes, false, DiscGame.screen_width);
                }
                break;
            case PostGameSelect:
                PostGameSelect.drawShapes(shapes, DiscGame.endgame_options.size(), DiscGame.screen_width);
                break;
            case PostGameResult:
                PostGameResult.drawShapes(shapes, DiscGame.screen_width);
                break;
        }
    }

    public static void drawStateBatch(SpriteBatch batch) {
        switch(currentState) {
            case SelectDialog:
                SelectDialog.drawBatch(batch, DiscGame.dialog_options, DiscGame.screen_width);
                break;
            case InDialog:
                if (currentSpeaker.player) {
                    InDialog.drawBatch(batch, DiscGame.movestats_font, true, DiscGame.yi.cell.yi_dialog, DiscGame.yi.cell.arlene_resp_dialog, dialog_width_offset, yi_dialog_height_offset, arlene_dialog_height_offset);
                }
                else {
                    InDialog.drawBatch(batch, DiscGame.movestats_font, false, DiscGame.arlene.cell.arlene_dialog, DiscGame.arlene.cell.yi_resp_dialog, dialog_width_offset, arlene_dialog_height_offset, yi_dialog_height_offset);
                }
                break;
            case SelectAbility:
                SelectAbility.drawBatch(batch, DiscGame.yi, DiscGame.screen_width);
                break;
            case AbilityTargeting:
                AbilityTargeting.drawBatch(batch, DiscGame.movestats_font, yi_text, dialog_width_offset, yi_dialog_height_offset);
                break;
            case AbilityDialog:
                AbilityDialog.drawBatch(batch, DiscGame.movestats_font, DiscGame.yi.ability_selected.dialog, dialog_width_offset, yi_dialog_height_offset);
                break;
            case PostGameDialog:
                if (DiscGame.dealpower.dp >= 0) {
                    PostGameDialog.drawBatch(batch, DiscGame.movestats_font, true, "Why are we still talking?  I think it's time to resolve this.", "Definitely.", dialog_width_offset, yi_dialog_height_offset, arlene_dialog_height_offset);
                }
                else {
                    PostGameDialog.drawBatch(batch, DiscGame.movestats_font, false, "This discussion is over.", "That seems so.", dialog_width_offset, arlene_dialog_height_offset, yi_dialog_height_offset);
                }
                break;
            case PostGameSelect:
                PostGameSelect.drawBatch(batch, DiscGame.movestats_font, DiscGame.endgame_options);
                break;
            case PostGameResult:
                PostGameResult.drawBatch(batch, selected_endgame_option, dialog_width_offset, yi_dialog_height_offset, arlene_dialog_height_offset);
                //Redraw portraits on top of everything else
                DiscGame.yi_portrait.draw(batch);
                DiscGame.arlene_portrait.draw(batch);
                break;
        }

    }

    public static void advanceDialog(){
        if (currentSpeaker.player) { DiscGame.current_board.move_arlene(); }
        else {
            // For player character: update the new cells considered adjacent
            DiscGame.yi.adjacent = DiscGame.yi.cell.unoccupied_cells();
            DiscGame.yi.update_dialog_options();
            DiscGame.yi.update_abilities();

            // Check to see if it's game over
            if (DiscGame.arlene.confidence <= 0) {
                DiscGame.dealpower.dp += 1000;
                set_yi_offset(yi_text);
                set_arlene_offset(arlene_text);
                currentState = State.PostGameDialog;
                return;
            }
            if (DiscGame.yi.confidence <= 0) {
                DiscGame.dealpower.dp -= 1000;
                set_yi_offset(yi_text);
                set_arlene_offset(arlene_text);
                currentState = State.PostGameDialog;
                return;
            }

            // Otherwise back to select dialog
            currentState = State.SelectDialog;
        }
    }

    public static boolean checkState(State checkThis){
        if (currentState == checkThis) {return true;}
        return false;
    }

    public static void animateGain(SpriteBatch batch, boolean playerSpeaking) {
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
            if (StateHandling.combo) {
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
