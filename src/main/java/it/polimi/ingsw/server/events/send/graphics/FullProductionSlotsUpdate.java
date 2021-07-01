package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

/**
 * Contains all the data of all the owned Production Cards.
 */
public class FullProductionSlotsUpdate extends ProductionSlotsUpdate {

    @Override
    public void addUpdate(PersonalBoardUpdate personalBoardUpdate, Player player) {
        personalBoardUpdate.setNickname(player.getNickname());
        personalBoardUpdate.setProductionBoard(player.getPersonalBoard().getAllBoughtDevelopmentCardsIDs());
    }
}
