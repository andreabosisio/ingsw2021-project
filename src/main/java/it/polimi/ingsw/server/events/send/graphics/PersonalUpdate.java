package it.polimi.ingsw.server.events.send.graphics;

import it.polimi.ingsw.server.model.player.Player;

public interface PersonalUpdate {
    void addUpdateTo(PersonalBoardUpdate personalBoardUpdate, Player player);
}
