package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {
    TurnLogic turnLogic = new TurnLogic(new ArrayList<Player>(){{
        add(new Player("Pluto"));
    }});
    @Test
    void productionAbility() {
        assertFalse(new WhiteResource().productionAbility(turnLogic));
    }

    @Test
    void addPossibleTransformation() {
        assertFalse(new RedResource().addPossibleTransformation(new OtherResource(ResourceEnum.PURPLE)));
        assertFalse(new OtherResource(ResourceEnum.BLUE).addPossibleTransformation(new OtherResource(ResourceEnum.YELLOW)));
    }

    @Test
    void equals() {
        assertNotEquals(new OtherResource(ResourceEnum.PURPLE), turnLogic);
    }
}