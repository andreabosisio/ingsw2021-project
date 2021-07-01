package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.player.warehouse.ExtraSlots;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExtraSlotsTest {

    final ExtraSlots extraSlots = new ExtraSlots();
    @Test
    void simplyExtraSlotTest() {
        Resource correctResource = new StorableResource(ResourcesEnum.YELLOW);

        assertFalse(extraSlots.isActivated());

        extraSlots.activateExtraSlots(correctResource);
        assertEquals(extraSlots.getResourceType(), correctResource);

        extraSlots.setResource(0, new StorableResource(ResourcesEnum.BLUE));
        assertFalse(extraSlots.isLegal());

        extraSlots.setResource(0, correctResource);
        assertTrue(extraSlots.isLegal());

        assertNull(extraSlots.getResource(1)); //empty slot
        assertNull(extraSlots.takeResource(1)); //empty slot

        assertEquals(extraSlots.getResource(0), correctResource);
        assertEquals(extraSlots.takeResource(0), correctResource);

        //after taking a resource, the slot is empty
        assertNull(extraSlots.getResource(0));
        assertNull(extraSlots.takeResource(0));

    }
}