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

    static int previousPlayerLog;
    static int previousPlayerEth;
    static int previousPlayerIng;
    static int previousPlayerInt;

    static int previousComputerLog;
    static int previousComputerEth;
    static int previousComputerIng;
    static int previousComputerInt;

    private static int conf_plus_x;
    private static int ins_plus_x;
    private static int conf_minus_x;
    private static int ins_minus_x;

    private static String logical_plus_string;
    private static String ethical_plus_string;
    private static String interrogate_plus_string;
    private static String intimidate_plus_string;

    private static String logical_minus_string;
    private static String ethical_minus_string;
    private static String interrogate_minus_string;
    private static String intimidate_minus_string;

    /*
    private static String conf_plus_string;
    private static String conf_minus_string;
    private static String ins_plus_string;
    private static String ins_minus_string;
    */

    public static int player_dialog_height_offset;
    public static int computer_dialog_height_offset;
    public static int dialog_width_offset =  DiscGame.screen_width/2 - 190;

    private static String player_text;
    private static String computer_text;
    public static EndGameOption selected_endgame_option;

    static boolean combo;

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
                    InDialog.drawBatch(batch, DiscGame.movestats_font, true, DiscGame.player.cell.player_dialog, "", dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                }
                else {
                    InDialog.drawBatch(batch, DiscGame.movestats_font, false, DiscGame.computer.cell.computer_dialog, "", dialog_width_offset, computer_dialog_height_offset, player_dialog_height_offset);
                }
                break;
            case SelectAbility:
                SelectAbility.drawBatch(batch, DiscGame.player.abilities, DiscGame.screen_width);
                break;
            case AbilityTargeting:
                AbilityTargeting.drawBatch(batch, DiscGame.movestats_font, player_text, dialog_width_offset, player_dialog_height_offset);
                break;
            case AbilityDialog:
                AbilityDialog.drawBatch(batch, DiscGame.player.ability_selected, dialog_width_offset, player_dialog_height_offset);
                break;
            case PostGameDialog:
                if (DiscGame.dealpower.dp >= 0) {
                    PostGameDialog.drawBatch(batch, DiscGame.movestats_font, true, "Why are we still talking?  I think it's time to resolve this.", "Definitely.", dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                }
                else {
                    PostGameDialog.drawBatch(batch, DiscGame.movestats_font, false, "This discussion is over.", "That seems so.", dialog_width_offset, computer_dialog_height_offset, player_dialog_height_offset);
                }
                break;
            case PostGameSelect:
                PostGameSelect.drawBatch(batch, DiscGame.movestats_font, DiscGame.endgame_options);
                break;
            case PostGameResult:
                PostGameResult.drawBatch(batch, selected_endgame_option, dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                //Redraw portraits on top of everything else
                DiscGame.player.portrait.draw(batch);
                DiscGame.computer.portrait.draw(batch);
                break;
        }

    }

    public static void advanceDialog(){
        if (currentSpeaker.player) {
            if (DiscGame.computer.stammering) {
                // Computer is stammering, allow player to move twice
                DiscGame.computer.stammering = false;
            }
            else {
                DiscGame.current_board.move_computer();
                return;
            }
        }
        else {
            if (DiscGame.player.stammering) {
                // Player is stammering, move computer twice before returning control
                DiscGame.player.stammering = false;
                DiscGame.current_board.move_computer();
            }
        }

        // For player character: update the new cells considered adjacent
        DiscGame.player.adjacent = DiscGame.player.cell.unoccupied_cells();
        DiscGame.player.update_dialog_options();
        DiscGame.player.update_abilities();

        // TODO: Need new game over code

        // Otherwise back to select dialog
        currentState = State.SelectDialog;
    }

    public static boolean checkState(State checkThis){
        if (currentState == checkThis) {return true;}
        return false;
    }

    public static void setState(State state) {
        currentState = state;
    }

    public static void animateGain(SpriteBatch batch, boolean playerSpeaking) {
        if (animation_counter <= animation_max * animation_coefficient) {
            DiscGame.animation_font.setColor(1f, 1f, 1f, 1 - ((float) animation_counter / (animation_coefficient * 100)));

            if (playerSpeaking) {
                conf_plus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 116;
                ins_plus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 76;
                conf_minus_x =  DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 44;
                ins_minus_x = DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 84;

                logical_plus_string = Integer.toString(DiscGame.player.logical_bar - previousPlayerLog);
                ethical_plus_string = Integer.toString(DiscGame.player.ethical_bar - previousPlayerEth);
                interrogate_plus_string = Integer.toString(DiscGame.player.interrogate_bar - previousPlayerIng);
                intimidate_plus_string = Integer.toString(DiscGame.player.intimidate_bar - previousPlayerInt);

                logical_minus_string = Integer.toString(DiscGame.computer.logical_bar - previousComputerLog);
                ethical_minus_string = Integer.toString(DiscGame.computer.ethical_bar - previousComputerEth);
                interrogate_minus_string = Integer.toString(DiscGame.computer.interrogate_bar - previousComputerIng);
                intimidate_minus_string = Integer.toString(DiscGame.computer.intimidate_bar - previousComputerInt);
            }
            else {
                conf_plus_x = DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 44;
                ins_plus_x =  DiscGame.screen_width/2 + (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) + 84;
                conf_minus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 116;
                ins_minus_x = DiscGame.screen_width/2 - (Board.CELL_EDGE_SIZE * DiscGame.BOARD_WIDTH/2) - 76;

                logical_plus_string = Integer.toString(DiscGame.computer.logical_bar - previousComputerLog);
                ethical_plus_string = Integer.toString(DiscGame.computer.ethical_bar - previousComputerEth);
                interrogate_plus_string = Integer.toString(DiscGame.computer.interrogate_bar - previousComputerIng);
                intimidate_plus_string = Integer.toString(DiscGame.computer.intimidate_bar - previousComputerInt);

                logical_minus_string = Integer.toString(DiscGame.player.logical_bar - previousPlayerLog);
                ethical_minus_string = Integer.toString(DiscGame.player.ethical_bar - previousPlayerEth);
                interrogate_minus_string = Integer.toString(DiscGame.player.interrogate_bar - previousPlayerIng);
                intimidate_minus_string = Integer.toString(DiscGame.player.intimidate_bar - previousPlayerInt);
            }
            DiscGame.animation_font.drawWrapped(batch, Integer.toString(DiscGame.dealpower.dp - previousPower), DiscGame.dealpower.x, DiscGame.screen_height/2 - 20 + animation_counter/animation_coefficient, 380);

            DiscGame.animation_font.drawWrapped(batch, logical_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, ethical_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, interrogate_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, intimidate_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);

            DiscGame.animation_font.drawWrapped(batch, logical_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, ethical_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, interrogate_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
            DiscGame.animation_font.drawWrapped(batch, intimidate_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);

            if (StateHandling.combo) {
                DiscGame.animation_font.drawWrapped(batch, "COMBO", currentSpeaker.cell.x, currentSpeaker.cell.y + 30 + animation_counter / animation_coefficient, 380);
            }
            animation_counter++;
        }
    }

    public static void set_player_offset(String text) {
        player_text = text;
        player_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(text, 350).height)/2));
    }

    public static void set_computer_offset(String text) {
        computer_text = text;
        computer_dialog_height_offset = 50 + (int) (((DiscGame.movestats_font.getWrappedBounds(text, 350).height)/2));
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
