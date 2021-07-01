package it.polimi.ingsw.server.model.resources;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.NonStorableResourceException;
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
        assertFalse(new RedResource().addPossibleTransformation(new StorableResource(ResourcesEnum.PURPLE)));
        assertFalse(new StorableResource(ResourcesEnum.BLUE).addPossibleTransformation(new StorableResource(ResourcesEnum.YELLOW)));
    }

    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @Test
    void equals() {
        assertNotEquals(new StorableResource(ResourcesEnum.PURPLE), turnLogic);
    }

    @Test
    void constructor() throws NonStorableResourceException {
        assertEquals(ResourceFactory.produceResource(ResourcesEnum.GRAY), new StorableResource(ResourcesEnum.GRAY));

        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourcesEnum.RED));
        assertThrows(NonStorableResourceException.class, () -> ResourceFactory.produceResource(ResourcesEnum.WHITE));
    }
}