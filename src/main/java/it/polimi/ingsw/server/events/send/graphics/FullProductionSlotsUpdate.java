package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

public class FullProductionSlotsUpdate extends ProductionSlotsUpdate {

    @Override
    public void addUpdateTo(PersonalBoardUpdate personalBoardUpdate, Player player) {
        personalBoardUpdate.setNickname(player.getNickname());
        personalBoardUpdate.setProductionBoard(player.getPersonalBoard().getAllBoughtDevelopmentCardsIDs());
    }
}
