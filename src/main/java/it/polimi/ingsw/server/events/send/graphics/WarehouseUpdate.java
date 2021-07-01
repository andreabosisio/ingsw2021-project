package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

/**
 * Contains all the data to Update the Client's Warehouse.
 */
public class WarehouseUpdate implements PersonalUpdate {
    @Override
    public void addUpdate(PersonalBoardUpdate personalBoardUpdate, Player player) {
        Warehouse warehouse = player.getPersonalBoard().getWarehouse();
        warehouse.reorderStrongBox();
        personalBoardUpdate.setWarehouse(warehouse.getAllPositionsAndResources());
        personalBoardUpdate.setNickname(player.getNickname());
    }
}
