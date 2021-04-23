package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.exceptions.NonStorableResourceException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.resources.OtherResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BasicPowerCardTest {
    ModelInterface modelInterface = new ModelInterface(new ArrayList<String>(){{
        add("pepo");
    }});
    TurnLogic turnLogic = modelInterface.getTurnLogic();
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
        getterTest(desiredProductionResources);
        simplyUsePowerTest();

        desiredProductionResources.clear();

        desiredProductionResources.add(new OtherResource(ResourceEnum.PURPLE));
        desiredProductionResources.add(new OtherResource(ResourceEnum.GRAY));
        desiredProductionResources.add(new OtherResource(ResourceEnum.BLUE));
        assertTrue(card.canDoProduction(desiredProductionResources));
        getterTest(desiredProductionResources);
        simplyUsePowerTest();


    }

    void simplyUsePowerTest(){
        assertTrue(card.usePower(turnLogic));
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

        assertThrows(NonStorableResourceException.class, () -> card.canDoProduction(desiredProductionResources));

        desiredProductionResources.clear();

        assertFalse(card.canDoProduction(desiredProductionResources));

    }

    @Test
    void getVictoryPointsTest() {
        assertEquals(card.getPoints(), 0);
    }
}