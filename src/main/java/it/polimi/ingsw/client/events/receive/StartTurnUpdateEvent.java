package it.polimi.ingsw.client.events.receive;

import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.cli.AnsiEnum;
import it.polimi.ingsw.client.view.cli.CLI;

public class StartTurnUpdateEvent implements ReceiveEvent{
    private String nextPlayer;
    @Override
    public void updateView(View view) {
        if(view.getNickname().equals(nextPlayer)){
            //fixme only for cli
            /*
            System.out.print(AnsiEnum.WHITE_BRIGHT + "Your turn is starting" + AnsiEnum.RESET);
            CLI.showThreePointsAnimation();

             */
            view.setOnYourTurn();
        }
        else {
            view.setOnWaitForYourTurn(nextPlayer);
        }
    }
}
