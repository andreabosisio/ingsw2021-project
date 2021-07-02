package it.polimi.ingsw.server.model.cards;


import it.polimi.ingsw.commons.enums.CardColorsEnum;
import it.polimi.ingsw.commons.enums.ResourcesEnum;
import it.polimi.ingsw.server.exceptions.EmptySlotException;
import it.polimi.ingsw.server.exceptions.InvalidIndexException;
import it.polimi.ingsw.server.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.player.warehouse.Warehouse;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardTest {

    final CardsGenerator devCardGenerator = new CardsGenerator();
    final ModelInterface modelInterface = new ModelInterface(new ArrayList<>() {{
        add("Simone");
        add("Andrea");
    }});
    final TurnLogic turnLogic = modelInterface.getTurnLogic();
    final Warehouse warehouse = turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse();
    final List<DevelopmentCard> devCards = devCardGenerator.generateDevelopmentCards();
    DevelopmentCard devCard;

    /**
     * Verify that the outResources of the card aren't mutable
     */
    @Test
     void getOutResourcesTest () {
        List<Resource> getOutResources = devCards.get(0).getOutResources();
        getOutResources.remove(0);
        assertFalse(devCards.get(0).getOutResources().isEmpty());
    }

    @Test
     void simplyCanDoProductionTest() {
        devCard = devCards.get(0);
        List<Resource> correctResources = new ArrayList<>();
        List<Resource> insufficientResources = new ArrayList<>();
        List<Resource> incorrectResources = new ArrayList<>();

        correctResources.add(new StorableResource(ResourcesEnum.GRAY));
        correctResources.add(new StorableResource(ResourcesEnum.GRAY));

        insufficientResources.add(new StorableResource(ResourcesEnum.GRAY));

        incorrectResources.add(new StorableResource(ResourcesEnum.PURPLE));
        incorrectResources.add(new StorableResource(ResourcesEnum.GRAY));


        assertTrue(devCard.canDoProduction(correctResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(insufficientResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(incorrectResources) && !devCard.getInResources().isEmpty());
    }

    @Test
    void simplyCanDoProductionTest2() {
        devCard = devCards.get(8);
        List<Resource> correctResources = new ArrayList<>();
        List<Resource> insufficientResources = new ArrayList<>();
        List<Resource> incorrectResources = new ArrayList<>();

        correctResources.add(new StorableResource(ResourcesEnum.GRAY));
        correctResources.add(new StorableResource(ResourcesEnum.BLUE));

        insufficientResources.add(new StorableResource(ResourcesEnum.BLUE));

        incorrectResources.add(new StorableResource(ResourcesEnum.PURPLE));
        incorrectResources.add(new StorableResource(ResourcesEnum.BLUE));


        assertTrue(devCard.canDoProduction(correctResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(insufficientResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(incorrectResources) && !devCard.getInResources().isEmpty());

        //devCards.get(8).getPrice().remove(0);
    }

    @Test
    void getterTest(){
            devCard = devCards.get(19);
        List<Resource> correctPrice = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.BLUE));
            add(new StorableResource(ResourcesEnum.BLUE));
        }};
        List<Resource> correctInResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
        }};
        List<Resource> correctOutResources = new ArrayList<>() {{
            add(new RedResource());
        }};
        assertEquals(correctPrice, devCard.getPrice());
        assertEquals(CardColorsEnum.GREEN, devCard.getColor());
        assertEquals(1, devCard.getLevel());
        assertEquals(1, devCard.getPoints());
        assertTrue(devCard.getOutResources().equals(correctOutResources) && devCard.getInResources().equals(correctInResources));
    }

    @Test
    void usePowerTest(){
        devCard = devCards.get(9);
        List<Resource> correctOutResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource((ResourcesEnum.PURPLE)));
            add(new RedResource());
        }};
        assertEquals(correctOutResources, devCard.getOutResources());
        assertTrue(devCard.usePower(turnLogic));
    }

    @Test
    void buyCardTest() throws InvalidIndexException, EmptySlotException, NonAccessibleSlotException {
        devCard = devCards.get(14);
        List<Integer> paymentResourcePositions;
        List<Resource> discount;

        //----------------

        List<Resource> strongBoxResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.GRAY));
        }};
        warehouse.addResourcesToStrongBox(strongBoxResources);

        discount = new ArrayList<>();

        //correct resources for payment
        paymentResourcePositions = new ArrayList<>() {{
            add(14);
            add(15);
            add(16);
            add(17);
            add(18);
        }};

        assertTrue(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));
        assertEquals(warehouse.getResources(paymentResourcePositions).size(), 0);

        //----------------

        paymentResourcePositions.clear();
        warehouse.reorderStrongBox();

        strongBoxResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.GRAY));
            add(new StorableResource(ResourcesEnum.GRAY));
        }};
        warehouse.addResourcesToStrongBox(strongBoxResources);

        discount.add(new StorableResource(ResourcesEnum.GRAY));
        discount.add(new StorableResource(ResourcesEnum.YELLOW));

        //correct resources for payment with discount
        paymentResourcePositions = new ArrayList<>() {{
            add(15);
            add(16);
            add(18);
        }};

        List<Resource> remainingResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.GRAY));
        }};

        assertTrue(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));
        assertEquals(warehouse.getResources(paymentResourcePositions).size(), 0);
        assertEquals(warehouse.takeResources(new ArrayList<>() {{
            add(14);
            add(17);
        }}), remainingResources);


        //----------------

        //insufficient resources
        assertFalse(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));

        //----------------

        List<Resource> incorrectResources = new ArrayList<>() {{
            add(new StorableResource(ResourcesEnum.YELLOW));
            add(new StorableResource(ResourcesEnum.PURPLE));
            add(new StorableResource(ResourcesEnum.BLUE));
        }};
        warehouse.reorderStrongBox();
        warehouse.addResourcesToStrongBox(incorrectResources);

        //incorrect resources for payment
        paymentResourcePositions = new ArrayList<>() {{
            add(14);
            add(15);
            add(16);
        }};

        //incorrect resources for payment with discount
        assertFalse(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));

    }
}