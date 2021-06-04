package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

import java.util.ArrayList;

public class ProductionSlotsUpdate implements PersonalUpdate{
    @Override
    public void addUpdateTo(PersonalBoardUpdate personalBoardUpdate, Player player) {
        personalBoardUpdate.setNickname(player.getNickname());
        personalBoardUpdate.setProductionBoard(new ArrayList<>() {{
            add(player.getPersonalBoard().getVisibleDevelopmentCardsIDs());
        }});
    }
}
