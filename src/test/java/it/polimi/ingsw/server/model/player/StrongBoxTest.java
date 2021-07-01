package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.player.warehouse.StrongBox;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    final StrongBox strongBox = new StrongBox();

    @Test
    void simplyStrongBoxTest() throws EmptySlotException {

        Resource toStock = new StorableResource(ResourcesEnum.PURPLE);
        strongBox.addResource(toStock);

        assertFalse(strongBox.setResource(2, toStock)); //cannot set a resource in a slot, only add

        assertNull(strongBox.getResource(1)); //empty slot

        assertThrows(EmptySlotException.class, () -> strongBox.takeResource(1)); //empty slot

        assertEquals(strongBox.getResource(0), toStock);
        assertEquals(strongBox.takeResource(0), toStock);

        //after taking a resource the slot is empty
        assertNull(strongBox.getResource(0));

        assertThrows(EmptySlotException.class, () -> strongBox.takeResource(0)); //empty slot

    }

}