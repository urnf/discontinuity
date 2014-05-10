package org.discontinuous.discgame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.discontinuous.discgame.states.game.*;

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
    public static int dialog_width_offset =  SympGame.DESIRED_WIDTH/2 - 190;

    private static String player_text;
    private static String computer_text;
    public static EndGameOption selected_endgame_option;

    static boolean combo;

    public static void drawShapes(ShapeRenderer shapes) {

    }

    public static void drawBatch(SpriteBatch batch) {

    }

    public static void drawGameShapes(ShapeRenderer shapes) {
        switch(currentState) {
            case SelectDialog:
                SelectDialog.drawShapes(shapes);
                break;
            case BoardTransition:
                break;
            case InDialog:
                if (currentSpeaker.player) {
                    InDialog.drawShapes(shapes, true, SympGame.DESIRED_WIDTH);
                }
                else {
                    InDialog.drawShapes(shapes, false, SympGame.DESIRED_WIDTH);
                }
                break;
            case SelectAbility:
                SelectAbility.drawShapes(shapes);
                break;
            case AbilityTargeting:
                AbilityTargeting.drawShapes(shapes, SympGame.DESIRED_WIDTH);
                break;
            case AbilityDialog:
                AbilityDialog.drawShapes(shapes, SympGame.DESIRED_WIDTH);
                break;
            case PostGameDialog:
                if (GameState.player.boards_won > GameState.computer.boards_won) {
                    PostGameDialog.drawShapes(shapes, true, SympGame.DESIRED_WIDTH);
                }
                else {
                    PostGameDialog.drawShapes(shapes, false, SympGame.DESIRED_WIDTH);
                }
                break;
            case PostGameSelect:
                PostGameSelect.drawShapes(shapes, GameState.endgame_options.size(), SympGame.DESIRED_WIDTH);
                break;
            case PostGameResult:
                PostGameResult.drawShapes(shapes, SympGame.DESIRED_WIDTH);
                break;
        }
    }

    public static void drawGameBatch(SpriteBatch batch) {
        switch(currentState) {
            case SelectDialog:
                SelectDialog.drawBatch(batch, GameState.dialog_options, SympGame.DESIRED_WIDTH);
                break;
            case InDialog:
                if (currentSpeaker.player) {
                    InDialog.drawBatch(batch, SympGame.movestats_font, true, GameState.player.cell.player_dialog, "", dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                }
                else {
                    InDialog.drawBatch(batch, SympGame.movestats_font, false, GameState.computer.cell.computer_dialog, "", dialog_width_offset, computer_dialog_height_offset, player_dialog_height_offset);
                }
                break;
            case SelectAbility:
                SelectAbility.drawBatch(batch, GameState.player.abilities, SympGame.screen_width);
                break;
            case AbilityTargeting:
                AbilityTargeting.drawBatch(batch, SympGame.movestats_font, player_text, dialog_width_offset, player_dialog_height_offset);
                break;
            case AbilityDialog:
                AbilityDialog.drawBatch(batch, GameState.player.ability_selected, dialog_width_offset, player_dialog_height_offset);
                break;
            case PostGameDialog:
                if (GameState.player.boards_won > GameState.computer.boards_won) {
                    PostGameDialog.drawBatch(batch, SympGame.movestats_font, true, "Why are we still talking?  I think it's time to resolve this.", "Definitely.", dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                }
                else {
                    PostGameDialog.drawBatch(batch, SympGame.movestats_font, false, "This discussion is over.", "That seems so.", dialog_width_offset, computer_dialog_height_offset, player_dialog_height_offset);
                }
                break;
            case PostGameSelect:
                PostGameSelect.drawBatch(batch, SympGame.movestats_font, GameState.endgame_options);
                break;
            case PostGameResult:
                PostGameResult.drawBatch(batch, selected_endgame_option, dialog_width_offset, player_dialog_height_offset, computer_dialog_height_offset);
                //Redraw portraits on top of everything else
                GameState.player.portrait.draw(batch);
                GameState.computer.portrait.draw(batch);
                break;
        }

        // TODO: Hacky way to hide this in the post game scenario
        if(currentState != State.PostGameResult) { GameState.drawBatchCoreTop(batch); }

    }

    public static void advanceDialog(){
        if (currentSpeaker.player) {
            if (GameState.computer.stammering) {
                // Computer is stammering, allow player to move twice
                GameState.computer.stammering = false;
            }
            else {
                GameState.current_board.move_computer();
                return;
            }
        }
        else {
            if (GameState.player.stammering) {
                // Player is stammering, move computer twice before returning control
                GameState.player.stammering = false;
                GameState.current_board.move_computer();
            }
        }

        // For player character: update the new cells considered adjacent
        GameState.player.adjacent = GameState.player.cell.unoccupied_cells();
        GameState.player.update_dialog_options();
        GameState.player.update_abilities();

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
/*
    public static void animateGain(SpriteBatch batch, boolean playerSpeaking) {
        if (animation_counter <= animation_max * animation_coefficient) {
            SympGame.animation_font.setColor(1f, 1f, 1f, 1 - ((float) animation_counter / (animation_coefficient * 100)));

            if (playerSpeaking) {
                conf_plus_x = SympGame.screen_width/2 - (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) - 116;
                ins_plus_x = SympGame.screen_width/2 - (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) - 76;
                conf_minus_x =  SympGame.screen_width/2 + (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) + 44;
                ins_minus_x = SympGame.screen_width/2 + (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) + 84;

                logical_plus_string = Integer.toString(SympGame.player.logical_bar - previousPlayerLog);
                ethical_plus_string = Integer.toString(SympGame.player.ethical_bar - previousPlayerEth);
                interrogate_plus_string = Integer.toString(SympGame.player.interrogate_bar - previousPlayerIng);
                intimidate_plus_string = Integer.toString(SympGame.player.intimidate_bar - previousPlayerInt);

                logical_minus_string = Integer.toString(SympGame.computer.logical_bar - previousComputerLog);
                ethical_minus_string = Integer.toString(SympGame.computer.ethical_bar - previousComputerEth);
                interrogate_minus_string = Integer.toString(SympGame.computer.interrogate_bar - previousComputerIng);
                intimidate_minus_string = Integer.toString(SympGame.computer.intimidate_bar - previousComputerInt);
            }
            else {
                conf_plus_x = SympGame.screen_width/2 + (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) + 44;
                ins_plus_x =  SympGame.screen_width/2 + (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) + 84;
                conf_minus_x = SympGame.screen_width/2 - (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) - 116;
                ins_minus_x = SympGame.screen_width/2 - (Board.CELL_EDGE_SIZE * SympGame.BOARD_WIDTH/2) - 76;

                logical_plus_string = Integer.toString(SympGame.computer.logical_bar - previousComputerLog);
                ethical_plus_string = Integer.toString(SympGame.computer.ethical_bar - previousComputerEth);
                interrogate_plus_string = Integer.toString(SympGame.computer.interrogate_bar - previousComputerIng);
                intimidate_plus_string = Integer.toString(SympGame.computer.intimidate_bar - previousComputerInt);

                logical_minus_string = Integer.toString(SympGame.player.logical_bar - previousPlayerLog);
                ethical_minus_string = Integer.toString(SympGame.player.ethical_bar - previousPlayerEth);
                interrogate_minus_string = Integer.toString(SympGame.player.interrogate_bar - previousPlayerIng);
                intimidate_minus_string = Integer.toString(SympGame.player.intimidate_bar - previousPlayerInt);
            }
            SympGame.animation_font.drawWrapped(batch, Integer.toString(SympGame.dealpower.dp - previousPower), SympGame.dealpower.x, SympGame.screen_height/2 - 20 + animation_counter/animation_coefficient, 380);

            SympGame.animation_font.drawWrapped(batch, logical_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, ethical_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, interrogate_plus_string, conf_plus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, intimidate_plus_string, ins_plus_x, 420 + animation_counter/animation_coefficient, 380);

            SympGame.animation_font.drawWrapped(batch, logical_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, ethical_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, interrogate_minus_string, conf_minus_x, 420 + animation_counter/animation_coefficient, 380);
            SympGame.animation_font.drawWrapped(batch, intimidate_minus_string, ins_minus_x, 420 + animation_counter/animation_coefficient, 380);

            if (StateHandling.combo) {
                SympGame.animation_font.drawWrapped(batch, "COMBO", currentSpeaker.cell.x, currentSpeaker.cell.y + 30 + animation_counter / animation_coefficient, 380);
            }
            animation_counter++;
        }
    }
    */

    public static void set_player_offset(String text) {
        player_text = text;
        player_dialog_height_offset = 50 + (int) (((SympGame.movestats_font.getWrappedBounds(text, 350).height)/2));
    }

    public static void set_computer_offset(String text) {
        computer_text = text;
        computer_dialog_height_offset = 50 + (int) (((SympGame.movestats_font.getWrappedBounds(text, 350).height)/2));
    }

    public static void setup_endgame_options() {
         int i = 0;
         for (EndGameOption option: GameState.endgame_options) {
             option.width = 428;
             option.height = 80;
             option.x = dialog_width_offset - 24;
             option.y = 585  - 100 * i;
             i++;
             // Entity has action on hover, add to hover list
             SympGame.hover_list.add(option);
             // Entity may have action on click, add to click list
             SympGame.click_list.add(option);
         }
    }
}
