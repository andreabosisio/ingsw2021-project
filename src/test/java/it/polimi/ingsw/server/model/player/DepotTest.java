package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.player.warehouse.Depot;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepotTest {

    final Depot depot = new Depot(3);
    @Test
    void simplyDepotTest() {
        Resource correctResource = new StorableResource(ResourcesEnum.BLUE);
        depot.setResource(2, correctResource);
        assertEquals(depot.getResourceType(), correctResource);
        assertNull(depot.getResource(1)); //empty slot
        assertNull(depot.takeResource(0)); //empty slot
        assertEquals(depot.getResource(2), correctResource);
        assertEquals(depot.takeResource(2), correctResource);

        //after taking the resource, the slot is empty
        assertNull(depot.getResource(2));
        assertNull(depot.takeResource(2));
        assertNull(depot.getResourceType(), (String) null);
    }
}