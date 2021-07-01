package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicPowerCardTest {
    final ModelInterface modelInterface = new ModelInterface(new ArrayList<>() {{
        add("pepo");
    }});
    final TurnLogic turnLogic = modelInterface.getTurnLogic();
    final BasicPowerCard card = new BasicPowerCard();
    final List<Resource> desiredProductionResources = new ArrayList<>();

    @Test
    @Order(1)
    void simplyCanDoProductionTest() {
        desiredProductionResources.add(new StorableResource(ResourcesEnum.GRAY));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.GRAY));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.YELLOW));
        assertTrue(card.canDoProduction(desiredProductionResources));
        getterTest(desiredProductionResources);
        simplyUsePowerTest();

        desiredProductionResources.clear();

        desiredProductionResources.add(new StorableResource(ResourcesEnum.PURPLE));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.GRAY));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.BLUE));
        assertTrue(card.canDoProduction(desiredProductionResources));
        getterTest(desiredProductionResources);
        simplyUsePowerTest();


        desiredProductionResources.clear();

        desiredProductionResources.add(null);
        desiredProductionResources.add(null);
        desiredProductionResources.add(null);
        assertFalse(card.canDoProduction(desiredProductionResources));

    }

    void simplyUsePowerTest(){
        assertTrue(card.usePower(turnLogic));
    }

    void getterTest(List<Resource> desiredProductionResources){
        assertTrue(card.getInResources() == null && card.getOutResources().equals(desiredProductionResources.subList(2,3)));
    }

    @Test
    @Order(2)
    void invalidResourceForProduction() {
        desiredProductionResources.add(new StorableResource(ResourcesEnum.GRAY));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.YELLOW));
        assertFalse(card.canDoProduction(desiredProductionResources));

        desiredProductionResources.clear();

        desiredProductionResources.add(new RedResource());
        desiredProductionResources.add(new StorableResource(ResourcesEnum.YELLOW));
        desiredProductionResources.add(new StorableResource(ResourcesEnum.YELLOW));

        //assertThrows(NonStorableResourceException.class, () -> card.canDoProduction(desiredProductionResources));

        desiredProductionResources.clear();

        assertFalse(card.canDoProduction(desiredProductionResources));

    }

    @Test
    void getVictoryPointsAndIDTest() {
        assertEquals(card.getID(),"basicPowerCard");
        assertEquals(card.getPoints(), 0);
    }

}