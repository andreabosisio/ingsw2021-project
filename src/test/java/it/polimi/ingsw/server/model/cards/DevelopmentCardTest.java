package it.polimi.ingsw.server.model.cards;


import it.polimi.ingsw.exceptions.EmptySlotException;
import it.polimi.ingsw.exceptions.InvalidIndexException;
import it.polimi.ingsw.exceptions.NonAccessibleSlotException;
import it.polimi.ingsw.server.model.ModelInterface;
import it.polimi.ingsw.server.model.enums.CardColorEnum;
import it.polimi.ingsw.server.model.enums.ResourceEnum;
import it.polimi.ingsw.server.model.player.Warehouse;
import it.polimi.ingsw.server.model.resources.StorableResource;
import it.polimi.ingsw.server.model.resources.RedResource;
import it.polimi.ingsw.server.model.resources.Resource;
import it.polimi.ingsw.server.model.turn.TurnLogic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardTest {

    CardsGenerator devCardGenerator = new CardsGenerator();
    ModelInterface modelInterface = new ModelInterface(new ArrayList<String>(){{
        add("Simone");
        add("Andrea");
    }});
    TurnLogic turnLogic = modelInterface.getTurnLogic();
    Warehouse warehouse = turnLogic.getCurrentPlayer().getPersonalBoard().getWarehouse();
    List<DevelopmentCard> devCards = devCardGenerator.generateDevelopmentCards();
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

        correctResources.add(new StorableResource(ResourceEnum.GRAY));
        correctResources.add(new StorableResource(ResourceEnum.GRAY));

        insufficientResources.add(new StorableResource(ResourceEnum.GRAY));

        incorrectResources.add(new StorableResource(ResourceEnum.PURPLE));
        incorrectResources.add(new StorableResource(ResourceEnum.GRAY));


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

        correctResources.add(new StorableResource(ResourceEnum.GRAY));
        correctResources.add(new StorableResource(ResourceEnum.BLUE));

        insufficientResources.add(new StorableResource(ResourceEnum.BLUE));

        incorrectResources.add(new StorableResource(ResourceEnum.PURPLE));
        incorrectResources.add(new StorableResource(ResourceEnum.BLUE));


        assertTrue(devCard.canDoProduction(correctResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(insufficientResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(incorrectResources) && !devCard.getInResources().isEmpty());

        //devCards.get(8).getPrice().remove(0);
    }

    @Test
    void getterTest(){
            devCard = devCards.get(19);
        List<Resource> correctPrice = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.BLUE));
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        List<Resource> correctInResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
        }};
        List<Resource> correctOutResources = new ArrayList<Resource>(){{
            add(new RedResource());
        }};
        assertEquals(correctPrice, devCard.getPrice());
        assertEquals(CardColorEnum.GREEN, devCard.getColor());
        assertEquals(1, devCard.getLevel());
        assertEquals(1, devCard.getPoints());
        assertTrue(devCard.getOutResources().equals(correctOutResources) && devCard.getInResources().equals(correctInResources));
    }

    @Test
    void usePowerTest(){
        devCard = devCards.get(9);
        List<Resource> correctOutResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new StorableResource((ResourceEnum.PURPLE)));
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

        List<Resource> strongBoxResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.GRAY));
            add(new StorableResource(ResourceEnum.GRAY));
        }};
        warehouse.addResourcesToStrongBox(strongBoxResources);

        discount = new ArrayList<>();

        //correct resources for payment
        paymentResourcePositions = new ArrayList<Integer>(){{
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

        strongBoxResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.GRAY));
            add(new StorableResource(ResourceEnum.GRAY));
        }};
        warehouse.addResourcesToStrongBox(strongBoxResources);

        discount.add(new StorableResource(ResourceEnum.GRAY));
        discount.add(new StorableResource(ResourceEnum.YELLOW));

        //correct resources for payment with discount
        paymentResourcePositions = new ArrayList<Integer>(){{
            add(15);
            add(16);
            add(18);
        }};

        List<Resource> remainingResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.GRAY));
        }};

        assertTrue(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));
        assertEquals(warehouse.getResources(paymentResourcePositions).size(), 0);
        assertEquals(warehouse.takeResources(new ArrayList<Integer>(){{
            add(14);
            add(17);
        }}), remainingResources);


        //----------------

        //insufficient resources
        assertFalse(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));

        //----------------

        List<Resource> incorrectResources = new ArrayList<Resource>(){{
            add(new StorableResource(ResourceEnum.YELLOW));
            add(new StorableResource(ResourceEnum.PURPLE));
            add(new StorableResource(ResourceEnum.BLUE));
        }};
        warehouse.reorderStrongBox();
        warehouse.addResourcesToStrongBox(incorrectResources);

        //incorrect resources for payment
        paymentResourcePositions = new ArrayList<Integer>(){{
            add(14);
            add(15);
            add(16);
        }};

        //incorrect resources for payment with discount
        assertFalse(devCard.buyCard(turnLogic.getCurrentPlayer(), paymentResourcePositions, discount));

    }


    //@Test
     /*void moreCanDoProductionTest() {
        ProductionCard devCard = createDevCard();
        List<Resource> plusResources = new ArrayList<>();
        List<Resource> emptyResources = new ArrayList<>();

        plusResources.add(new StorableResource(ResourceEnum.BLUE));
        plusResources.add(new StorableResource(ResourceEnum.PURPLE));
        plusResources.add(new StorableResource(ResourceEnum.GRAY));


        assertTrue(devCard.canDoProduction(plusResources) && !devCard.getInResources().isEmpty());
        assertTrue(!devCard.canDoProduction(emptyResources) && !devCard.getInResources().isEmpty());

    }*/

    /*
    /**
     * Verify if usePower() return NEW instances of the correct resources
     */
    /*
    @Test
     void usePowerTest() {
        ProductionCard devCard = createDevCard();
        assertTrue(!devCard.usePower().isEmpty() && devCard.usePower() != devCard.getOutResources() && devCard.usePower().equals(devCard.getOutResources()));
    }*/

    //@Test
    //void buyCardTest(){
    //    DevelopmentCard devCard = createDevCard();
    //    devCard.buyCard(new Player(), )
    //}


}