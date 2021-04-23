package it.polimi.ingsw.server.model.player;

import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    StrongBox strongBox = new StrongBox();

    @Test
    void simplyStrongBoxTest() throws EmptySlotException {

        Resource toStock = new OtherResource(ResourceEnum.PURPLE);
        strongBox.addResource(toStock);

        assertFalse(strongBox.setResource(2, toStock)); //cannot set a resource in a slot, only add

        assertNull(strongBox.getResource(1)); //empty slot
        try {
            strongBox.takeResource(1); //empty slot
        }catch (EmptySlotException e){
            assertTrue(true);
        }

        assertEquals(strongBox.getResource(0), toStock);
        assertEquals(strongBox.takeResource(0), toStock);

        //after taking a resource the slot is empty
        assertNull(strongBox.getResource(0));
        try {
            strongBox.takeResource(0); //empty slot
        }catch (EmptySlotException e){
            assertTrue(true);
        }

    }

}