package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BasicPowerCardTest {
    BasicPowerCard card = new BasicPowerCard();
    List<Resource> desiredProductionResources = new ArrayList<>();

    //TODO: Come gestiamo risorse vuote o nulle?

    @Test
    @Order(1)
    void simplyCanDoProductionTest() throws NonStorableResourceException {
        desiredProductionResources.add(new OtherResource(ResourceEnum.GRAY));
        desiredProductionResources.add(new OtherResource(ResourceEnum.GRAY));
        desiredProductionResources.add(new OtherResource(ResourceEnum.YELLOW));
        assertTrue(card.canDoProduction(desiredProductionResources));
        simplyUsePowerTest(desiredProductionResources);
        getterTest(desiredProductionResources);
    }

    void simplyUsePowerTest(List<Resource> desiredProductionResources){
        assertEquals(desiredProductionResources.subList(2, 3), card.usePower());
    }

    void getterTest(List<Resource> desiredProductionResources){
        assertTrue(card.getInResources() == null && card.getOutResources().equals(desiredProductionResources.subList(2,3)));
    }

    @Test
    @Order(2)
    void invalidResourceForProduction() throws NonStorableResourceException {
        desiredProductionResources.add(new OtherResource(ResourceEnum.GRAY));
        desiredProductionResources.add(new OtherResource(ResourceEnum.YELLOW));
        assertFalse(card.canDoProduction(desiredProductionResources));
        desiredProductionResources.clear();
        desiredProductionResources.add(new RedResource());
        desiredProductionResources.add(new OtherResource(ResourceEnum.YELLOW));
        desiredProductionResources.add(new OtherResource(ResourceEnum.YELLOW));
        try {
            card.canDoProduction(desiredProductionResources);
        }catch (NonStorableResourceException e)
        {
            assertTrue(true);
        }

    }

}