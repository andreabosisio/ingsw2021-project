package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.server.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTest {
    final TurnLogic turnLogic = new TurnLogic(new ArrayList<>() {{
        add(new Player("Pluto"));
    }},null);
    @Test
    void productionAbility() {
        assertFalse(new WhiteResource().productionAbility(turnLogic));
    }

    @Test
    void addPossibleTransformation() {
        assertFalse(new RedResource().addPossibleTransformation(new StorableResource(ResourceEnum.PURPLE)));
        assertFalse(new StorableResource(ResourceEnum.BLUE).addPossibleTransformation(new StorableResource(ResourceEnum.YELLOW)));
    }

    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @Test
    void equals() {
        assertNotEquals(new StorableResource(ResourceEnum.PURPLE), turnLogic);
    }

    @Test
    void constructor() throws NonStorableResourceException {
        assertEquals(ResourceFactory.produceResource(ResourceEnum.GRAY), new StorableResource(ResourceEnum.GRAY));

        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourceEnum.RED));
        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourceEnum.WHITE));
    }
}