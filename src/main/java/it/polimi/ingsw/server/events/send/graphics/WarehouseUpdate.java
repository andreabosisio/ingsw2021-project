package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;

public class WarehouseUpdate implements PersonalUpdate{
    @Override
    public void addUpdateTo(PersonalBoardUpdate personalBoardUpdate, Player player) {
        Warehouse warehouse = player.getPersonalBoard().getWarehouse();
        warehouse.reorderStrongBox();

        personalBoardUpdate.setWarehouse(warehouse.getAllPositionsAndResources());
        personalBoardUpdate.setNickname(player.getNickname());
    }
}
